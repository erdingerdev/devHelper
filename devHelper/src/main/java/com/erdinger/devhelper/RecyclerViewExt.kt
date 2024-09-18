package com.erdinger.devhelper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.linear(): RecyclerView{
    layoutManager = LinearLayoutManager(context)
    return this
}

fun RecyclerView.horizon(): RecyclerView{
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    return this
}