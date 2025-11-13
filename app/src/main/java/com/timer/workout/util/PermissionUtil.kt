package com.timer.workout.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * 权限管理工具类
 */
object PermissionUtil {

    // 震动权限
    private const val VIBRATE_PERMISSION = Manifest.permission.VIBRATE

    // 音频权限
    private const val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO

    /**
     * 检查是否有震动权限
     */
    fun hasVibratePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, VIBRATE_PERMISSION) == 
               PackageManager.PERMISSION_GRANTED
    }

    /**
     * 检查是否有音频权限
     */
    fun hasAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, AUDIO_PERMISSION) == 
               PackageManager.PERMISSION_GRANTED
    }

    /**
     * 请求震动权限
     */
    fun requestVibratePermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity, 
            arrayOf(VIBRATE_PERMISSION), 
            requestCode
        )
    }

    /**
     * 请求音频权限
     */
    fun requestAudioPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity, 
            arrayOf(AUDIO_PERMISSION), 
            requestCode
        )
    }

    /**
     * 请求所有提醒相关权限
     */
    fun requestAllReminderPermissions(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity, 
            arrayOf(VIBRATE_PERMISSION, AUDIO_PERMISSION), 
            requestCode
        )
    }

    /**
     * 检查权限请求结果
     */
    fun isPermissionGranted(
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        return grantResults.isNotEmpty() && 
               grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }

    /**
     * 检查是否所有提醒权限都已授予
     */
    fun hasAllReminderPermissions(context: Context): Boolean {
        return hasVibratePermission(context) && hasAudioPermission(context)
    }

    /**
     * 显示权限说明对话框（如果需要）
     */
    fun shouldShowPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * 获取权限名称
     */
    fun getPermissionName(permission: String): String {
        return when (permission) {
            VIBRATE_PERMISSION -> "震动权限"
            AUDIO_PERMISSION -> "音频权限"
            else -> "未知权限"
        }
    }

    /**
     * 获取权限说明
     */
    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            VIBRATE_PERMISSION -> "需要震动权限来提供触觉反馈"
            AUDIO_PERMISSION -> "需要音频权限来播放提醒声音"
            else -> "需要此权限来提供完整功能"
        }
    }
}