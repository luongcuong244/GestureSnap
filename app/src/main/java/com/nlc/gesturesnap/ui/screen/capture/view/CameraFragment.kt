package com.nlc.gesturesnap.ui.screen.capture.view

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener.ORIENTATION_UNKNOWN
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.databinding.FragmentCameraBinding
import com.nlc.gesturesnap.extension.flipHorizontally
import com.nlc.gesturesnap.extension.rotate
import com.nlc.gesturesnap.helper.AppConstant
import com.nlc.gesturesnap.helper.Formatter
import com.nlc.gesturesnap.helper.GestureRecognizerHelper
import com.nlc.gesturesnap.helper.MediaHelper
import com.nlc.gesturesnap.helper.OrientationLiveData
import com.nlc.gesturesnap.helper.PermissionHelper
import com.nlc.gesturesnap.model.enums.GestureCategory
import com.nlc.gesturesnap.view_model.capture.CameraModeViewModel
import com.nlc.gesturesnap.view_model.capture.GestureDetectViewModel
import com.nlc.gesturesnap.view_model.capture.PermissionViewModel
import com.nlc.gesturesnap.view_model.capture.RecentPhotoViewModel
import com.nlc.gesturesnap.view_model.capture.TimerViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraFragment : Fragment(),
    GestureRecognizerHelper.GestureRecognizerListener {

    companion object {
        private const val TAG = "CameraFragment"
        private const val ANIMATION_FAST_MILLIS = 50L
    }

    private var _fragmentCameraBinding: FragmentCameraBinding? = null

    private val fragmentCameraBinding
        get() = _fragmentCameraBinding!!

    private var imageCapture: ImageCapture? = null

    private var gestureRecognizerHelper: GestureRecognizerHelper? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraSelector: CameraSelector? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var aspectRatio: Int? = null

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundExecutor: ExecutorService

    private lateinit var relativeOrientation: OrientationLiveData

    private val animationTask: Runnable by lazy {
        Runnable {
            // Flash white animation
            fragmentCameraBinding.screenFlashingOverlay.background = Color.argb(150, 255, 255, 255).toDrawable()
            // Wait for ANIMATION_FAST_MILLIS
            fragmentCameraBinding.screenFlashingOverlay.postDelayed({
                // Remove white flash animation
                fragmentCameraBinding.screenFlashingOverlay.background = null
            }, ANIMATION_FAST_MILLIS)
        }
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.

        // Start the GestureRecognizerHelper again when users come back
        // to the foreground.

        cameraProvider?.let {
            bindCameraUseCases(AppConstant.ANIMATION_DURATION_MILLIS.toLong())
        }

        backgroundExecutor.execute {
            if (gestureRecognizerHelper?.isClosed() == true) {
                gestureRecognizerHelper?.setupGestureRecognizer()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        cameraProvider?.unbindAll()

        if (gestureRecognizerHelper != null) {
            // Close the Gesture Recognizer helper and release resources
            backgroundExecutor.execute { gestureRecognizerHelper?.clearGestureRecognizer() }
        }
    }

    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()

        // Shut down our background executor
        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)

        fragmentCameraBinding.lifecycleOwner = this

        val gestureDetectViewModel = ViewModelProvider(requireActivity())[GestureDetectViewModel::class.java]
        fragmentCameraBinding.gestureDetectViewModel = gestureDetectViewModel

        val timerViewModel = ViewModelProvider(requireActivity())[TimerViewModel::class.java]
        timerViewModel.photoSavingTrigger.observe(requireActivity()) {
            takePhoto()
        }

        val cameraModeViewModel =
            ViewModelProvider(requireActivity())[CameraModeViewModel::class.java]

        fragmentCameraBinding.cameraModeViewModel = cameraModeViewModel

        cameraModeViewModel.cameraAspectRatio.observe(requireActivity()) {
            if(cameraProvider != null){
                bindCameraUseCases()
            }
        }

        cameraModeViewModel.cameraOrientation.observe(requireActivity()) {
            if(cameraProvider != null){
                bindCameraUseCases()
            }
        }

        cameraModeViewModel.flashOption.observe(requireActivity()) {
            if(cameraProvider != null){
                setFlashMode(it.value)
            }
        }

        gestureDetectViewModel.shouldRunHandTracking.observe(requireActivity()) {
            if(cameraProvider != null){
                if(it)
                    bindImageAnalyzer()
                else {
                    unbindImageAnalyzer()
                }
            }
        }

        relativeOrientation = OrientationLiveData(requireContext()).apply {
            observe(viewLifecycleOwner) { orientation ->
                Log.d(TAG, "Orientation changed: $orientation")
                if (orientation == ORIENTATION_UNKNOWN) {
                    return@observe
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                preview?.targetRotation = rotation
                imageCapture?.targetRotation = rotation
            }
        }

        return fragmentCameraBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize our background executor
        backgroundExecutor = Executors.newSingleThreadExecutor()

        // Wait for the views to be properly laid out
        fragmentCameraBinding.viewFinder.post {
            // Set up the camera and its use cases
            setUpCamera()
        }

        // Create the Hand Gesture Recognition Helper that will handle the
        // inference
        backgroundExecutor.execute {
            gestureRecognizerHelper = GestureRecognizerHelper(
                context = requireContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = GestureRecognizerHelper.DEFAULT_HAND_DETECTION_CONFIDENCE,
                minHandTrackingConfidence = GestureRecognizerHelper.DEFAULT_HAND_TRACKING_CONFIDENCE,
                minHandPresenceConfidence = GestureRecognizerHelper.DEFAULT_HAND_PRESENCE_CONFIDENCE,
                currentDelegate = GestureRecognizerHelper.DELEGATE_CPU,
                gestureRecognizerListener = this
            )
        }
    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError", "RestrictedApi")
    private fun bindCameraUseCases(delay: Long = 0L) {

        if(activity == null){
            return
        }

        val cameraModeViewModel = ViewModelProvider(requireActivity())[CameraModeViewModel::class.java]

        aspectRatio = cameraModeViewModel.cameraAspectRatio.value ?: AspectRatio.RATIO_4_3

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraOrientation = cameraModeViewModel.cameraOrientation.value ?: CameraSelector.LENS_FACING_BACK

        cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraOrientation).build()

        val targetRotation = Formatter.orientationToSurfaceRotation(relativeOrientation.value ?: 0)

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = Preview.Builder()
            .setTargetAspectRatio(aspectRatio!!)
            .setTargetRotation(targetRotation)
            .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(aspectRatio!!)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        recognizeHand(image)
                    }
                }

        val flashMode = cameraModeViewModel.flashOption.value?.value ?: ImageCapture.FLASH_MODE_OFF

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(aspectRatio!!)
            .setTargetRotation(targetRotation)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .build()


        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(
            {
                // Must unbind the use-cases before rebinding them
                cameraProvider.unbindAll()
                try {
                    // A variable number of use-cases can be passed here -
                    // camera provides access to CameraControl & CameraInfo
                    cameraProvider.bindToLifecycle(
                        viewLifecycleOwner, cameraSelector!!, preview, imageAnalyzer, imageCapture
                    )

                    // Attach the viewfinder's surface provider to preview use case
                    preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                } finally {
                    cameraModeViewModel.setShouldRefreshCamera(true)
                }
            },
            delay
        )
    }

    private fun setFlashMode(flashMode: Int) : Boolean{
        if(cameraProvider == null || cameraSelector == null || aspectRatio == null){
            return false
        }

        val targetRotation = Formatter.orientationToSurfaceRotation(relativeOrientation.value ?: 0)

        // rebind imageCapture

        cameraProvider?.unbind(imageCapture)

        imageCapture = ImageCapture.Builder()
            .setTargetAspectRatio(aspectRatio!!)
            .setTargetRotation(targetRotation)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .build()

        cameraProvider?.bindToLifecycle(
            viewLifecycleOwner, cameraSelector!!, imageCapture
        )

        return true
    }

    private fun bindImageAnalyzer(){
        if(imageAnalyzer == null){
            return
        }

        unbindImageAnalyzer()

        cameraProvider?.bindToLifecycle(
            viewLifecycleOwner, cameraSelector!!, imageAnalyzer
        )
    }

    private fun unbindImageAnalyzer(){
        if(imageAnalyzer == null){
            return
        }
        cameraProvider?.unbind(imageAnalyzer)
    }

    private fun recognizeHand(imageProxy: ImageProxy) {
        gestureRecognizerHelper?.recognizeLiveStream(
            imageProxy = imageProxy,
            isFontCamera =
                fragmentCameraBinding
                    .cameraModeViewModel?.cameraOrientation?.value == (CameraSelector.LENS_FACING_FRONT
                ?: true),
            deviceRotation = relativeOrientation.value ?: 0
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            fragmentCameraBinding.viewFinder.display.rotation
    }

    // Update UI after a hand gesture has been recognized. Extracts original
    // image height/width to scale and place the landmarks properly through
    // OverlayView. Only one result is expected at a time. If two or more
    // hands are seen in the camera frame, only one will be processed.
    override fun onResults(
        resultBundle: GestureRecognizerHelper.ResultBundle
    ) {
        activity?.runOnUiThread {
            if (_fragmentCameraBinding != null) {

                if(fragmentCameraBinding.gestureDetectViewModel?.shouldRunHandTracking?.value == false){
                    fragmentCameraBinding.overlay.clear()
                    return@runOnUiThread
                }

                // Show result of recognized gesture
                val gestureCategories = resultBundle.results.first().gestures()

                val selectedCategory = fragmentCameraBinding.gestureDetectViewModel?.currentHandGesture?.value?.gestureCategory
                selectedCategory?.let {

                    var gestureCategoryName : String? = null

                    if(gestureCategories.isNotEmpty()){
                        gestureCategoryName = gestureCategories.first()[0].categoryName()
                    }

                    val isDetecting = fragmentCameraBinding.gestureDetectViewModel?.isDetecting() ?: true

                    if (gestureCategoryName != null
                        && gestureCategoryName != GestureCategory.NONE.stringValue
                        && it != GestureCategory.NONE
                        && (it.stringValue == gestureCategoryName || it == GestureCategory.ALl)
                    ) {
                        if(!isDetecting){
                            fragmentCameraBinding.gestureDetectViewModel?.setIsDetecting(true)
                            fragmentCameraBinding.gestureDetectViewModel?.startTimer()
                        }
                    } else if(isDetecting){
                        fragmentCameraBinding.gestureDetectViewModel?.setIsDetecting(false)
                        fragmentCameraBinding.gestureDetectViewModel?.cancelTimer()
                    }
                }

                // Pass necessary information to OverlayView for drawing on the canvas
                fragmentCameraBinding.overlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    resultBundle.deviceRotation,
                    RunningMode.LIVE_STREAM
                )

                // Force a redraw
                fragmentCameraBinding.overlay.invalidate()
            }
        }
    }

    override fun onError(error: String, errorCode: Int) {
        Log.d(TAG, error)
    }

    fun takePhoto() {

        if(!PermissionHelper.isCameraPermissionGranted(requireContext())){
            Toast.makeText(
                requireContext(),
                "Camera permission isn't granted",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if(!PermissionHelper.isReadExternalStoragePermissionGranted(requireContext())){
            val permissionViewModel =
                ViewModelProvider(requireActivity())[PermissionViewModel::class.java]
            permissionViewModel.setStoragePermissionDialogShowing(true)
            return
        }

        // Get a stable reference of the
        // modifiable image capture use case
        val imageCapture = imageCapture ?: return

        fragmentCameraBinding.viewFinder.post(animationTask)

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "Can't take a photo. Something went wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, exception.toString())
                    super.onError(exception)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    Log.d(TAG, "Capture Success")

                    val isFontCamera = fragmentCameraBinding
                        .cameraModeViewModel?.cameraOrientation?.value == (CameraSelector.LENS_FACING_FRONT
                        ?: true)

                    val rotationDegrees = image.imageInfo.rotationDegrees.toFloat()

                    var bitmap = image.toBitmap()
                        .rotate(rotationDegrees)

                    if(isFontCamera){
                        bitmap = bitmap.flipHorizontally()
                    }

                    val recentPhotoViewModel =
                        ViewModelProvider(requireActivity())[RecentPhotoViewModel::class.java]

                    recentPhotoViewModel.setRecentPhoto(bitmap)

                    // Create time-stamped output file to hold the image
                    val photoName = "IMG_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg"

                    val photoUri = MediaHelper.createPhotoUri(requireContext(), "Pictures", photoName)

                    MediaHelper.savePhoto(requireContext(), bitmap, photoUri)

                    image.close()
                }
            }
        )
    }
}