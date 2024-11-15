package com.erdinger.devhelper.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter4.BaseSingleItemAdapter
import java.lang.reflect.ParameterizedType

abstract class BaseSingleAdapter<E:Any,VB: ViewBinding>(item:E): BaseSingleItemAdapter<E,BaseSingleAdapter.VH<VB>>(){

    init {
        this.item = item
    }

    class VH<VB: ViewBinding> (private val binding: VB ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH<VB> {
        val superclass = this.javaClass.genericSuperclass as ParameterizedType
        val type = superclass.actualTypeArguments[1] as Class<*>
        val method = type.getDeclaredMethod("inflate", LayoutInflater::class.java,ViewGroup::class.java,Boolean::class.java)
        val vb = method.invoke(null,LayoutInflater.from(context),parent,false) as VB
        return VH(vb)
    }

}