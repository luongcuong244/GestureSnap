package com.nlc.gesturesnap.ui.screen.capture.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.flexbox.FlexboxLayout
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.helper.AppConstant
import com.nlc.gesturesnap.helper.LocalStorageHelper
import com.nlc.gesturesnap.model.GestureDetectOption
import com.nlc.gesturesnap.model.enums.GestureRecyclerViewItemColor
import kotlinx.coroutines.*

class GestureDetectAdapter(
    private val recyclerView: RecyclerView,
    private val mContext: Context,
    private var mOptions: List<GestureDetectOption>,
    private val initPosition: Int,
    private val callback: (Int) -> Unit
) : RecyclerView.Adapter<GestureDetectAdapter.ViewHolder>() {

    private var adapterSize = mOptions.count() + 2

    private var itemRotationValue: Int = 0
    private var oldSelectedIndex: Int = initPosition

    companion object {
        private const val TAG = "GestureDetectAdapter"
    }

    init {

        val deviceWidth = mContext.resources.displayMetrics.widthPixels

        recyclerView.addItemDecoration(RecyclerViewPaddingItemDecoration(deviceWidth / 2))

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val layoutManager = recyclerView.layoutManager

                if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager is LinearLayoutManager) {
                    val firstVisibleItemPosition =
                        layoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition =
                        layoutManager.findLastVisibleItemPosition()

                    var targetPosition = (firstVisibleItemPosition + lastVisibleItemPosition) / 2

                    if(firstVisibleItemPosition == 0 || lastVisibleItemPosition == adapterSize - 1){
                        for(i in firstVisibleItemPosition + 1 until lastVisibleItemPosition){
                            val viewCenter = recyclerView.width / 2
                            val targetView = layoutManager.findViewByPosition(i) ?: continue

                            if(targetView.left <= viewCenter && targetView.right >= viewCenter ){
                                targetPosition = i
                                break
                            }
                        }
                    }

                    scrollToMiddleAndSelectAndSavePosition(targetPosition)
                }
            }
        })

        moveInitPositionToCenter()
    }

    private fun moveInitPositionToCenter(){
        CoroutineScope(Dispatchers.Main).launch {
            checkReadyToScroll().await()

            val targetPosition = initPosition + 1  // +1 because the first element is empty element

            // when moving to element i, element i + 1 will appear
            for(i in 1 .. targetPosition){
                scrollToMiddleAndSelectAndSavePosition(i, false)
            }
        }
    }

    private fun checkReadyToScroll() = CoroutineScope(Dispatchers.IO).async {

        var maxTime = 5000L
        val delay = 50L

        while (maxTime > 0) {
            val layoutManager = recyclerView.layoutManager
            if (layoutManager !is LinearLayoutManager) {
                return@async
            }

            if(layoutManager.findViewByPosition(0) != null){
                return@async
            }

            maxTime -= delay

            delay(delay)
        }
    }

    fun updateItem(option: GestureDetectOption){
        val newSelectedIndex = mOptions.indexOfFirst {
            it.gestureCategory == option.gestureCategory
        }

        if(newSelectedIndex == -1){
            return
        }

        mOptions[oldSelectedIndex].isSelecting = false
        mOptions[newSelectedIndex].isSelecting = true

        notifyItemChanged(newSelectedIndex + 1)
        notifyItemChanged(oldSelectedIndex + 1)

        oldSelectedIndex = newSelectedIndex
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItemRotationValue(value : Int){
        itemRotationValue = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val heroView: View = inflater.inflate(R.layout.gesture_detect_recycler_item, parent, false)
        return ViewHolder(heroView)
    }

    override fun getItemCount(): Int {
        return adapterSize
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(position == 0 || position == adapterSize - 1){
            holder.mRootView.alpha = 0F
            holder.mRootView.setOnClickListener {
                scrollToMiddleAndSelectAndSavePosition(if(position == 0) position + 1 else position - 1)
            }
            return
        }

        holder.mRootView.alpha = 1F

        val option: GestureDetectOption = mOptions[position - 1]

        holder.mText.text = option.text

        if(option.isSelecting){
            holder.mText.setTextColor(GestureRecyclerViewItemColor.ACTIVE_COLOR.value)
            holder.mIcon.setImageDrawable(option.activeIcon)
        } else {
            holder.mText.setTextColor(GestureRecyclerViewItemColor.INACTIVE_COLOR.value)
            holder.mIcon.setImageDrawable(option.inactiveIcon)
        }

        holder.mRootView.rotation = itemRotationValue.toFloat()

        holder.mRootView.setOnClickListener {
            scrollToMiddleAndSelectAndSavePosition(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIcon: ImageView
        val mText: TextView
        val mRootView: FlexboxLayout

        init {
            this.mIcon = itemView.findViewById(R.id.hand_action_icon)
            this.mText = itemView.findViewById(R.id.hand_action_text)
            this.mRootView = itemView.findViewById(R.id.hand_action_root_view)
        }
    }

    class RecyclerViewPaddingItemDecoration(
        private val padding: Int,
    ) :
        ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.left = padding
            }
            if (position == parent.adapter!!.itemCount - 1) {
                outRect.right = padding
            }
        }
    }

    fun scrollToMiddleAndSelectAndSavePosition(position: Int, moveWithAnimation: Boolean = true) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {

            // move exactly to the middle of the recycle view

            val viewCenter = recyclerView.width / 2
            val targetView = layoutManager.findViewByPosition(position)
            targetView?.let {
                val viewCenterOffset = targetView.width / 2
                if(moveWithAnimation){
                    recyclerView.smoothScrollBy(targetView.left - viewCenter + viewCenterOffset, 0)
                } else {
                    recyclerView.scrollBy(targetView.left - viewCenter + viewCenterOffset, 0)
                }
            }

            val indexInListGestures = position - 1

            selectItem(indexInListGestures)
            saveIndex(indexInListGestures)
        }
    }

    private fun selectItem(position : Int){
        callback(position)
    }

    private fun saveIndex(index: Int){
        LocalStorageHelper.writeData(
            mContext,
            AppConstant.GESTURE_OPTION_INDEX_KEY,
            index
        )
    }
}