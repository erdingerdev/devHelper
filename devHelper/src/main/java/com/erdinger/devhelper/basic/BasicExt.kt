package com.erdinger.devhelper.basic

import android.content.Context
import android.view.View

fun dp2px(context: Context, dip: Float): Int {
    if (dip <= 0)return 0
    val scale: Float = context.resources.displayMetrics.density
    return (dip * scale + 0.5f).toInt()
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.safeAs(): T {
    return this as T
}

fun String?.orNull():String?{
    return if (this.isNullOrEmpty()) null else this
}

fun Int?.stringOrEmpty():String{
    return this?.toString() ?: ""
}

fun String?.ifNullOrEmpty(block:() -> String): String {
    return if (this.isNullOrEmpty()) block.invoke() else this
}

fun View.goneIfNullOrEmpty(content: String?) {
    if (content.isNullOrEmpty()) this.visibility = View.GONE else this.visibility = View.VISIBLE
}

fun View.invisibleIfNullOrEmpty(content: String?) {
    if (content.isNullOrEmpty()) this.visibility = View.INVISIBLE else this.visibility = View.VISIBLE
}

fun dip2px(view: View, dip: Float): Int {
    if (dip <= 0)return 0
    val scale: Float = view.context.resources.displayMetrics.density
    return (dip * scale + 0.5f).toInt()
}

fun dip2px(context: Context, dip: Float): Int {
    if (dip <= 0)return 0
    val scale: Float = context.resources.displayMetrics.density
    return (dip * scale + 0.5f).toInt()
}

fun View.show(){
    visibility = View.VISIBLE
}

fun View.hide(){
    visibility = View.INVISIBLE
}

fun View.gone(){
    visibility = View.GONE
}

/**
 * 为View设置防抖动点击事件监听器
 *
 * 防抖动机制用于防止在指定时间间隔内重复触发点击事件，当用户点击频率过快时，只有在最后一次点击后经过指定时间间隔
 * 没有再次点击时，才会执行实际的点击事件处理逻辑。这有助于避免由于快速连续点击导致的重复操作或状态不一致问题
 *
 * @param interval 防抖动的时间间隔，单位为毫秒，默认值为500毫秒这个参数决定了在两次点击之间必须经过多长时间才能再次触发点击事件
 * @param block 点击事件的处理逻辑，当满足防抖动时间间隔时执行这个参数是一个lambda表达式，接收被点击的View实例作为参数
 */
fun View.setOnDebouncedClickListener(interval: Long = 500L, block: (View) -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > interval) {
                block.invoke(v)
            }
            lastClickTime = currentTime
        }
    })
}