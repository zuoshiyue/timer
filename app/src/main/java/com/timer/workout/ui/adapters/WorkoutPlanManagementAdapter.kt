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
import com.timer.workout.data.model.WorkoutPlan
import com.timer.workout.data.model.WorkoutType

class WorkoutPlanManagementAdapter(
    private val onPlanEdit: (WorkoutPlan) -> Unit,
    private val onPlanDelete: (WorkoutPlan) -> Unit,
    private val onPlanDuplicate: (WorkoutPlan) -> Unit
) : ListAdapter<WorkoutPlan, WorkoutPlanManagementAdapter.PlanViewHolder>(PlanDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_plan_management, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = getItem(position)
        holder.bind(plan)
    }

    inner class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val planName: TextView = itemView.findViewById(R.id.plan_name)
        private val planType: TextView = itemView.findViewById(R.id.plan_type)
        private val planDescription: TextView = itemView.findViewById(R.id.plan_description)
        private val planDuration: TextView = itemView.findViewById(R.id.plan_duration)
        private val planStages: TextView = itemView.findViewById(R.id.plan_stages)
        private val planCreatedAt: TextView = itemView.findViewById(R.id.plan_created_at)
        private val btnEdit: MaterialButton = itemView.findViewById(R.id.btn_edit)
        private val btnDelete: MaterialButton = itemView.findViewById(R.id.btn_delete)
        private val btnDuplicate: MaterialButton = itemView.findViewById(R.id.btn_duplicate)

        fun bind(plan: WorkoutPlan) {
            planName.text = plan.name
            planType.text = getTypeDisplayName(plan.type)
            planDescription.text = plan.description
            planDuration.text = formatDuration(plan.totalDuration)
            planStages.text = "${getStageCount(plan.type)}阶段"
            planCreatedAt.text = formatDate(plan.createdAt)

            // 设置按钮点击事件
            btnEdit.setOnClickListener {
                onPlanEdit(plan)
            }

            btnDelete.setOnClickListener {
                onPlanDelete(plan)
            }

            btnDuplicate.setOnClickListener {
                onPlanDuplicate(plan)
            }

            // 根据计划类型设置不同的背景色
            setTypeBackground(plan.type)
        }

        private fun getTypeDisplayName(type: WorkoutType): String {
            return when (type) {
                WorkoutType.SIMPLE -> "简单"
                WorkoutType.MULTI_STAGE -> "多阶段"
                WorkoutType.HIIT -> "HIIT"
                WorkoutType.TABATA -> "TABATA"
                WorkoutType.BODYWEIGHT -> "自重"
                WorkoutType.INCLINE -> "坡度"
            }
        }

        private fun formatDuration(millis: Long): String {
            val minutes = millis / 60000
            return "${minutes}分钟"
        }

        private fun getStageCount(type: WorkoutType): Int {
            return when (type) {
                WorkoutType.SIMPLE -> 1
                WorkoutType.MULTI_STAGE -> 4
                WorkoutType.HIIT -> 4
                WorkoutType.TABATA -> 8
                WorkoutType.BODYWEIGHT -> 3
                WorkoutType.INCLINE -> 5
            }
        }

        private fun formatDate(timestamp: Long): String {
            val date = java.util.Date(timestamp)
            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            return formatter.format(date)
        }

        private fun setTypeBackground(type: WorkoutType) {
            val colorRes = when (type) {
                WorkoutType.SIMPLE -> R.color.type_simple
                WorkoutType.MULTI_STAGE -> R.color.type_multi_stage
                WorkoutType.HIIT -> R.color.type_hiit
                WorkoutType.TABATA -> R.color.type_tabata
                WorkoutType.BODYWEIGHT -> R.color.type_bodyweight
                WorkoutType.INCLINE -> R.color.type_incline
            }
            planType.setBackgroundColor(itemView.context.getColor(colorRes))
        }
    }

    companion object {
        private object PlanDiffCallback : DiffUtil.ItemCallback<WorkoutPlan>() {
            override fun areItemsTheSame(oldItem: WorkoutPlan, newItem: WorkoutPlan): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: WorkoutPlan, newItem: WorkoutPlan): Boolean {
                return oldItem == newItem
            }
        }
    }
}