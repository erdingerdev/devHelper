package com.erdinger.devhelper.recycler

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseSingleItemAdapter

class ViewTempAdapter(private val view: View): BaseSingleItemAdapter<String, ViewTempAdapter.VH>() {

    init {
        this.item = ""
    }

    class VH (val view: View ): RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: VH, item: String?) {}

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(view)
    }

}