package com.timer.workout.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.timer.workout.R
import com.timer.workout.data.model.WorkoutPlan
import com.timer.workout.data.model.WorkoutType
import com.timer.workout.service.TimerService
import com.timer.workout.ui.adapters.WorkoutPlanAdapter
import kotlinx.coroutines.launch

class TimerFragment : Fragment() {

    private lateinit var timerService: TimerService
    private lateinit var planAdapter: WorkoutPlanAdapter
    private var countDownTimer: CountDownTimer? = null
    private var remainingTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        timerService = TimerService(requireContext())
        setupViews(view)
        setupRecyclerView()
        loadSampleData()
    }

    private fun setupViews(view: View) {
        // 设置按钮点击事件
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_start).setOnClickListener {
            startTimer()
        }
        
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pause).setOnClickListener {
            pauseTimer()
        }
        
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_reset).setOnClickListener {
            resetTimer()
        }
        
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_quick_add).setOnClickListener {
            showQuickAddDialog()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = requireView().findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.plan_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        planAdapter = WorkoutPlanAdapter { plan ->
            startWorkoutPlan(plan)
        }
        recyclerView.adapter = planAdapter
    }

    private fun loadSampleData() {
        val samplePlans = listOf(
            WorkoutPlan(
                id = 1,
                name = "HIIT燃脂训练",
                description = "高强度间歇训练，包含4个阶段",
                totalDuration = 1800000, // 30分钟
                type = WorkoutType.HIIT,
                soundEnabled = true,
                vibrationEnabled = true,
                createdAt = System.currentTimeMillis()
            ),
            WorkoutPlan(
                id = 2,
                name = "TABATA核心训练",
                description = "TABATA训练法，8个循环",
                totalDuration = 1200000, // 20分钟
                type = WorkoutType.TABATA,
                soundEnabled = true,
                vibrationEnabled = true,
                createdAt = System.currentTimeMillis()
            ),
            WorkoutPlan(
                id = 3,
                name = "有氧耐力训练",
                description = "恒定速度有氧训练",
                totalDuration = 2700000, // 45分钟
                type = WorkoutType.SIMPLE,
                soundEnabled = true,
                vibrationEnabled = false,
                createdAt = System.currentTimeMillis()
            )
        )
        planAdapter.submitList(samplePlans)
    }

    private fun startTimer() {
        if (remainingTime == 0L) {
            remainingTime = 150000 // 2分30秒
        }
        
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateTimerDisplay(millisUntilFinished)
            }

            override fun onFinish() {
                remainingTime = 0
                updateTimerDisplay(0)
                // 播放完成音效
                timerService.playCompletionSound()
            }
        }.start()
        
        updateButtonStates(true)
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        updateButtonStates(false)
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        remainingTime = 0
        updateTimerDisplay(0)
        updateButtonStates(false)
    }

    private fun updateTimerDisplay(millisUntilFinished: Long) {
        val minutes = (millisUntilFinished / 1000) / 60
        val seconds = (millisUntilFinished / 1000) % 60
        val displayText = "%02d:%02d".format(minutes, seconds)
        
        requireView().findViewById<android.widget.TextView>(R.id.timer_display).text = displayText
    }

    private fun updateButtonStates(isRunning: Boolean) {
        val btnStart = requireView().findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_start)
        val btnPause = requireView().findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_pause)
        val btnReset = requireView().findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_reset)
        
        btnStart.isEnabled = !isRunning
        btnPause.isEnabled = isRunning
        btnReset.isEnabled = true
        
        btnStart.alpha = if (isRunning) 0.5f else 1.0f
        btnPause.alpha = if (isRunning) 1.0f else 0.5f
    }

    private fun startWorkoutPlan(plan: WorkoutPlan) {
        lifecycleScope.launch {
            try {
                timerService.startWorkout(plan)
                // 更新界面显示当前训练计划
                updateCurrentWorkoutInfo(plan)
            } catch (e: Exception) {
                // 处理错误
                showError("启动训练失败: ${e.message}")
            }
        }
    }

    private fun updateCurrentWorkoutInfo(plan: WorkoutPlan) {
        requireView().findViewById<android.widget.TextView>(R.id.current_stage).text = "训练: ${plan.name}"
        requireView().findViewById<android.widget.TextView>(R.id.progress_text).text = "准备开始"
    }

    private fun showQuickAddDialog() {
        // 实现快速添加对话框
        // 这里可以添加快速创建训练计划的逻辑
        showMessage("快速添加功能开发中")
    }

    private fun showMessage(message: String) {
        // 实现消息提示
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        // 实现错误提示
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}