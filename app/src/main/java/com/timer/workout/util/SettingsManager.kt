package com.timer.workout.util

import android.content.Context
import android.content.SharedPreferences

/**
 * 设置管理器
 */
class SettingsManager private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("workout_timer_settings", Context.MODE_PRIVATE)

    // 声音设置
    var soundEnabled: Boolean
        get() = sharedPreferences.getBoolean("sound_enabled", true)
        set(value) = sharedPreferences.edit().putBoolean("sound_enabled", value).apply()

    var soundVolume: Int
        get() = sharedPreferences.getInt("sound_volume", 80)
        set(value) = sharedPreferences.edit().putInt("sound_volume", value).apply()

    var soundType: String
        get() = sharedPreferences.getString("sound_type", "beep") ?: "beep"
        set(value) = sharedPreferences.edit().putString("sound_type", value).apply()

    // 震动设置
    var vibrationEnabled: Boolean
        get() = sharedPreferences.getBoolean("vibration_enabled", true)
        set(value) = sharedPreferences.edit().putBoolean("vibration_enabled", value).apply()

    var vibrationIntensity: Int
        get() = sharedPreferences.getInt("vibration_intensity", 50)
        set(value) = sharedPreferences.edit().putInt("vibration_intensity", value).apply()

    var vibrationType: String
        get() = sharedPreferences.getString("vibration_type", "short") ?: "short"
        set(value) = sharedPreferences.edit().putString("vibration_type", value).apply()

    // 提醒设置
    var stageCompleteReminder: Boolean
        get() = sharedPreferences.getBoolean("stage_complete_reminder", true)
        set(value) = sharedPreferences.edit().putBoolean("stage_complete_reminder", value).apply()

    var workoutCompleteReminder: Boolean
        get() = sharedPreferences.getBoolean("workout_complete_reminder", true)
        set(value) = sharedPreferences.edit().putBoolean("workout_complete_reminder", value).apply()

    var warningReminder: Boolean
        get() = sharedPreferences.getBoolean("warning_reminder", true)
        set(value) = sharedPreferences.edit().putBoolean("warning_reminder", value).apply()

    // 其他设置
    var autoStartNextStage: Boolean
        get() = sharedPreferences.getBoolean("auto_start_next_stage", false)
        set(value) = sharedPreferences.edit().putBoolean("auto_start_next_stage", value).apply()

    var showCountdown: Boolean
        get() = sharedPreferences.getBoolean("show_countdown", true)
        set(value) = sharedPreferences.edit().putBoolean("show_countdown", value).apply()

    var keepScreenOn: Boolean
        get() = sharedPreferences.getBoolean("keep_screen_on", true)
        set(value) = sharedPreferences.edit().putBoolean("keep_screen_on", value).apply()

    /**
     * 重置所有设置为默认值
     */
    fun resetToDefaults() {
        with(sharedPreferences.edit()) {
            // 声音设置
            putBoolean("sound_enabled", true)
            putInt("sound_volume", 80)
            putString("sound_type", "beep")
            
            // 震动设置
            putBoolean("vibration_enabled", true)
            putInt("vibration_intensity", 50)
            putString("vibration_type", "short")
            
            // 提醒设置
            putBoolean("stage_complete_reminder", true)
            putBoolean("workout_complete_reminder", true)
            putBoolean("warning_reminder", true)
            
            // 其他设置
            putBoolean("auto_start_next_stage", false)
            putBoolean("show_countdown", true)
            putBoolean("keep_screen_on", true)
            apply()
        }
    }

    /**
     * 获取声音设置摘要
     */
    fun getSoundSettingsSummary(): String {
        return if (soundEnabled) {
            "音量: ${soundVolume}%, 类型: ${getSoundTypeDisplayName()}"
        } else {
            "已关闭"
        }
    }

    /**
     * 获取震动设置摘要
     */
    fun getVibrationSettingsSummary(): String {
        return if (vibrationEnabled) {
            "强度: ${vibrationIntensity}%, 类型: ${getVibrationTypeDisplayName()}"
        } else {
            "已关闭"
        }
    }

    /**
     * 获取声音类型显示名称
     */
    private fun getSoundTypeDisplayName(): String {
        return when (soundType) {
            "beep" -> "蜂鸣声"
            "alarm" -> "警报声"
            "custom" -> "自定义"
            else -> "蜂鸣声"
        }
    }

    /**
     * 获取震动类型显示名称
     */
    private fun getVibrationTypeDisplayName(): String {
        return when (vibrationType) {
            "short" -> "短震动"
            "long" -> "长震动"
            "alert" -> "提醒模式"
            "complete" -> "完成模式"
            "warning" -> "警告模式"
            "heartbeat" -> "心跳模式"
            else -> "短震动"
        }
    }

    /**
     * 检查是否启用了任何提醒
     */
    fun isAnyReminderEnabled(): Boolean {
        return soundEnabled || vibrationEnabled
    }

    /**
     * 检查是否启用了特定类型的提醒
     */
    fun isReminderEnabledForType(reminderType: String): Boolean {
        return when (reminderType) {
            "stage_complete" -> stageCompleteReminder
            "workout_complete" -> workoutCompleteReminder
            "warning" -> warningReminder
            else -> true
        }
    }
}