package com.erdinger.devhelper.recycler

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseSingleItemAdapter
import com.erdinger.devhelper.basic.dp2px


class TextViewAdapter(item: String = "", private var height: Float = 35F, private var bg: Int? = null): BaseSingleItemAdapter<String, TextViewAdapter.VH>(item) {

    init {
        this.item = item
    }

    class VH (val text: TextView ): RecyclerView.ViewHolder(text)

    override fun onBindViewHolder(holder: VH, item: String?) {
        holder.text.text = item.orEmpty()
        holder.text.height = dp2px(context, height)
        bg?.apply { holder.text.setBackgroundResource(this) }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        val text = TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
            setTextColor(Color.parseColor("#999999"))
            gravity = Gravity.CENTER
        }
        return VH(text)
    }

}