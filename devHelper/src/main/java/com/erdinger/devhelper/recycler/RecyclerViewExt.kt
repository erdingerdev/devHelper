package com.erdinger.devhelper.recycler

/**
 * 创建列表的快捷方式，依赖一下库 如有冲突，可自行处理
 * io.github.cymchad:BaseRecyclerViewAdapterHelper4:4.1.4
 *
 * @link https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 *
 * @author erdinger 耳钉哥
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseMultiItemAdapter
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.util.addOnDebouncedChildClick
import com.chad.library.adapter4.util.setOnDebouncedItemClick
import com.erdinger.devhelper.R
import java.lang.ClassCastException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType

private fun getTagId(): Int {
    return R.id.tag_helper
}

private fun RecyclerView.getHelperByTag(): QuickAdapterHelper {
    val helper: QuickAdapterHelper
    val tag = getTag(getTagId())
    if (tag == null) {
        helper = QuickAdapterHelper.Builder(adapter as BaseQuickAdapter<*, *>).build()
        setTag(getTagId(), helper)
        adapter = helper.adapter
    } else {
        //已有标签 说明已经添加过头部或者尾部 不再设置
        helper = tag as QuickAdapterHelper
    }
    return helper
}

/**
 * 垂直排列
 */
fun RecyclerView.linear(): RecyclerView {
    layoutManager = LinearLayoutManager(context)
    return this
}

/**
 * 水平排列
 */
fun RecyclerView.horizon(): RecyclerView {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    return this
}

/**
 * 设置内容 adapter
 */
fun RecyclerView.currentAdapter(adapter: BaseQuickAdapter<*, *>): RecyclerView {
    val helper = QuickAdapterHelper.Builder(adapter).build()
    setTag(getTagId(), helper)
    setAdapter(helper.adapter)
    return this
}

/**
 * 内如实现匿名类 BaseBindAdapter去设置 adapter
 * binding内返回三个参数
 * @suppress RecyclerView.currentAdapter
 *
 *      RecyclerView.currentAdapter(Entity, ItemViewBinding){ binding, position, item ->
 *          binding.textView.text = "xxx"
 *      }
 *
 */
inline fun <T : Any, VB : ViewBinding> RecyclerView.currentAdapter(crossinline binding: (binding: VB, position: Int, item: T?) -> Unit): RecyclerView {
    val adapter = object : BaseBindAdapter<T, VB>() {
        override fun onBindViewHolder(holder: VH<VB>, position: Int, item: T?) {
            binding.invoke(holder.binding, position, item)
        }
    }
    val helper = QuickAdapterHelper.Builder(adapter).build()
    setTag(R.id.tag_helper, helper)
    setAdapter(helper.adapter)
    return this
}

/**
 * 公用ViewHolder
 */
class ViewHolder<VB: ViewBinding> (val binding: VB ): RecyclerView.ViewHolder(binding.root)

/**
 * 配合 currentAdapter（multiItem:BaseMultiItemAdapterImpl<T>.() -> Unit） 使用
 * 简化多布局样式设置
 */
open class BaseMultiItemAdapterImpl<T:Any>: BaseMultiItemAdapter<T>(){

    inline fun <reified VB:ViewBinding> addItemType(itemType: Int, isFullSpan:Boolean = false, crossinline onBind:(binding: VB, position: Int, item: T?) -> Unit){

        addItemType(itemType, object : OnMultiItemAdapterListener<T,ViewHolder<VB>>{

            override fun onBind(holder: ViewHolder<VB>, position: Int, item: T?) {
                onBind.invoke(holder.binding, position, item)
            }

            override fun onCreate(context: Context, parent: ViewGroup, viewType: Int): ViewHolder<VB> {
                val method = VB::class.java.getDeclaredMethod(
                    "inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.java,
                    Boolean::class.java
                )
                val vb = method.invoke(null, LayoutInflater.from(context),parent,false) as VB
                return ViewHolder(vb)
            }

            override fun isFullSpanItem(itemType: Int): Boolean {
                return isFullSpan
            }
        })
    }
}

