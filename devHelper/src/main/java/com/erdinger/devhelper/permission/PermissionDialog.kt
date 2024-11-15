package com.erdinger.devhelper.permission

import android.content.Context
import android.os.Bundle
import android.view.View
import com.erdinger.devhelper.R
import com.erdinger.devhelper.databinding.DialogPasswordTipBinding
import com.permissionx.guolindev.dialog.RationaleDialog

class PermissionDialog: RationaleDialog {

    companion object{
        @JvmStatic
        fun with(context: Context): PermissionDialog {
            return PermissionDialog(context)
        }
    }

    private constructor(context: Context):this(context, R.style.MsgDialogStyle)
    private constructor(context: Context, style: Int):super(context,style)

    private val permissions:MutableList<String> = mutableListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        val attributes = window!!.attributes
//        attributes.width = WindowManager.LayoutParams.MATCH_PARENT
    }

    override fun getPositiveButton(): View {
        return binding.confirm
    }

    override fun getNegativeButton(): View? {
        return null
    }

    override fun getPermissionsToRequest(): MutableList<String> {
        return permissions
    }

    val binding: DialogPasswordTipBinding =
        DialogPasswordTipBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)

    fun bind (title: String, content:String, btnText:String ): PermissionDialog {
        binding.title.text = title
        binding.content.text = content
        binding.confirm.text = btnText
        return this
    }

    fun permissions(list:MutableList<String>): PermissionDialog {
        permissions.clear()
        permissions.addAll(list)
        return this
    }

}