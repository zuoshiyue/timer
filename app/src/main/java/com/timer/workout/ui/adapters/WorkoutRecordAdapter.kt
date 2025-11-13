package com.timer.workout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.timer.workout.R
import com.timer.workout.data.model.WorkoutRecord
import com.timer.workout.data.model.WorkoutStatus

class WorkoutRecordAdapter(
    private val onRecordClick: (WorkoutRecord) -> Unit,
    private val onRecordDelete: (WorkoutRecord) -> Unit
) : ListAdapter<WorkoutRecord, WorkoutRecordAdapter.RecordViewHolder>(RecordDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = getItem(position)
        holder.bind(record)
    }

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val planName: TextView = itemView.findViewById(R.id.record_plan_name)
        private val recordType: TextView = itemView.findViewById(R.id.record_type)
        private val recordDate: TextView = itemView.findViewById(R.id.record_date)
        private val recordDuration: TextView = itemView.findViewById(R.id.record_duration)
        private val recordCompletion: TextView = itemView.findViewById(R.id.record_completion)
        private val recordCalories: TextView = itemView.findViewById(R.id.record_calories)
        private val recordStatus: TextView = itemView.findViewById(R.id.record_status)
        private val btnDetails: MaterialButton = itemView.findViewById(R.id.btn_details)
        private val btnDelete: MaterialButton = itemView.findViewById(R.id.btn_delete)

        fun bind(record: WorkoutRecord) {
            planName.text = record.planName
            recordType.text = getTypeDisplayName(record.type)
            recordDate.text = formatDate(record.startTime)
            recordDuration.text = formatDuration(record.totalDuration ?: 0)
            recordCompletion.text = "${record.completionRate ?: 0}%"
            recordCalories.text = "${record.caloriesBurned?.toInt() ?: 0} kcal"
            recordStatus.text = getStatusDisplayName(record.status)

            // 设置状态颜色
            setStatusColor(record.status)

            // 设置点击事件
            itemView.setOnClickListener {
                onRecordClick(record)
            }

            btnDetails.setOnClickListener {
                onRecordClick(record)
            }

            btnDelete.setOnClickListener {
                onRecordDelete(record)
            }
        }

        private fun getTypeDisplayName(type: com.timer.workout.data.model.WorkoutType): String {
            return when (type) {
                com.timer.workout.data.model.WorkoutType.SIMPLE -> "简单训练"
                com.timer.workout.data.model.WorkoutType.MULTI_STAGE -> "多阶段训练"
                com.timer.workout.data.model.WorkoutType.HIIT -> "HIIT训练"
                com.timer.workout.data.model.WorkoutType.TABATA -> "TABATA训练"
                com.timer.workout.data.model.WorkoutType.BODYWEIGHT -> "自重训练"
                com.timer.workout.data.model.WorkoutType.INCLINE -> "坡度训练"
            }
        }

        private fun formatDate(date: java.util.Date): String {
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
            return formatter.format(date)
        }

        private fun formatDuration(millis: Long): String {
            val minutes = millis / 60000
            return "${minutes}分钟"
        }

        private fun getStatusDisplayName(status: WorkoutStatus): String {
            return when (status) {
                WorkoutStatus.COMPLETED -> "已完成"
                WorkoutStatus.CANCELLED -> "已取消"
            }
        }

        private fun setStatusColor(status: WorkoutStatus) {
            val colorRes = when (status) {
                WorkoutStatus.COMPLETED -> R.color.success_color
                WorkoutStatus.CANCELLED -> R.color.error_color
            }
            recordStatus.setTextColor(itemView.context.getColor(colorRes))
        }
    }

    companion object {
        private object RecordDiffCallback : DiffUtil.ItemCallback<WorkoutRecord>() {
            override fun areItemsTheSame(oldItem: WorkoutRecord, newItem: WorkoutRecord): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: WorkoutRecord, newItem: WorkoutRecord): Boolean {
                return oldItem == newItem
            }
        }
    }
}