/**
 * 多布局 adapter
 * @suppress RecyclerView.currentAdapter
 *
 *    RecyclerView.currentAdapter<Entity>{
 *      addItemType<ListItemBinding>(0){ binding, position, item ->
 *
 *      }
 *
 *      addItemType<ListItemBinding>(1){ binding, position, item ->
 *
 *      }
 *
 *      onItemViewType { position, list ->
 *        position % 2
 *      }
 *    }
 *
 */
fun <T: Any>RecyclerView.currentAdapter(multiItem:BaseMultiItemAdapterImpl<T>.() -> Unit): RecyclerView {
    val helper = QuickAdapterHelper.Builder(
        BaseMultiItemAdapterImpl<T>().apply {
            multiItem.invoke(this)
        }
    ).build()
    setTag(getTagId(), helper)
    setAdapter(helper.adapter)
    return this
}

/**
 * @return BaseQuickAdapter<*,*>
 */
fun RecyclerView.getCurrentAdapter(): BaseQuickAdapter<*, *> {
    if (adapter == null) throw NullPointerException("currentAdapter is null")
    return getHelperByTag().contentAdapter
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> RecyclerView.currentAdapter(): BaseQuickAdapter<T, *> {
    if (adapter == null) throw NullPointerException("currentAdapter is null")
    return getHelperByTag().contentAdapter as BaseQuickAdapter<T, *>
}

/**
 * @param height 分割线高度
 * @param leftRightSpec 分割线padding左右的距离
 * @param colorRes 分割线颜色
 * @param alsoShowAtHead 头部和列表之间也展示该分割线
 */
fun RecyclerView.itemDecoration(
    height: Float,
    leftRightSpec: Float = 0F,
    colorRes: Int = android.R.color.transparent,
    alsoShowAtHead: Boolean = false
): RecyclerView {
    return itemDecoration(
        HorizontalItemDecoration(
            height,
            leftRightSpec,
            colorRes,
            alsoShowAtHead
        )
    )
}

fun RecyclerView.itemDecoration(deco: RecyclerView.ItemDecoration): RecyclerView {
    addItemDecoration(deco)
    return this
}

/**
 * 头部列表
 * 此方法只接受一个头部列表 多余的会被移除 保留最后一次设置的头部
 */
fun RecyclerView.header(quickAdapter: BaseQuickAdapter<*, *>): RecyclerView {
    return addHeader(true, quickAdapter)
}

/**
 * 头部列表
 * 此方法只接受一个头部列表 多余的会被移除 保留最后一次设置的头部
 */
fun RecyclerView.header(view: () -> View): RecyclerView {
    return addHeader(true, view)
}

/**
 * 头部占位适配器
 */
fun RecyclerView.header(height: Float, description: String = ""): RecyclerView {
    return addHeader(height, description, true)
}

/**
 * 头部列表
 * 根据调用顺序 添加头部列表
 */
fun RecyclerView.addHeader(
    isClean: Boolean = false,
    quickAdapter: BaseQuickAdapter<*, *>
): RecyclerView {
    //设置尾部 需要本体
    if (adapter == null) throw NullPointerException("need call currentAdapter() first")
    getHelperByTag().apply {
        if (isClean) clearBeforeAdapters()
        addBeforeAdapter(quickAdapter)
    }
    return this
}

/**
 * 头部列表
 * 根据调用顺序添加头部列表
 */
fun RecyclerView.addHeader(isClean: Boolean = false, view: () -> View): RecyclerView {
    //设置尾部 需要本体
    if (adapter == null) throw NullPointerException("need call currentAdapter() first")
    getHelperByTag().apply {
        if (isClean) clearBeforeAdapters()
        addBeforeAdapter(ViewTempAdapter(view.invoke()))
    }
    return this
}

/**
 * 头部占位适配器
 */
fun RecyclerView.addHeader(
    height: Float,
    description: String = "",
    isClean: Boolean = false
): RecyclerView {
    //设置尾部 需要本体
    if (adapter == null) throw NullPointerException("need call currentAdapter() first")
    getHelperByTag().apply {
        if (isClean) clearBeforeAdapters()
        addBeforeAdapter(TextViewAdapter(description, height))
    }
    return this
}

/**
 * 尾部列表
 * 此方法只接受一个尾部列表 多余的会被移除 保留最后一次设置的尾部
 */
fun RecyclerView.footer(quickAdapter: BaseQuickAdapter<*, *>): RecyclerView {
    return addFooter(true, quickAdapter)
}

/**
 * 尾部列表
 * 此方法只接受一个尾部列表 多余的会被移除 保留最后一次设置的尾部
 */
fun RecyclerView.footer(view: () -> View): RecyclerView {
    return addFooter(true, view)
}

/**
 * 尾部占位适配器
 */
fun RecyclerView.footer(height: Int, description: String = ""): RecyclerView {
    return addFooter(height, description, true)
}


/**
 * 尾部列表
 * 根据调用顺序 添加尾部列表
 */
fun RecyclerView.addFooter(
    isClean: Boolean = false,
    quickAdapter: BaseQuickAdapter<*, *>
): RecyclerView {
    //设置尾部 需要本体
    if (adapter == null) throw NullPointerException("need call currentAdapter() first")
    getHelperByTag().apply {
        if (isClean) clearAfterAdapters()
        addAfterAdapter(quickAdapter)
    }
    return this
}

/**
 * 尾部列表
 * 根据调用顺序添加尾部列表
 */
fun RecyclerView.addFooter(isClean: Boolean = false, view: () -> View): RecyclerView {
    //设置尾部 需要本体
    if (adapter == null) throw NullPointerException("need call currentAdapter() first")
    getHelperByTag().apply {
        if (isClean) clearAfterAdapters()
        addAfterAdapter(ViewTempAdapter(view.invoke()))
    }
    return this
}

/**
 * 尾部占位适配器
 */
fun RecyclerView.addFooter(
    height: Int,
    description: String = "",
    isClean: Boolean = false
): RecyclerView {
    //设置尾部 需要本体
    if (adapter == null) throw NullPointerException("need call currentAdapter() first")
    getHelperByTag().apply {
        if (isClean) clearAfterAdapters()
        addAfterAdapter(TextViewAdapter(description, height.toFloat()))
    }
    return this
}

/**
 * 设置空布局占位符
 * @suppress 注意该空布局的父布局是满屏的。如果使用空布局且存在 footer 的时候，footer 会在最底下出现
 * 如果 recyclerView height 是 wrapContent 则recyclerview高度为emptyView高度，且能滑动出footer
 */
fun RecyclerView.emptyView(emptyView: View): RecyclerView {
    if (adapter == null) throw NullPointerException("need call currentAdapter() first")
    val helper = getHelperByTag()
    val current = helper.contentAdapter
    current.stateView = emptyView
    current.isStateViewEnable = true
    return this
}

/**
 * 设置空布局占位符
 * @suppress 注意该空布局的父布局是满屏的。如果使用空布局且存在 footer 的时候，footer 会在最底下出现
 * 如果 recyclerView height 是 wrapContent 则recyclerview高度为emptyView高度，且能滑动出footer
 */
fun RecyclerView.emptyView(@LayoutRes emptyViewResId: Int): RecyclerView {
    if (adapter == null) throw NullPointerException("need call currentAdapter() first")
    val helper = getHelperByTag()
    val current = helper.contentAdapter
    current.setStateViewLayout(context, emptyViewResId)
    current.isStateViewEnable = true
    return this
}

/**
 * 允许展示空布局
 */
fun RecyclerView.showEmpty() {
    if (adapter != null) getHelperByTag().contentAdapter.isStateViewEnable = true
}

/**
 * 不允许展示空布局
 */
fun RecyclerView.hideEmpty() {
    if (adapter != null) getHelperByTag().contentAdapter.isStateViewEnable = false
}

/**
 * 头部占位适配器
 */
fun RecyclerView.cleanHeader(): RecyclerView {
    //设置尾部 需要本体
    if (adapter == null) return this
    getHelperByTag().clearBeforeAdapters()
    return this
}

/**
 * 头部占位适配器
 */
fun RecyclerView.cleanFooter(): RecyclerView {
    //设置尾部 需要本体
    if (adapter == null) return this
    getHelperByTag().clearAfterAdapters()
    return this
}

/**
 * 列表点击
 */
fun <T : Any> RecyclerView.onItemClick(listener: (position: Int, item: T) -> Unit): RecyclerView {
    currentAdapter<T>().setOnDebouncedItemClick { adapter, _, position ->
        listener.invoke(position, adapter.items[position])
    }
    return this
}

/**
 * 列表长按
 */
fun <T : Any> RecyclerView.onItemLongClick(listener: (position: Int, item: T) -> Unit): RecyclerView {
    currentAdapter<T>().setOnItemLongClickListener { adapter, _, position ->
        listener.invoke(position, adapter.items[position])
        true
    }
    return this
}

/**
 * 列表子view点击
 */
fun <T : Any> RecyclerView.addItemChildClick(
    @IdRes id: Int,
    listener: (position: Int, item: T) -> Unit
): RecyclerView {
    currentAdapter<T>().addOnDebouncedChildClick(id) { adapter, _, position ->
        listener.invoke(position, adapter.items[position])
    }
    return this
}

/**
 * 列表子view长按
 */
fun <T : Any> RecyclerView.addItemChildLongClick(
    @IdRes id: Int,
    listener: (position: Int, item: T) -> Unit
): RecyclerView {
    currentAdapter<T>().addOnItemChildLongClickListener(id) { adapter, _, position ->
        listener.invoke(position, adapter.items[position])
        true
    }
    return this
}

/**
 * 设置数据集合
 */
fun <T : Any> RecyclerView.submitList(list: List<T>) {
    currentAdapter<T>().submitList(list)
}

/**
 * 绑定数据集合
 */
fun <T : Any> RecyclerView.submitLiveData(activity: FragmentActivity, list: MutableLiveData<MutableList<T>>) {
    currentAdapter<T>().submitList(list.value)
    list.observe(activity){ notifyDataSetChange() }
}

/**
 * 绑定数据集合
 */
fun <T : Any> RecyclerView.submitLiveData(fragment: Fragment, list: MutableLiveData<MutableList<T>>) {
    currentAdapter<T>().submitList(list.value)
    list.observe(fragment){ notifyDataSetChange() }
}

fun <T: Any>RecyclerView.addItem(item: T){
    currentAdapter<T>().apply {
        //刷新最后一行的分割线
        if (items.isNotEmpty())notifyItemChanged(items.size - 1)
        add(item)
    }
}

fun <T: Any>RecyclerView.addItems(list: List<T>){

    currentAdapter<T>().apply {
        //刷新最后一行的分割线
        if (items.isNotEmpty())notifyItemChanged(items.size - 1)
        addAll(list)
    }
}

fun RecyclerView.remove(item: Any){
    currentAdapter<Any>().apply {
        if (items.isNotEmpty()){
            remove(item)
        }
    }
}

fun RecyclerView.remove(position: Int){
    currentAdapter<Any>().apply {
        if (items.isNotEmpty()){
            removeAt(position)
        }
    }
}

fun RecyclerView.removeAll(){
    currentAdapter<Any>().apply {
        removeAtRange(items.indices)
    }
}

@SuppressLint("NotifyDataSetChanged")
fun RecyclerView.notifyDataSetChange(){
    getCurrentAdapter().notifyDataSetChanged()
}
