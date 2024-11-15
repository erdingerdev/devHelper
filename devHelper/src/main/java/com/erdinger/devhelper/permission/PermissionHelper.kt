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
    private var permissionsTemp = arrayListOf<String>()
    private var explain = false
    private var explainDone = false//已经解释过了 不重复弹窗
    private var forwardDone = false//权限被限于为不再提醒的解释 已经解释过了 不重复弹窗
    private var contentString = ""
    private var explainString = "快进商店申请相机权限，用于扫描二维码进行设备登录"

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

        const val CONTENT_CAMERA = "请允许快进go使用您的相机权限"
        const val EXPLAIN_CAMERA = "快进go申请相机权限，用于识别/拍照上传等"
        const val CONTENT_FILE = "请允许快进go使用您的储存权限"
        const val EXPLAIN_FILE = "快进go申请储存权限，用于读取/保存图片视频等"
        const val CONTENT_LOCATION = "请允许快进go使用您的位置权限"
        const val EXPLAIN_LOCATION = "快进go申请定位权限，用于为您提供精准的定位及导航类服务"
        const val CONTENT_BLE = "请允许快进go使用您的位置和蓝牙权限"
        const val EXPLAIN_BLE = "快进go申请位置和蓝牙权限，用于访问您的蓝牙与打印机进行通信"
        const val CONTENT_PHONE = "请允许快进go使用您的电话权限"
        const val EXPLAIN_PHONE = "快进go申请电话权限，用于提供拨打电话服务"
    }

    fun cameraPer(): PermissionHelper {
        permission(Permissions.CAMERA)
        type(CONTENT_CAMERA)
        explainString = EXPLAIN_CAMERA
        return this
    }

    fun readPer(context: Context): PermissionHelper {
        permission(Permissions.getReadPer(context))
        type(CONTENT_FILE)
        explainString = EXPLAIN_FILE
        return this
    }

    fun readWritePer(): PermissionHelper {
        permission(Permissions.getReadWritePer())
        type(CONTENT_FILE)
        explainString = EXPLAIN_FILE
        return this
    }

    fun blePer(): PermissionHelper {
        permission(Permissions.BLE)
        type(CONTENT_BLE)
        explainString = EXPLAIN_BLE
        return this
    }

    fun callPhonePer(): PermissionHelper {
        permission(Permissions.CALL_PHONE)
        type(CONTENT_PHONE)
        explainString = EXPLAIN_PHONE
        return this
    }

    fun locationPer(): PermissionHelper {
        permission(Permissions.LOCATION)
        type(CONTENT_LOCATION)
        explainString = EXPLAIN_LOCATION
        return this
    }

    fun permission(permissions: List<String>): PermissionHelper {
        permissionsTemp.addAll(permissions)
        return this
    }

    fun permission(permission: String): PermissionHelper {
        permissionsTemp.add(permission)
        return this
    }

    fun type(contentString: String): PermissionHelper {
        this.contentString = contentString
        return this
    }

    fun explainType(explainString: String): PermissionHelper {
        this.explainString = explainString
        return this
    }

    fun explain(): PermissionHelper {
        explain = true
        return this
    }

    @Synchronized
    fun request(callBack: RequestSuccess) {
        if (!createRequest){
            createRequest = true
            explainDone = false
            forwardDone = false
            if (builder == null) media?.apply { builder = this.permissions(permissionsTemp) }
            builder?.apply {
                if (explain){
                    explainReasonBeforeRequest()
                    onExplainRequestReason { scope, deniedList ->
                        if (explainDone.not()){
                            explainDone = true
                            val dialog = PermissionDialog.with(builder!!.activity).permissions(deniedList).bind(
                                "温馨提示", explainString, "去开启授权")
                            scope.showRequestReasonDialog(dialog)
                            dialog.setCancelable(true)
                            dialog.setCanceledOnTouchOutside(true)
                            dialog.setOnCancelListener { createRequest = false }
                        }else {
                            createRequest = false
                        }
                    }
                }
                onForwardToSettings { scope, deniedList ->
                    if (forwardDone.not()){
                        forwardDone = true
                        val dialog = PermissionDialog.with(builder!!.activity).permissions(deniedList).bind(
                            "温馨提示", contentString, "去开启授权"
                        )
                        scope.showForwardToSettingsDialog(dialog)
                        dialog.setCancelable(true)
                        dialog.setCanceledOnTouchOutside(true)
                        dialog.setOnCancelListener { createRequest = false }
                    }else {
                        createRequest = false
                    }
                }
                this.request { allGranted, _, _ ->
                    createRequest = false
                    if (allGranted) callBack.onSuccess()
                }
            }
        }
    }

}

