package com.timer.workout.model

/**
 * 震动模式配置
 */
data class VibrationPattern(
    val pattern: LongArray,
    val repeat: Int
) {
    companion object {
        /**
         * 短震动模式（100ms）
         */
        val SHORT = VibrationPattern(longArrayOf(0, 100), -1)

        /**
         * 长震动模式（500ms）
         */
        val LONG = VibrationPattern(longArrayOf(0, 500), -1)

        /**
         * 提醒模式（短-长-短）
         */
        val ALERT = VibrationPattern(longArrayOf(0, 200, 100, 300), 0)

        /**
         * 完成模式（长-短-长）
         */
        val COMPLETE = VibrationPattern(longArrayOf(0, 500, 200, 500), 0)

        /**
         * 警告模式（连续短震动）
         */
        val WARNING = VibrationPattern(longArrayOf(0, 100, 50, 100, 50, 100), 0)

        /**
         * 心跳模式
         */
        val HEARTBEAT = VibrationPattern(longArrayOf(0, 100, 200, 100), 0)

        /**
         * 根据类型获取震动模式
         */
        fun getPatternByType(type: VibrationType): VibrationPattern {
            return when (type) {
                VibrationType.SHORT -> SHORT
                VibrationType.LONG -> LONG
                VibrationType.ALERT -> ALERT
                VibrationType.COMPLETE -> COMPLETE
                VibrationType.WARNING -> WARNING
                VibrationType.HEARTBEAT -> HEARTBEAT
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VibrationPattern

        if (!pattern.contentEquals(other.pattern)) return false
        if (repeat != other.repeat) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pattern.contentHashCode()
        result = 31 * result + repeat
        return result
    }
}

/**
 * 震动类型枚举
 */
enum class VibrationType {
    SHORT,
    LONG,
    ALERT,
    COMPLETE,
    WARNING,
    HEARTBEAT
}