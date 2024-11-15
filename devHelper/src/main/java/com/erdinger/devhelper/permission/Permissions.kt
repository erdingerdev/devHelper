package com.erdinger.devhelper.permission

import android.Manifest
import android.content.Context
import android.os.Build

object Permissions {

    const val CAMERA = Manifest.permission.CAMERA

    const val CALL_PHONE = Manifest.permission.CALL_PHONE

    val LOCATION = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val BLE = listOf(
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun getReadWritePer(): List<String>{
        return mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun getReadPer(context: Context): MutableList<String> {
//        if (SdkVersionUtils.isUPSIDE_DOWN_CAKE()) {
//            val targetSdkVersion: Int = context.applicationInfo.targetSdkVersion
//            return if (targetSdkVersion >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                mutableListOf<String>(
//                    PermissionConfig.READ_MEDIA_VISUAL_USER_SELECTED,
//                    PermissionConfig.READ_MEDIA_IMAGES,
//                    PermissionConfig.READ_MEDIA_VIDEO
//                )
//            } else if (targetSdkVersion == Build.VERSION_CODES.TIRAMISU) {
//                mutableListOf(
//                    PermissionConfig.READ_MEDIA_IMAGES,
//                    PermissionConfig.READ_MEDIA_VIDEO
//                )
//            } else {
//                mutableListOf(PermissionConfig.READ_EXTERNAL_STORAGE)
//            }
//        } else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val targetSdkVersion: Int = context.applicationInfo.targetSdkVersion
            return if (targetSdkVersion >= Build.VERSION_CODES.TIRAMISU)
                mutableListOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            else
                mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }


}