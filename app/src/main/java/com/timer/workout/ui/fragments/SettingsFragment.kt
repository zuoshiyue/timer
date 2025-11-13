package com.timer.workout.ui.fragments

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.timer.workout.R
import com.timer.workout.databinding.FragmentSettingsBinding
import com.timer.workout.util.PermissionUtil
import com.timer.workout.util.SettingsManager
import kotlinx.coroutines.launch

/**
 * 设置界面
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        settingsManager = SettingsManager.getInstance(requireContext())
        
        setupToolbar()
        setupSoundSettings()
        setupVibrationSettings()
        setupReminderSettings()
        setupOtherSettings()
        setupPermissionSection()
        setupResetButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.title = "设置"
    }

    private fun setupSoundSettings() {
        // 声音开关
        binding.switchSound.isChecked = settingsManager.soundEnabled
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.soundEnabled = isChecked
            updateSoundSettingsVisibility()
        }

        // 音量设置
        binding.seekBarVolume.progress = settingsManager.soundVolume
        binding.seekBarVolume.setOnSeekBarChangeListener(object : 
            androidx.appcompat.widget.AppCompatSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: androidx.appcompat.widget.AppCompatSeekBar?, progress: Int, fromUser: Boolean) {
                binding.textVolumeValue.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: androidx.appcompat.widget.AppCompatSeekBar?) {}

            override fun onStopTrackingTouch(seekBar: androidx.appcompat.widget.AppCompatSeekBar?) {
                settingsManager.soundVolume = seekBar?.progress ?: 80
            }
        })

        // 声音类型
        binding.textSoundTypeValue.text = getSoundTypeDisplayName(settingsManager.soundType)
        binding.layoutSoundType.setOnClickListener {
            showSoundTypeDialog()
        }

        updateSoundSettingsVisibility()
    }

    private fun setupVibrationSettings() {
        // 震动开关
        binding.switchVibration.isChecked = settingsManager.vibrationEnabled
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.vibrationEnabled = isChecked
            updateVibrationSettingsVisibility()
        }

        // 震动强度
        binding.seekBarVibrationIntensity.progress = settingsManager.vibrationIntensity
        binding.seekBarVibrationIntensity.setOnSeekBarChangeListener(object : 
            androidx.appcompat.widget.AppCompatSeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: androidx.appcompat.widget.AppCompatSeekBar?, progress: Int, fromUser: Boolean) {
                binding.textVibrationIntensityValue.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: androidx.appcompat.widget.AppCompatSeekBar?) {}

            override fun onStopTrackingTouch(seekBar: androidx.appcompat.widget.AppCompatSeekBar?) {
                settingsManager.vibrationIntensity = seekBar?.progress ?: 50
            }
        })

        // 震动类型
        binding.textVibrationTypeValue.text = getVibrationTypeDisplayName(settingsManager.vibrationType)
        binding.layoutVibrationType.setOnClickListener {
            showVibrationTypeDialog()
        }

        updateVibrationSettingsVisibility()
    }

    private fun setupReminderSettings() {
        // 阶段完成提醒
        binding.switchStageCompleteReminder.isChecked = settingsManager.stageCompleteReminder
        binding.switchStageCompleteReminder.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.stageCompleteReminder = isChecked
        }

        // 训练完成提醒
        binding.switchWorkoutCompleteReminder.isChecked = settingsManager.workoutCompleteReminder
        binding.switchWorkoutCompleteReminder.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.workoutCompleteReminder = isChecked
        }

        // 警告提醒
        binding.switchWarningReminder.isChecked = settingsManager.warningReminder
        binding.switchWarningReminder.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.warningReminder = isChecked
        }
    }

    private fun setupOtherSettings() {
        // 自动开始下一阶段
        binding.switchAutoStartNextStage.isChecked = settingsManager.autoStartNextStage
        binding.switchAutoStartNextStage.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.autoStartNextStage = isChecked
        }

        // 显示倒计时
        binding.switchShowCountdown.isChecked = settingsManager.showCountdown
        binding.switchShowCountdown.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.showCountdown = isChecked
        }

        // 保持屏幕常亮
        binding.switchKeepScreenOn.isChecked = settingsManager.keepScreenOn
        binding.switchKeepScreenOn.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.keepScreenOn = isChecked
        }
    }

    private fun setupPermissionSection() {
        updatePermissionStatus()

        // 请求权限按钮
        binding.buttonRequestPermissions.setOnClickListener {
            requestPermissions()
        }

        // 应用设置按钮
        binding.buttonAppSettings.setOnClickListener {
            openAppSettings()
        }
    }

    private fun setupResetButton() {
        binding.buttonResetSettings.setOnClickListener {
            showResetConfirmationDialog()
        }
    }

    private fun updateSoundSettingsVisibility() {
        val isEnabled = settingsManager.soundEnabled
        binding.layoutVolume.visibility = if (isEnabled) View.VISIBLE else View.GONE
        binding.layoutSoundType.visibility = if (isEnabled) View.VISIBLE else View.GONE
    }

    private fun updateVibrationSettingsVisibility() {
        val isEnabled = settingsManager.vibrationEnabled
        binding.layoutVibrationIntensity.visibility = if (isEnabled) View.VISIBLE else View.GONE
        binding.layoutVibrationType.visibility = if (isEnabled) View.VISIBLE else View.GONE
    }

    private fun updatePermissionStatus() {
        val hasVibratePermission = PermissionUtil.hasVibratePermission(requireContext())
        val hasAudioPermission = PermissionUtil.hasAudioPermission(requireContext())
        
        val vibrateStatus = if (hasVibratePermission) "✓ 已授权" else "✗ 未授权"
        val audioStatus = if (hasAudioPermission) "✓ 已授权" else "✗ 未授权"
        
        binding.textVibratePermissionStatus.text = vibrateStatus
        binding.textAudioPermissionStatus.text = audioStatus
        
        val allGranted = hasVibratePermission && hasAudioPermission
        binding.textPermissionSummary.text = if (allGranted) {
            "所有权限已授权"
        } else {
            "部分权限未授权，可能影响提醒功能"
        }
    }

    private fun showSoundTypeDialog() {
        val soundTypes = arrayOf("蜂鸣声", "警报声", "自定义")
        val currentIndex = when (settingsManager.soundType) {
            "beep" -> 0
            "alarm" -> 1
            "custom" -> 2
            else -> 0
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("选择声音类型")
            .setSingleChoiceItems(soundTypes, currentIndex) { dialog, which ->
                val selectedType = when (which) {
                    0 -> "beep"
                    1 -> "alarm"
                    2 -> "custom"
                    else -> "beep"
                }
                settingsManager.soundType = selectedType
                binding.textSoundTypeValue.text = soundTypes[which]
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showVibrationTypeDialog() {
        val vibrationTypes = arrayOf("短震动", "长震动", "提醒模式", "完成模式", "警告模式", "心跳模式")
        val currentIndex = when (settingsManager.vibrationType) {
            "short" -> 0
            "long" -> 1
            "alert" -> 2
            "complete" -> 3
            "warning" -> 4
            "heartbeat" -> 5
            else -> 0
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("选择震动模式")
            .setSingleChoiceItems(vibrationTypes, currentIndex) { dialog, which ->
                val selectedType = when (which) {
                    0 -> "short"
                    1 -> "long"
                    2 -> "alert"
                    3 -> "complete"
                    4 -> "warning"
                    5 -> "heartbeat"
                    else -> "short"
                }
                settingsManager.vibrationType = selectedType
                binding.textVibrationTypeValue.text = vibrationTypes[which]
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun requestPermissions() {
        PermissionUtil.requestAllReminderPermissions(requireActivity(), PERMISSION_REQUEST_CODE)
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun showResetConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("重置设置")
            .setMessage("确定要重置所有设置为默认值吗？")
            .setPositiveButton("确定") { dialog, _ ->
                settingsManager.resetToDefaults()
                refreshSettings()
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun refreshSettings() {
        lifecycleScope.launch {
            // 重新加载所有设置
            setupSoundSettings()
            setupVibrationSettings()
            setupReminderSettings()
            setupOtherSettings()
            updatePermissionStatus()
        }
    }

    private fun getSoundTypeDisplayName(type: String): String {
        return when (type) {
            "beep" -> "蜂鸣声"
            "alarm" -> "警报声"
            "custom" -> "自定义"
            else -> "蜂鸣声"
        }
    }

    private fun getVibrationTypeDisplayName(type: String): String {
        return when (type) {
            "short" -> "短震动"
            "long" -> "长震动"
            "alert" -> "提醒模式"
            "complete" -> "完成模式"
            "warning" -> "警告模式"
            "heartbeat" -> "心跳模式"
            else -> "短震动"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            updatePermissionStatus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}