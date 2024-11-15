package com.erdinger.devhelper.recycler

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.chad.library.adapter4.QuickAdapterHelper
import com.erdinger.devhelper.R
import com.erdinger.devhelper.basic.dp2px

class HorizontalItemDecoration(private val horizontalHeight:Float,
                               private val leftRightSpec:Float = 0F,
                               private val color:Int = android.R.color.transparent,
                               private var alsoShowAtHead: Boolean = false): ItemDecoration() {

    private val paint = Paint()

    fun setAlsoShowAtHead(alsoShowAtHead: Boolean){
        this.alsoShowAtHead = alsoShowAtHead
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.adapter == null)return
        if (alsoShowAtHead){
            val position = parent.getChildAdapterPosition(view)
            if (position != parent.adapter!!.itemCount - 1) {
                outRect.bottom = dp2px(view.context,horizontalHeight)
            }else {
                outRect.bottom = 0
            }
            return
        }

        val bindingAdapter = parent.getChildViewHolder(view).bindingAdapter
        //内容部分最后一行底部不填充
        /**
         * 这个是当前 item对应的adapter 的item个数，使用 比如
         *
         * 该列表是
         * headAdapter 1个元素
         * headAdapter 1个元素
         * contentAdapter 3个元素
         * footerAdapter 2个元素
         *
         * 对应的count为
         *  val position = parent.getChildAdapterPosition(view)
         *  position = 0 -> count = 1
         *  position = 1 -> count = 1
         *  position = 2 -> count = 3
         *  position = 3 -> count = 3
         *  position = 4 -> count = 3
         *  position = 5 -> count = 2
         *  position = 6 -> count = 2
         *
         */
        val currentItemCount = bindingAdapter!!.itemCount
        var headCount = 0
        val helper = parent.getTag(R.id.tag_helper)
        if (parent.adapter is ConcatAdapter && helper is QuickAdapterHelper){
            headCount = helper.beforeAdapterList.size
        }

        val position = parent.getChildAdapterPosition(view)
        //头部列表 和 尾部列表 以及最后一行 不设置
        if (position < headCount || position >= currentItemCount + headCount - 1){
            outRect.bottom = 0
        }else {
            outRect.bottom = dp2px(view.context,horizontalHeight)
        }

    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (parent.adapter == null)return
        paint.color = ContextCompat.getColor(parent.context,color)
        //获取item的数量
        val count = parent.childCount
        //设置要绘制的矩形的left、right、top、bottom值
        val left = parent.paddingLeft.toFloat() + dp2px(parent.context,leftRightSpec)
        val right = parent.width - parent.paddingRight - dp2px(parent.context,leftRightSpec)
        val itemCount = parent.adapter!!.itemCount

        for (i in 0 until count){
            val child = parent.getChildAt(i)
            val top = child.bottom.toFloat()
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams

            if (alsoShowAtHead){
                val position = layoutParams.absoluteAdapterPosition
                val  bottom = if (position != itemCount - 1) {
                    (child.bottom + dp2px(parent.context, horizontalHeight)).toFloat()
                }else {
                    child.bottom.toFloat()
                }
                c.drawRect(left, top,right.toFloat(), bottom,paint)
            }else {

                val bindingAdapter = parent.getChildViewHolder(child).bindingAdapter
                val currentItemCount = bindingAdapter!!.itemCount
                var headCount = 0
                val helper = parent.getTag(R.id.tag_helper)
                if (parent.adapter is ConcatAdapter && helper is QuickAdapterHelper){
                    headCount = helper.beforeAdapterList.size
                }

                val position = layoutParams.absoluteAdapterPosition
                //头部列表 和 衔接处 和 尾部列表 以及最后一行 不设置
                val bottom = if (position < headCount || position >= currentItemCount + headCount - 1){
                    child.bottom.toFloat()
                }else {
                    (child.bottom + dp2px(parent.context, horizontalHeight)).toFloat()
                }

                c.drawRect(left, top,right.toFloat(), bottom,paint)
            }
        }
    }


}