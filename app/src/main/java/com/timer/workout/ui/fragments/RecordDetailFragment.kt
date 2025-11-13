package com.timer.workout.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.timer.workout.databinding.FragmentRecordDetailBinding
import com.timer.workout.data.model.WorkoutRecord
import com.timer.workout.data.model.WorkoutStatus
import com.timer.workout.data.model.WorkoutType
import com.timer.workout.util.DateUtil
import kotlinx.coroutines.launch

/**
 * 训练记录详情页面
 */
class RecordDetailFragment : Fragment() {

    private var _binding: FragmentRecordDetailBinding? = null
    private val binding get() = _binding!!

    private val args: RecordDetailFragmentArgs by navArgs()
    private lateinit var record: WorkoutRecord

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 获取记录数据
        record = args.workoutRecord
        
        setupViews()
        setupClickListeners()
        displayRecordDetails()
    }

    /**
     * 初始化视图
     */
    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.title = "训练记录详情"
    }

    /**
     * 设置点击监听器
     */
    private fun setupClickListeners() {
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.btnShare.setOnClickListener {
            shareRecord()
        }
    }

    /**
     * 显示记录详情
     */
    private fun displayRecordDetails() {
        // 基本信息
        binding.tvPlanName.text = record.planName
        binding.tvWorkoutType.text = getWorkoutTypeDisplayName(record.type)
        binding.tvWorkoutDate.text = DateUtil.formatDateTime(record.startTime)
        binding.tvDuration.text = formatDuration(record.actualDuration)
        binding.tvStatus.text = getStatusDisplayName(record.status)

        // 设置状态颜色
        setStatusColor(record.status)

        // 统计信息
        binding.tvCompletionRate.text = "${(record.completionRate * 100).toInt()}%"
        binding.tvCaloriesBurned.text = "${record.caloriesBurned.toInt()} kcal"
        binding.tvIntensity.text = getIntensityDisplayName(record.intensity)

        // 进度信息
        binding.tvTotalStages.text = "${record.totalDuration / 60} 分钟"
        binding.tvCompletedStages.text = "${record.completedStages} 阶段"
        binding.tvActualDuration.text = formatDuration(record.actualDuration)

        // 备注信息
        if (record.notes.isNullOrEmpty()) {
            binding.tvNotes.visibility = View.GONE
            binding.labelNotes.visibility = View.GONE
        } else {
            binding.tvNotes.text = record.notes
            binding.tvNotes.visibility = View.VISIBLE
            binding.labelNotes.visibility = View.VISIBLE
        }

        // 设置进度条
        setupProgressBars()
    }

    /**
     * 设置进度条
     */
    private fun setupProgressBars() {
        // 完成度进度条
        val completionRate = record.completionRate.coerceIn(0f, 1f)
        binding.progressCompletion.progress = (completionRate * 100).toInt()

        // 强度进度条
        val intensityProgress = (record.intensity.coerceIn(1, 5) - 1) * 25
        binding.progressIntensity.progress = intensityProgress
    }

    /**
     * 设置状态颜色
     */
    private fun setStatusColor(status: WorkoutStatus) {
        val colorRes = when (status) {
            WorkoutStatus.COMPLETED -> com.timer.workout.R.color.success
            WorkoutStatus.CANCELLED -> com.timer.workout.R.color.error
        }
        
        binding.tvStatus.setTextColor(
            resources.getColor(colorRes, null)
        )
    }

    /**
     * 显示删除确认对话框
     */
    private fun showDeleteConfirmationDialog() {
        // 这里应该显示一个确认对话框
        // 暂时直接删除
        deleteRecord()
    }

    /**
     * 删除记录
     */
    private fun deleteRecord() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 这里应该调用DAO删除记录
                // workoutRecordDao.delete(record)
                
                // 显示删除成功消息
                showMessage("记录删除成功")
                
                // 返回上一页
                findNavController().navigateUp()
            } catch (e: Exception) {
                showMessage("删除失败: ${e.message}")
            }
        }
    }

    /**
     * 分享记录
     */
    private fun shareRecord() {
        val shareText = buildShareText()
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        
        startActivity(Intent.createChooser(shareIntent, "分享训练记录"))
    }

    /**
     * 构建分享文本
     */
    private fun buildShareText(): String {
        return """
            🏋️ 训练记录分享
            
            计划名称：${record.planName}
            训练类型：${getWorkoutTypeDisplayName(record.type)}
            训练时间：${DateUtil.formatDateTime(record.startTime)}
            训练时长：${formatDuration(record.actualDuration)}
            完成度：${(record.completionRate * 100).toInt()}%
            消耗卡路里：${record.caloriesBurned.toInt()} kcal
            训练强度：${getIntensityDisplayName(record.intensity)}
            
            使用训练间隔计时器APP记录
        """.trimIndent()
    }

    /**
     * 显示消息
     */
    private fun showMessage(message: String) {
        // 这里应该使用Toast或Snackbar显示消息
        // Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 获取训练类型显示名称
     */
    private fun getWorkoutTypeDisplayName(type: WorkoutType): String {
        return when (type) {
            WorkoutType.SIMPLE -> "简单训练"
            WorkoutType.MULTI_STAGE -> "多阶段训练"
            WorkoutType.HIIT -> "HIIT训练"
            WorkoutType.TABATA -> "Tabata训练"
            WorkoutType.BODYWEIGHT -> "自重训练"
            WorkoutType.INCLINE -> "坡度训练"
        }
    }

    /**
     * 获取状态显示名称
     */
    private fun getStatusDisplayName(status: WorkoutStatus): String {
        return when (status) {
            WorkoutStatus.COMPLETED -> "已完成"
            WorkoutStatus.CANCELLED -> "已取消"
        }
    }

    /**
     * 获取强度显示名称
     */
    private fun getIntensityDisplayName(intensity: Int): String {
        return when (intensity) {
            1 -> "轻松"
            2 -> "轻度"
            3 -> "中等"
            4 -> "高强度"
            5 -> "极限"
            else -> "未知"
        }
    }

    /**
     * 格式化时长
     */
    private fun formatDuration(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return if (minutes > 0) {
            "${minutes}分钟${remainingSeconds}秒"
        } else {
            "${remainingSeconds}秒"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "RecordDetailFragment"
    }
}