package com.timer.workout.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.timer.workout.data.dao.WorkoutPlanDao
import com.timer.workout.data.dao.WorkoutRecordDao
import com.timer.workout.data.dao.WorkoutStageDao
import com.timer.workout.data.model.WorkoutPlan
import com.timer.workout.data.model.WorkoutRecord
import com.timer.workout.data.model.WorkoutStage

/**
 * 训练计时器数据库
 */
@Database(
    entities = [WorkoutPlan::class, WorkoutStage::class, WorkoutRecord::class],
    version = 1,
    exportSchema = false
)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract fun workoutPlanDao(): WorkoutPlanDao
    abstract fun workoutStageDao(): WorkoutStageDao
    abstract fun workoutRecordDao(): WorkoutRecordDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getInstance(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}