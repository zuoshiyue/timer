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

class WorkoutPlanAdapter(
    private val onPlanSelected: (WorkoutPlan) -> Unit
) : ListAdapter<WorkoutPlan, WorkoutPlanAdapter.PlanViewHolder>(PlanDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_plan, parent, false)
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
        private val btnStartPlan: MaterialButton = itemView.findViewById(R.id.btn_start_plan)
        private val btnEditPlan: MaterialButton = itemView.findViewById(R.id.btn_edit_plan)
        private val btnDeletePlan: MaterialButton = itemView.findViewById(R.id.btn_delete_plan)

        fun bind(plan: WorkoutPlan) {
            planName.text = plan.name
            planType.text = getTypeDisplayName(plan.type)
            planDescription.text = plan.description
            planDuration.text = formatDuration(plan.totalDuration)
            planStages.text = "${getStageCount(plan.type)}阶段"

            // 设置按钮点击事件
            btnStartPlan.setOnClickListener {
                onPlanSelected(plan)
            }

            btnEditPlan.setOnClickListener {
                // 编辑计划逻辑
                showEditDialog(plan)
            }

            btnDeletePlan.setOnClickListener {
                // 删除计划逻辑
                showDeleteDialog(plan)
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

        private fun showEditDialog(plan: WorkoutPlan) {
            // 实现编辑对话框
            android.widget.Toast.makeText(
                itemView.context,
                "编辑计划: ${plan.name}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        private fun showDeleteDialog(plan: WorkoutPlan) {
            // 实现删除确认对话框
            android.widget.Toast.makeText(
                itemView.context,
                "删除计划: ${plan.name}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
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