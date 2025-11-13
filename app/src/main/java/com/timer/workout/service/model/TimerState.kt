package com.timer.workout.service.model

import com.timer.workout.data.model.StageType

/**
 * 计时器状态枚举
 */
enum class TimerState {
    READY,      // 准备开始
    RUNNING,    // 运行中
    PAUSED,     // 已暂停
    COMPLETED   // 已完成
}

/**
 * 计时器当前状态数据类
 */
data class CurrentTimerState(
    val state: TimerState = TimerState.READY,
    val currentStageIndex: Int = 0,
    val currentCycle: Int = 1,
    val remainingTime: Long = 0,
    val totalElapsedTime: Long = 0,
    val currentStageType: StageType? = null,
    val currentStageName: String = "",
    val totalStages: Int = 0,
    val totalCycles: Int = 1
)

/**
 * 计时器配置数据类
 */
data class TimerConfig(
    val planId: Long,
    val planName: String,
    val stages: List<TimerStage>,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

/**
 * 计时器阶段数据类
 */
data class TimerStage(
    val stageId: Long,
    val name: String,
    val duration: Long, // 秒
    val stageType: StageType,
    val cycles: Int = 1,
    val workDuration: Long? = null, // 用于间歇训练
    val restDuration: Long? = null   // 用于间歇训练
)