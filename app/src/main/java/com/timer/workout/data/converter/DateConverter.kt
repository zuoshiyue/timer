package com.timer.workout.data.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * Date类型转换器
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}