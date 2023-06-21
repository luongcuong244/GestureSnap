package com.nlc.gesturesnap.screen.capture.ui.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.nlc.gesturesnap.R
import com.nlc.gesturesnap.screen.capture.model.GestureDetectOption
import com.nlc.gesturesnap.screen.capture.ui.value.GestureRecyclerViewItemColor
import com.nlc.gesturesnap.screen.capture.ui.listener.ItemClickListener


class GestureDetectAdapter(
    private val mContext: Context,
    private var mOptions: List<GestureDetectOption>,
    private val callback: ItemClickListener
) : RecyclerView.Adapter<GestureDetectAdapter.ViewHolder>() {

    private var oldSelectedIndex: Int = 0

    fun updateItem(option: GestureDetectOption){
        val newSelectedIndex = mOptions.indexOfFirst {
            it.gestureCategory == option.gestureCategory
        }

        mOptions[oldSelectedIndex].isSelecting = false
        mOptions[newSelectedIndex].isSelecting = true

        notifyItemChanged(newSelectedIndex)
        notifyItemChanged(oldSelectedIndex)

        oldSelectedIndex = newSelectedIndex
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        val heroView: View = inflater.inflate(R.layout.gesture_detect_recycler_item, parent, false)
        return ViewHolder(heroView)
    }

    override fun getItemCount(): Int {
        return mOptions.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option: GestureDetectOption = mOptions.get(position)

        holder.mText.text = option.text

        if(option.isSelecting){
            holder.mText.setTextColor(GestureRecyclerViewItemColor.ACTIVE_COLOR.value)
            holder.mIcon.setImageDrawable(option.activeIcon)
        } else {
            holder.mText.setTextColor(GestureRecyclerViewItemColor.INACTIVE_COLOR.value)
            holder.mIcon.setImageDrawable(option.inactiveIcon)
        }

        holder.mRootView.setOnClickListener {
            callback.onItemClicked(position)
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
}