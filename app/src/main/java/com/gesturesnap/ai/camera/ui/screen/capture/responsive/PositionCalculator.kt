package com.gesturesnap.ai.camera.ui.screen.capture.responsive

import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.databinding.ActivityCaptureBinding

class PositionCalculator(private val context: Context, private val binding: ActivityCaptureBinding) {

    private var notchHeight : Int? = null

    // cần sắp xếp:
    // camera_preview có chiều cao là HCP
    // gesture_recycle_view có chiều cao là HGRV
    // option_bar có chiều cao là HOB
    // hand_progress có chiều cao là HHP

    // Một giá trị margin giữa 2 thành phần nào đó: margin

    // Vị trí capture_button_container ở dưới cùng màn hình là cố định

    /* Giả sử chiều cao màn hình là HS
     - đầu tiên: Tính xem camera_preview cao bao nhiêu: giả sử là HCP
     - Cũng cần phải tính xem capture_button_container cao bao nhiêu: giả sử là HCBC
     - Vậy thì phần trống cho những view còn lại là: HS - HCP - HCBC = HRM ( height remaining )

     - Tính chiều cao của tai thỏ ( notch ): HN
     - Nếu HS - HN - HCP - ( HOB * 2 ) > 0: margin top 1 khoảng bằng tai thỏ

     - Nếu:
            + HRM < 0: lúc này camera_preview phủ gần hết màn hình
                => constraint:
                       camera_preview gắn với top của màn hình
                       gesture_recycle_view gắn với top của màn hình ( bên trong camera_preview )
                       option_bar nằm trên capture_button_container
                       hand_progress nằm trên option_bar
            + HRM <= HOB + 2 * margin
                => constraint:
                       camera_preview gắn với top của màn hình
                       gesture_recycle_view gắn với top của màn hình ( bên trong camera_preview )
                       option_bar gắn với bottom của camera_preview
                       hand_progress nằm trên option_bar
            + Nếu không thì:
                option_bar nằm trên capture_button_container
                hand_progress gắn với bottom của camera_preview

                Tính khoảng trống còn lại: HRM = HRM - ( HOB + 2 * margin )
                Nếu:
                    + HRM < HGRV + 2 * gesture_recycle_view_margin: quá ít để có thể hiển thị gesture_recycle_view bên ngoài camera_preview
                        => constraint:
                            gesture_recycle_view gắn với top của camera_preview
                            camera_preview gắn với top của màn hình
                    + else:
                        => constraint:
                            gesture_recycle_view gắn với top của màn hình
                            camera_preview gắn với top của option_bar và bottom của gesture_recycle_view
     */

