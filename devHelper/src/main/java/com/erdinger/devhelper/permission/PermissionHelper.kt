package com.erdinger.devhelper.permission

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionMediator
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.PermissionBuilder

class PermissionHelper {

    private var media: PermissionMediator? = null
    private var builder: PermissionBuilder? = null
    private var contentString = ""

    companion object {
        //避免频繁弹起
        private var createRequest = false

        @JvmStatic
        fun with(fragmentActivity: FragmentActivity): PermissionHelper {
            val helper = PermissionHelper()
            helper.media = PermissionX.init(fragmentActivity)
            return helper
        }

        @JvmStatic
        fun with(fragment: Fragment): PermissionHelper {
            val helper = PermissionHelper()
            helper.media = PermissionX.init(fragment)
            return helper
        }

        const val CONTENT_CAMERA_FILE = "请允许快进go使用您的相机和储存权限"
        const val CONTENT_CAMERA = "请允许快进go使用您的相机权限"
        const val CONTENT_FILE = "请允许快进go使用您的储存权限"
        const val CONTENT_LOCATION = "请允许快进go使用您的位置权限"
        const val CONTENT_BLE = "请允许快进go使用您的位置和蓝牙权限"
        const val CONTENT_PHONE = "请允许快进go使用您的电话权限"
    }

    fun cameraPer(): PermissionHelper {
        permission(Permissions.CAMERA)
        type(CONTENT_CAMERA)
        return this
    }

    fun readPer(context: Context): PermissionHelper {
        permission(Permissions.getReadPer(context))
        type(CONTENT_FILE)
        return this
    }

    fun readWritePer(): PermissionHelper {
        permission(Permissions.getReadWritePer())
        type(CONTENT_FILE)
        return this
    }

    fun blePer(): PermissionHelper {
        permission(Permissions.BLE)
        type(CONTENT_BLE)
        return this
    }

    fun callPhonePer(): PermissionHelper {
        permission(Permissions.CALL_PHONE)
        type(CONTENT_PHONE)
        return this
    }

    fun locationPer(): PermissionHelper {
        permission(Permissions.LOCATION)
        type(CONTENT_LOCATION)
        return this
    }

    fun permission(permissions: List<String>): PermissionHelper {
        builder = media?.permissions(permissions)
        return this
    }

    fun permission(permission: String): PermissionHelper {
        builder = media?.permissions(permission)
        return this
    }

    fun type(contentString: String): PermissionHelper {
        this.contentString = contentString
        return this
    }

    fun explain(): PermissionHelper {
        builder?.apply {
            onExplainRequestReason { scope, deniedList ->
                val dialog = PermissionDialog.with(builder!!.activity).permissions(deniedList).bind(
                    "安全提示", contentString, "去开启授权")
                scope.showRequestReasonDialog(dialog)
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(true)
                dialog.setOnCancelListener { createRequest = false }
            }
        }
        return this
    }

    @Synchronized
    fun request(callBack: RequestSuccess) {
        if (!createRequest){
            createRequest = true
            builder?.apply {
//                onExplainRequestReason { scope, deniedList ->
//                    val dialog = PermissionDialog.with(builder!!.activity).permissions(deniedList).bind(
//                        "安全提示",
//                        "请允许快进go使用您的${contentString}权限",
//                        "去开启授权"
//                    )
//                    scope.showRequestReasonDialog(dialog)
//                    dialog.setCancelable(true)
//                    dialog.setCanceledOnTouchOutside(true)
//                    dialog.setOnCancelListener { createRequest = false }
//                }
                onForwardToSettings { scope, deniedList ->
                    val dialog = PermissionDialog.with(builder!!.activity).permissions(deniedList).bind(
                        "安全提示", contentString, "去开启授权"
                    )
                    scope.showForwardToSettingsDialog(dialog)
                    dialog.setCancelable(true)
                    dialog.setCanceledOnTouchOutside(true)
                    dialog.setOnCancelListener { createRequest = false }
                }
                this.request { allGranted, _, _ ->
                    createRequest = false
                    if (allGranted) callBack.onSuccess()
                }
            }
        }
    }

}

