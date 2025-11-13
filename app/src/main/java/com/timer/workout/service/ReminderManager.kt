package com.timer.workout.service

import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import com.timer.workout.R
import com.timer.workout.util.PermissionUtil
import com.timer.workout.util.SettingsManager

/**
 * 提醒管理器 - 负责声音和震动提醒
 */
class ReminderManager private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: ReminderManager? = null

        fun getInstance(context: Context): ReminderManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReminderManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val context: Context = context.applicationContext
    private val settingsManager: SettingsManager = SettingsManager.getInstance(context)
    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(context, NotificationManager::class.java)!!
    }

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = ContextCompat.getSystemService(context, VibratorManager::class.java)
            vibratorManager?.defaultVibrator ?: getLegacyVibrator()
        } else {
            getLegacyVibrator()
        }
    }

    private var mediaPlayer: MediaPlayer? = null

    /**
     * 播放声音提醒
     * @param soundType 声音类型
     */
    fun playSound(soundType: SoundType) {
        if (!settingsManager.soundEnabled || !PermissionUtil.hasAudioPermission(context)) {
            return
        }
        
        try {
            stopSound() // 停止之前的播放

            mediaPlayer = MediaPlayer().apply {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                setAudioAttributes(audioAttributes)

                when (soundType) {
                    SoundType.BEEP -> {
                        // 使用系统默认提示音
                        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        setDataSource(context, defaultSoundUri)
                    }
                    SoundType.ALARM -> {
                        // 使用系统闹钟声音
                        val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                        setDataSource(context, alarmSoundUri)
                    }
                    SoundType.CUSTOM -> {
                        // 使用自定义声音（如果有）
                        val customSoundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.timer_beep)
                        setDataSource(context, customSoundUri)
                    }
                }

                setOnPreparedListener { mp ->
                    mp.start()
                }

                setOnCompletionListener { mp ->
                    mp.release()
                    mediaPlayer = null
                }

                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止声音播放
     */
    fun stopSound() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
            mediaPlayer = null
        }
    }

    /**
     * 震动提醒
     * @param vibrationType 震动类型
     */
    fun vibrate(vibrationType: VibrationType) {
        if (!settingsManager.vibrationEnabled || !PermissionUtil.hasVibratePermission(context)) {
            return
        }
        
        try {
            if (!hasVibratePermission()) {
                return
            }

            val pattern = when (vibrationType) {
                VibrationType.SHORT -> longArrayOf(0, 200) // 短震动
                VibrationType.LONG -> longArrayOf(0, 500) // 长震动
                VibrationType.PATTERN -> longArrayOf(0, 100, 100, 100, 100, 100) // 模式震动
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止震动
     */
    fun stopVibration() {
        try {
            vibrator.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 组合提醒（声音+震动）
     */
    fun playCombinedReminder(soundType: SoundType, vibrationType: VibrationType) {
        if (settingsManager.soundEnabled && PermissionUtil.hasAudioPermission(context)) {
            playSound(soundType)
        }
        
        if (settingsManager.vibrationEnabled && PermissionUtil.hasVibratePermission(context)) {
            vibrate(vibrationType)
        }
    }

    /**
     * 停止所有提醒
     */
    fun stopAllReminders() {
        stopSound()
        stopVibration()
    }

    /**
     * 检查是否有震动权限
     */
    private fun hasVibratePermission(): Boolean {
        return PermissionUtil.hasVibratePermission(context)
    }

    /**
     * 检查是否有音频权限
     */
    fun hasAudioPermission(): Boolean {
        return PermissionUtil.hasAudioPermission(context)
    }

    /**
     * 检查是否启用了任何提醒
     */
    fun isAnyReminderEnabled(): Boolean {
        return settingsManager.isAnyReminderEnabled()
    }

    /**
     * 播放阶段完成提醒
     */
    fun playStageCompleteReminder() {
        if (!settingsManager.stageCompleteReminder) {
            return
        }
        
        val soundType = when (settingsManager.soundType) {
            "beep" -> SoundType.BEEP
            "alarm" -> SoundType.ALARM
            "custom" -> SoundType.CUSTOM
            else -> SoundType.BEEP
        }
        
        val vibrationType = when (settingsManager.vibrationType) {
            "short" -> VibrationType.SHORT
            "long" -> VibrationType.LONG
            "alert" -> VibrationType.PATTERN
            "complete" -> VibrationType.LONG
            "warning" -> VibrationType.PATTERN
            "heartbeat" -> VibrationType.PATTERN
            else -> VibrationType.SHORT
        }
        
        playCombinedReminder(soundType, vibrationType)
    }

    /**
     * 播放训练完成提醒
     */
    fun playWorkoutCompleteReminder() {
        if (!settingsManager.workoutCompleteReminder) {
            return
        }
        
        val soundType = when (settingsManager.soundType) {
            "beep" -> SoundType.BEEP
            "alarm" -> SoundType.ALARM
            "custom" -> SoundType.CUSTOM
            else -> SoundType.ALARM // 训练完成使用更明显的闹钟音
        }
        
        val vibrationType = when (settingsManager.vibrationType) {
            "short" -> VibrationType.LONG // 训练完成使用长震动
            "long" -> VibrationType.LONG
            "alert" -> VibrationType.PATTERN
            "complete" -> VibrationType.LONG
            "warning" -> VibrationType.PATTERN
            "heartbeat" -> VibrationType.PATTERN
            else -> VibrationType.LONG
        }
        
        playCombinedReminder(soundType, vibrationType)
    }

    /**
     * 播放警告提醒
     */
    fun playWarningReminder() {
        if (!settingsManager.warningReminder) {
            return
        }
        
        // 警告提醒使用更明显的模式
        playCombinedReminder(SoundType.ALARM, VibrationType.PATTERN)
    }

    /**
     * 获取旧版本震动器
     */
    @Suppress("DEPRECATION")
    private fun getLegacyVibrator(): Vibrator {
        return ContextCompat.getSystemService(context, Vibrator::class.java)!!
    }

    /**
     * 声音类型枚举
     */
    enum class SoundType {
        BEEP,    // 提示音
        ALARM,   // 闹钟音
        CUSTOM   // 自定义音
    }

    /**
     * 震动类型枚举
     */
    enum class VibrationType {
        SHORT,   // 短震动
        LONG,    // 长震动
        PATTERN  // 模式震动
    }
}