package com.timer.workout.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.os.VibrationEffect
import androidx.core.app.NotificationCompat
import com.timer.workout.R
import com.timer.workout.data.model.StageType
import com.timer.workout.service.model.CurrentTimerState
import com.timer.workout.service.model.TimerConfig
import com.timer.workout.service.model.TimerState
import com.timer.workout.ui.MainActivity
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * 计时器服务 - 负责后台计时和通知管理
 */
class TimerService : Service(), CoroutineScope {
    
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    
    companion object {
        const val ACTION_START_TIMER = "ACTION_START_TIMER"
        const val ACTION_PAUSE_TIMER = "ACTION_PAUSE_TIMER"
        const val ACTION_RESUME_TIMER = "ACTION_RESUME_TIMER"
        const val ACTION_RESET_TIMER = "ACTION_RESET_TIMER"
        const val ACTION_STOP_TIMER = "ACTION_STOP_TIMER"
        
        const val EXTRA_TIMER_CONFIG = "EXTRA_TIMER_CONFIG"
        const val NOTIFICATION_ID = 1001
        const val TIMER_CHANNEL_ID = "timer_notifications"
    }
    
    private lateinit var notificationManager: NotificationManager
    private lateinit var reminderManager: ReminderManager
    
    private var timerConfig: TimerConfig? = null
    private var currentState = CurrentTimerState()
    private var timerJob: Job? = null
    private var isForegroundService = false
    
    // 状态监听器
    private val stateListeners = mutableListOf<(CurrentTimerState) -> Unit>()
    
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        reminderManager = ReminderManager.getInstance(this)
        
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> {
                val config = intent.getParcelableExtra<TimerConfig>(EXTRA_TIMER_CONFIG)
                config?.let { startTimer(it) }
            }
            ACTION_PAUSE_TIMER -> pauseTimer()
            ACTION_RESUME_TIMER -> resumeTimer()
            ACTION_RESET_TIMER -> resetTimer()
            ACTION_STOP_TIMER -> stopTimer()
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        timerJob?.cancel()
        reminderManager.stopAllReminders()
    }
    
    /**
     * 开始计时器
     */
    private fun startTimer(config: TimerConfig) {
        timerConfig = config
        currentState = CurrentTimerState(
            state = TimerState.RUNNING,
            currentStageIndex = 0,
            currentCycle = 1,
            remainingTime = config.stages.firstOrNull()?.duration ?: 0,
            currentStageType = config.stages.firstOrNull()?.stageType,
            currentStageName = config.stages.firstOrNull()?.name ?: "",
            totalStages = config.stages.size,
            totalCycles = config.stages.sumOf { it.cycles }
        )
        
        startForegroundService()
        startTimerJob()
        notifyStateChange()
    }
    
    /**
     * 暂停计时器
     */
    private fun pauseTimer() {
        if (currentState.state == TimerState.RUNNING) {
            currentState = currentState.copy(state = TimerState.PAUSED)
            timerJob?.cancel()
            updateNotification()
            notifyStateChange()
        }
    }
    
    /**
     * 继续计时器
     */
    private fun resumeTimer() {
        if (currentState.state == TimerState.PAUSED) {
            currentState = currentState.copy(state = TimerState.RUNNING)
            startTimerJob()
            updateNotification()
            notifyStateChange()
        }
    }
    
    /**
     * 重置计时器
     */
    private fun resetTimer() {
        timerJob?.cancel()
        currentState = CurrentTimerState()
        timerConfig = null
        stopForegroundService()
        notifyStateChange()
    }
    
    /**
     * 停止计时器
     */
    private fun stopTimer() {
        timerJob?.cancel()
        currentState = currentState.copy(state = TimerState.COMPLETED)
        stopForegroundService()
        notifyStateChange()
        stopSelf()
    }
    
    /**
     * 启动计时器任务
     */
    private fun startTimerJob() {
        timerJob = launch {
            while (currentState.state == TimerState.RUNNING && currentState.remainingTime > 0) {
                delay(1000) // 1秒间隔
                
                currentState = currentState.copy(
                    remainingTime = currentState.remainingTime - 1,
                    totalElapsedTime = currentState.totalElapsedTime + 1
                )
                
                // 检查是否需要切换阶段
                if (currentState.remainingTime <= 0) {
                    handleStageTransition()
                }
                
                updateNotification()
                notifyStateChange()
            }
        }
    }
    
    /**
     * 处理阶段切换
     */
    private fun handleStageTransition() {
        val config = timerConfig ?: return
        val currentStageIndex = currentState.currentStageIndex
        
        // 播放提醒声音和震动
        playNotification()
        
        if (currentStageIndex < config.stages.size - 1) {
            // 切换到下一个阶段
            val nextStage = config.stages[currentStageIndex + 1]
            currentState = currentState.copy(
                currentStageIndex = currentStageIndex + 1,
                remainingTime = nextStage.duration,
                currentStageType = nextStage.stageType,
                currentStageName = nextStage.name
            )
        } else {
            // 所有阶段完成
            currentState = currentState.copy(state = TimerState.COMPLETED)
            stopForegroundService()
        }
    }
    
    /**
     * 播放提醒（声音和震动）
     */
    private fun playNotification() {
        val config = timerConfig ?: return
        
        // 根据当前阶段类型播放相应的提醒
        when (currentState.currentStageType) {
            StageType.WORKOUT -> {
                // 训练阶段完成提醒
                reminderManager.playStageCompleteReminder()
            }
            StageType.REST -> {
                // 休息阶段完成提醒
                reminderManager.playStageCompleteReminder()
            }
            StageType.WARMUP -> {
                // 热身阶段完成提醒
                reminderManager.playStageCompleteReminder()
            }
            StageType.COOLDOWN -> {
                // 冷身阶段完成提醒
                reminderManager.playStageCompleteReminder()
            }
            null -> {
                // 训练完成提醒
                reminderManager.playWorkoutCompleteReminder()
            }
        }
    }
    
    /**
     * 启动前台服务
     */
    private fun startForegroundService() {
        if (!isForegroundService) {
            val notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)
            isForegroundService = true
        }
    }
    
    /**
     * 停止前台服务
     */
    private fun stopForegroundService() {
        if (isForegroundService) {
            stopForeground(true)
            isForegroundService = false
        }
    }
    
    /**
     * 创建通知
     */
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        
        val timeText = formatTime(currentState.remainingTime)
        val stageText = currentState.currentStageName
        
        return NotificationCompat.Builder(this, TIMER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("训练计时器")
            .setContentText("$stageText - $timeText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .build()
    }
    
    /**
     * 更新通知
     */
    private fun updateNotification() {
        if (isForegroundService) {
            val notification = createNotification()
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }
    
    /**
     * 创建通知通道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TIMER_CHANNEL_ID,
                "计时器提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "训练计时器的时间提醒"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * 格式化时间显示
     */
    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
    
    /**
     * 通知状态变化
     */
    private fun notifyStateChange() {
        stateListeners.forEach { listener ->
            listener(currentState)
        }
    }
    
    /**
     * 添加状态监听器
     */
    fun addStateListener(listener: (CurrentTimerState) -> Unit) {
        stateListeners.add(listener)
    }
    
    /**
     * 移除状态监听器
     */
    fun removeStateListener(listener: (CurrentTimerState) -> Unit) {
        stateListeners.remove(listener)
    }
    
    /**
     * 获取当前状态
     */
    fun getCurrentState(): CurrentTimerState = currentState
}