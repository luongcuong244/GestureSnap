package com.nlc.gesturesnap.screen.capture.ui

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.databinding.ActivityCaptureBinding
import com.nlc.gesturesnap.helper.MediaHelper
import com.nlc.gesturesnap.helper.OrientationLiveData
import com.nlc.gesturesnap.helper.PermissionHelper
import com.nlc.gesturesnap.screen.capture.animation.AnimationHandler
import com.nlc.gesturesnap.screen.capture.ui.component.GestureDetectAdapter
import com.nlc.gesturesnap.screen.capture.ui.listener.ItemClickListener
import com.nlc.gesturesnap.screen.capture.ui.value.CameraOption
import com.nlc.gesturesnap.screen.capture.ui.view.CameraFragment
import com.nlc.gesturesnap.screen.capture.view_model.*

class CaptureActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CaptureActivity"
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                binding.permissionViewModel?.setCameraPermissionGranted(true)
            } else {
                binding.permissionViewModel?.setCameraPermissionTipDialogShowing(true)
            }
        }

    private val requestExternalPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                binding.permissionViewModel?.setStoragePermissionGranted(true)
            } else {
                binding.permissionViewModel?.setStoragePermissionTipDialogShowing(true)
            }
        }

    private var _binding: ActivityCaptureBinding? = null
    
    private val binding get() = _binding!!

    private var gestureDetectAdapter : GestureDetectAdapter? = null

    private lateinit var animationHandler: AnimationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(
            this, R.layout.activity_capture)

        animationHandler = AnimationHandler(this, binding)

        binding.lifecycleOwner = this

        val cameraFragment = CameraFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, cameraFragment)
            .commit()

        binding.cameraFragment = cameraFragment

        binding.captureActivity = this

        binding.screenRotation = OrientationLiveData(this).apply {
            observe(this@CaptureActivity) { orientation ->
                gestureDetectAdapter?.setItemRotationValue(-orientation)
            }
        }

        setupPermissionViewModel()
        setupTimerViewModel()
        setupGestureDetectViewModel()
        setupCameraModeViewModel()
        setupRecentPhotoViewModel()
    }

    private fun setupPermissionViewModel(){
        val permissionViewModel =
            ViewModelProvider(this)[PermissionViewModel::class.java]

        permissionViewModel.isStoragePermissionGranted.observe(this) {
            if(it){
                val photoPath = MediaHelper.getLatestPhotoPath(this)
                photoPath?.let {
                    val recentPhotoViewModel =
                        ViewModelProvider(this)[RecentPhotoViewModel::class.java]

                    val bitmap = BitmapFactory.decodeFile(photoPath)

                    bitmap?.let {
                        recentPhotoViewModel.setRecentPhoto(bitmap)
                    }
                }
            }
        }

        permissionViewModel.setCameraPermissionDialogShowing(!PermissionHelper.isCameraPermissionGranted(this))
        permissionViewModel.setStoragePermissionDialogShowing(false)

        permissionViewModel.setCameraPermissionTipDialogShowing(false)
        permissionViewModel.setStoragePermissionTipDialogShowing(false)

        permissionViewModel.setCameraPermissionGranted(PermissionHelper.isCameraPermissionGranted(this))
        permissionViewModel.setStoragePermissionGranted(PermissionHelper.isExternalStoragePermissionGranted(this))

        binding.permissionViewModel = permissionViewModel
    }

    private fun setupTimerViewModel(){
        val timerViewModel =
            ViewModelProvider(this)[TimerViewModel::class.java]

        binding.timerViewModel = timerViewModel

        timerViewModel.timerOption.observe(this) {
            binding.timerButton.setImageDrawable(ContextCompat.getDrawable(this, it.icon))
        }
    }

    private fun setupGestureDetectViewModel() {
        val gestureDetectViewModel =
            ViewModelProvider(this)[GestureDetectViewModel::class.java]

        binding.gestureDetectViewModel = gestureDetectViewModel

        gestureDetectViewModel.handGestureOptions.observe(this) {

            val layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            gestureDetectAdapter = GestureDetectAdapter(binding.recyclerGestureDetect ,this, it, object : ItemClickListener {
                override fun onItemClicked(index: Int) {
                    gestureDetectViewModel.setCurrentHandGesture(index)
                }
            })

            binding.recyclerGestureDetect.adapter = gestureDetectAdapter
            binding.recyclerGestureDetect.layoutManager = layoutManager
        }

        gestureDetectViewModel.createOptionList(this)

        gestureDetectViewModel.currentHandGesture.observe(this) {
            gestureDetectAdapter?.updateItem(it)
        }

        gestureDetectViewModel.setCurrentHandGesture(0)

        gestureDetectViewModel.timerTrigger.observe(this) {
            binding.timerViewModel?.startTimer()
        }
    }

    private fun setupCameraModeViewModel(){
        val cameraModeViewModel =
            ViewModelProvider(this)[CameraModeViewModel::class.java]

        binding.cameraModeViewModel = cameraModeViewModel

        cameraModeViewModel.flashOption.observe(this) {
            binding.flashButton.setImageDrawable(ContextCompat.getDrawable(this, it.icon))
        }

        val deviceWidth = resources.displayMetrics.widthPixels

        cameraModeViewModel.cameraAspectRatio.observe(this) {

            try {
                var layoutParams : FrameLayout.LayoutParams? = null

                val constraintLayout : ConstraintLayout = findViewById(R.id.capture_screen_root_view)
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)

                val params = binding.handGestureProgressContainer.layoutParams as ConstraintLayout.LayoutParams

                when(it){
                    AspectRatio.RATIO_4_3 -> {
                        layoutParams = FrameLayout.LayoutParams(deviceWidth, deviceWidth * 4 / 3)

                        constraintSet.connect(R.id.hand_gesture_progress_container, ConstraintSet.BOTTOM, R.id.fragment_container, ConstraintSet.BOTTOM)
                        constraintSet.applyTo(constraintLayout)

                        params.bottomMargin = resources.getDimension(R.dimen.small_padding).toInt()
                    }
                    AspectRatio.RATIO_16_9 -> {
                        layoutParams = FrameLayout.LayoutParams(deviceWidth, deviceWidth * 16 / 9)

                        constraintSet.connect(R.id.hand_gesture_progress_container, ConstraintSet.BOTTOM, R.id.option_bar_container, ConstraintSet.TOP)
                        constraintSet.applyTo(constraintLayout)

                        params.bottomMargin = resources.getDimension(R.dimen.large_padding).toInt()
                    }
                }

                if(layoutParams != null){
                    binding.fragmentContainer.layoutParams = layoutParams
                }

                binding.handGestureProgressContainer.layoutParams = params
            } catch (e : java.lang.Exception){
                Log.d(TAG, e.toString())
            }
        }
    }

    private fun setupRecentPhotoViewModel(){
        val recentPhotoViewModel =
            ViewModelProvider(this)[RecentPhotoViewModel::class.java]

        recentPhotoViewModel.recentPhoto.observe(this) {
            binding.recentPhotoButton.setImageBitmap(it)
        }
    }

    fun closePermissionDialogAndRequestCameraPermission(){
        binding.permissionViewModel?.setCameraPermissionDialogShowing(false)

        requestCameraPermissionLauncher.launch(
            Manifest.permission.CAMERA
        )
    }

    fun closePermissionDialogAndRequestStoragePermission(){
        binding.permissionViewModel?.setStoragePermissionDialogShowing(false)

        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

        requestExternalPermissionLauncher.launch(
            storagePermission
        )
    }

    fun closeDialogAndOpenAppInfo(){
        binding.permissionViewModel?.setCameraPermissionTipDialogShowing(false)
        binding.permissionViewModel?.setStoragePermissionTipDialogShowing(false)

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    fun switchToGalleryActivity(){
        if(!PermissionHelper.isExternalStoragePermissionGranted(this)){
            binding.permissionViewModel?.setStoragePermissionDialogShowing(true)
            return
        }

        // navigate to gallery activity
    }

    fun showMenuBar(cameraOption: CameraOption){
        animationHandler.showMenuBar(cameraOption)
    }

    override fun onResume() {
        super.onResume()
        binding.permissionViewModel?.setCameraPermissionGranted(PermissionHelper.isCameraPermissionGranted(this))
        binding.permissionViewModel?.setStoragePermissionGranted(PermissionHelper.isExternalStoragePermissionGranted(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}