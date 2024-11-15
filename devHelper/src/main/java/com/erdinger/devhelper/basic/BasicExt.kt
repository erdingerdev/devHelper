package com.erdinger.devhelper.basic

import android.content.Context

fun dp2px(context: Context, dip: Float): Int {
    if (dip <= 0)return 0
    val scale: Float = context.resources.displayMetrics.density
    return (dip * scale + 0.5f).toInt()
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.safeAs(): T {
    return this as T
}