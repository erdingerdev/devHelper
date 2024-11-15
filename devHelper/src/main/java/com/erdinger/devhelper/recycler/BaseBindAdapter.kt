package com.erdinger.devhelper.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseQuickAdapter
import java.lang.reflect.ParameterizedType

/**
 * 快速创建列表的基类，基于viewBinding
 */
abstract class BaseBindAdapter<E:Any,VB: ViewBinding>: BaseQuickAdapter<E,BaseBindAdapter.VH<VB>>(){

    class VH<VB: ViewBinding> (val binding: VB ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH<VB> {
        val superclass = this.javaClass.genericSuperclass as ParameterizedType
        val type = superclass.actualTypeArguments[1] as Class<*>
        val method = type.getDeclaredMethod("inflate", LayoutInflater::class.java,ViewGroup::class.java,Boolean::class.java)
        val vb = method.invoke(null,LayoutInflater.from(context),parent,false) as VB
        return VH(vb)
    }

}