    fun calculateViewsPosition(){

        val constraintLayout : ConstraintLayout = binding.root.findViewById(R.id.capture_screen_root_view)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        val marginValue = context.resources.getDimension(R.dimen.small_padding).toInt()
        val gestureRecycleViewMarginValue = context.resources.getDimension(R.dimen.gesture_recycle_view_margin).toInt()

        val deviceHeight = context.resources.displayMetrics.heightPixels

        val cameraPreviewHeight = binding.fragmentContainer.height
        val optionBarHeight = binding.optionBarContainer.height
        val captureButtonToBottom = getDistanceToBottomById(binding.captureButton)
        val gestureRecycleViewHeight = binding.recycleViewContainer.height

        val notchHeight = getNotchHeight()

        val shouldOccupyNotchSpace =
            deviceHeight - notchHeight - cameraPreviewHeight - captureButtonToBottom - (2 * marginValue + optionBarHeight) <= 0

        val notchMargin = if(shouldOccupyNotchSpace)
            0
        else notchHeight

        setPadding(
            binding.captureScreenRootView,
            top = notchMargin
        )

        var heightRemaining =
            deviceHeight - cameraPreviewHeight - captureButtonToBottom - notchMargin

        if(heightRemaining < 0){
            Log.d(TAG, "TH1")
            // camera preview
            constraintSet.connect(R.id.fragment_container, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            // gesture_recycle_view
            constraintSet.connect(R.id.recycle_view_container, ConstraintSet.TOP, R.id.fragment_container, ConstraintSet.TOP)
            // option_bar
            constraintSet.connect(R.id.option_bar_container, ConstraintSet.BOTTOM, R.id.capture_button, ConstraintSet.TOP)
            // hand_progress
            constraintSet.connect(R.id.hand_gesture_progress_container, ConstraintSet.BOTTOM, R.id.option_bar_container, ConstraintSet.TOP)
        } else if (heightRemaining < optionBarHeight + 2 * marginValue) {
            Log.d(TAG, "TH2")
            // camera preview
            constraintSet.connect(R.id.fragment_container, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            // gesture_recycle_view
            constraintSet.connect(R.id.recycle_view_container, ConstraintSet.TOP, R.id.fragment_container, ConstraintSet.TOP)
            // option_bar
            constraintSet.connect(R.id.option_bar_container, ConstraintSet.BOTTOM, R.id.fragment_container, ConstraintSet.BOTTOM)
            // hand_progress
            constraintSet.connect(R.id.hand_gesture_progress_container, ConstraintSet.BOTTOM, R.id.option_bar_container, ConstraintSet.TOP)
        } else {
            Log.d(TAG, "TH3")
            // option_bar
            constraintSet.connect(R.id.option_bar_container, ConstraintSet.BOTTOM, R.id.capture_button, ConstraintSet.TOP)
            // hand_progress
            constraintSet.connect(R.id.hand_gesture_progress_container, ConstraintSet.BOTTOM, R.id.fragment_container, ConstraintSet.BOTTOM)

            heightRemaining -= (optionBarHeight + 2 * marginValue)

            if(heightRemaining < gestureRecycleViewHeight + 2 * gestureRecycleViewMarginValue){
                // camera preview
                constraintSet.connect(R.id.fragment_container, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.connect(R.id.fragment_container, ConstraintSet.BOTTOM, R.id.option_bar_container, ConstraintSet.TOP)
                // gesture_recycle_view
                constraintSet.connect(R.id.recycle_view_container, ConstraintSet.TOP, R.id.fragment_container, ConstraintSet.TOP)
            } else {
                // camera preview
                constraintSet.connect(R.id.fragment_container, ConstraintSet.BOTTOM, R.id.option_bar_container, ConstraintSet.TOP)
                constraintSet.connect(R.id.fragment_container, ConstraintSet.TOP, R.id.recycle_view_container, ConstraintSet.BOTTOM)
                // gesture_recycle_view
                constraintSet.connect(R.id.recycle_view_container, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            }
        }

        constraintSet.applyTo(constraintLayout)

        setMargin(binding.optionBarContainer, bottom = marginValue)
        setMargin(binding.recycleViewContainer, top = gestureRecycleViewMarginValue, bottom = gestureRecycleViewMarginValue)
        setMargin(binding.handGestureProgressContainer, bottom = marginValue)
    }

    private fun getNotchHeight(): Int {

        if(notchHeight != null){
            return notchHeight ?: 0
        }

        notchHeight = if(SDK_INT >= Build.VERSION_CODES.P){
            val displayCutout = binding.captureActivity?.window?.decorView?.rootWindowInsets?.displayCutout
            if((displayCutout?.boundingRects?.size ?: 0) > 0){
                displayCutout?.boundingRects?.get(0)?.height()
            } else
                0
        } else {
            0
        }
        return notchHeight ?: 0
    }

    private fun setMargin(view: View, left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null){
        val marginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        marginLayoutParams.setMargins(
            left ?: marginLayoutParams.leftMargin,
            top ?: marginLayoutParams.topMargin,
            right ?: marginLayoutParams.rightMargin,
            bottom ?: marginLayoutParams.bottomMargin
        )
    }

    private fun setPadding(view: View, left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0){
        view.setPadding(left, top, right, bottom)
    }

    private fun getDistanceToBottomById(view: View) : Int{

        val deviceHeight = context.resources.displayMetrics.heightPixels

        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)
        val viewBottom = viewLocation[1]
        return deviceHeight - viewBottom
    }

    companion object {
        private const val TAG = "PositionCalculator"
    }
}