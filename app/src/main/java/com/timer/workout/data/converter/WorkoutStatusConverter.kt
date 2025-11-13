package com.timer.workout.data.converter

import androidx.room.TypeConverter
import com.timer.workout.data.model.WorkoutStatus

/**
 * WorkoutStatus枚举转换器
 */
class WorkoutStatusConverter {
    @TypeConverter
    fun fromWorkoutStatus(status: WorkoutStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toWorkoutStatus(name: String?): WorkoutStatus? {
        return name?.let { WorkoutStatus.valueOf(it) }
    }
}