package com.nlc.gesturesnap.screen.capture.ui.component

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
import com.nlc.gesturesnap.screen.capture.model.GestureDetectOption
import com.nlc.gesturesnap.screen.capture.ui.listener.ItemClickListener
import com.nlc.gesturesnap.screen.capture.ui.value.GestureRecyclerViewItemColor
import kotlinx.coroutines.*

class GestureDetectAdapter(
    private val recyclerView: RecyclerView,
    private val mContext: Context,
    private var mOptions: List<GestureDetectOption>,
    private val callback: ItemClickListener
) : RecyclerView.Adapter<GestureDetectAdapter.ViewHolder>() {

    private var adapterSize = mOptions.count() + 2

    private var itemRotationValue: Int = 0
    private var oldSelectedIndex: Int = 1

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

                    scrollToMiddleAndSelect(targetPosition)
                }
            }
        })

        moveFirstItemToCenter()
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
                scrollToMiddleAndSelect(if(position == 0) position + 1 else position - 1)
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
            scrollToMiddleAndSelect(position)
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

    private fun moveFirstItemToCenter(){
        CoroutineScope(Dispatchers.Main).launch {
            checkReadyToScroll().await()
            scrollToMiddleAndSelect(1)
        }
    }

    private fun checkReadyToScroll() = CoroutineScope(Dispatchers.Main).async {

        var maxTime = 5000L
        val delay = 50L

        while (maxTime > 0) {
            val layoutManager = recyclerView.layoutManager
            if (layoutManager !is LinearLayoutManager) {
                return@async
            }

            if(layoutManager.findViewByPosition(oldSelectedIndex) != null){
                return@async
            }

            maxTime -= delay

            delay(delay)
        }
    }

    fun scrollToMiddleAndSelect(position: Int) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {

            // move exactly to the middle of the recycle view

            val viewCenter = recyclerView.width / 2
            val targetView = layoutManager.findViewByPosition(position)
            targetView?.let {
                val viewCenterOffset = targetView.width / 2
                recyclerView.smoothScrollBy(targetView.left - viewCenter + viewCenterOffset, 0)
            }

            selectItem(position - 1)
        }
    }

    private fun selectItem(position : Int){
        callback.onItemClicked(position)
    }
}