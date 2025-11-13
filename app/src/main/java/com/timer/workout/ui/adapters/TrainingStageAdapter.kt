package com.timer.workout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.timer.workout.R
import com.timer.workout.data.model.TrainingStage
import com.timer.workout.databinding.ItemTrainingStageBinding

class TrainingStageAdapter(
    private var stages: MutableList<TrainingStage>,
    private val onStageDeleted: (TrainingStage) -> Unit
) : RecyclerView.Adapter<TrainingStageAdapter.StageViewHolder>() {

    inner class StageViewHolder(private val binding: ItemTrainingStageBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stage: TrainingStage) {
            binding.tvStageTitle.text = stage.name
            binding.etStageName.setText(stage.name)
            binding.sliderStageDuration.value = stage.duration.toFloat()
            binding.tvStageDurationValue.text = "${stage.duration}秒"
            binding.tvRepeats.text = stage.repeats.toString()

            // 设置阶段类型
            setStageType(stage.type)

            // 设置监听器
            setupListeners(stage)
        }

        private fun setStageType(type: TrainingStage.Type) {
            binding.chipWork.isChecked = type == TrainingStage.Type.WORK
            binding.chipRest.isChecked = type == TrainingStage.Type.REST
            binding.chipWarmup.isChecked = type == TrainingStage.Type.WARMUP
            binding.chipCooldown.isChecked = type == TrainingStage.Type.COOLDOWN
        }

        private fun setupListeners(stage: TrainingStage) {
            // 阶段名称变化
            binding.etStageName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val newName = binding.etStageName.text.toString().trim()
                    if (newName.isNotEmpty()) {
                        stage.name = newName
                        binding.tvStageTitle.text = newName
                    }
                }
            }

            // 阶段时长变化
            binding.sliderStageDuration.addOnChangeListener { _, value, _ ->
                stage.duration = value.toInt()
                binding.tvStageDurationValue.text = "${stage.duration}秒"
            }

            // 阶段类型变化
            binding.chipGroupStageType.setOnCheckedStateChangeListener { group, checkedIds ->
                val chip = group.findViewById<Chip>(checkedIds.first())
                when (chip?.id) {
                    R.id.chip_work -> stage.type = TrainingStage.Type.WORK
                    R.id.chip_rest -> stage.type = TrainingStage.Type.REST
                    R.id.chip_warmup -> stage.type = TrainingStage.Type.WARMUP
                    R.id.chip_cooldown -> stage.type = TrainingStage.Type.COOLDOWN
                }
            }

            // 重复次数变化
            binding.btnDecreaseRepeats.setOnClickListener {
                if (stage.repeats > 1) {
                    stage.repeats--
                    binding.tvRepeats.text = stage.repeats.toString()
                }
            }

            binding.btnIncreaseRepeats.setOnClickListener {
                if (stage.repeats < 20) {
                    stage.repeats++
                    binding.tvRepeats.text = stage.repeats.toString()
                }
            }

            // 删除阶段
            binding.btnDeleteStage.setOnClickListener {
                if (stages.size > 1) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val deletedStage = stages[position]
                        stages.removeAt(position)
                        notifyItemRemoved(position)
                        onStageDeleted(deletedStage)
                    }
                } else {
                    Toast.makeText(binding.root.context, "至少需要保留一个阶段", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StageViewHolder {
        val binding = ItemTrainingStageBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return StageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StageViewHolder, position: Int) {
        holder.bind(stages[position])
    }

    override fun getItemCount(): Int = stages.size

    fun updateStages(newStages: List<TrainingStage>) {
        stages.clear()
        stages.addAll(newStages)
        notifyDataSetChanged()
    }

    fun addStage(stage: TrainingStage) {
        stages.add(stage)
        notifyItemInserted(stages.size - 1)
    }

    fun removeStage(position: Int) {
        if (position in 0 until stages.size) {
            stages.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getStages(): List<TrainingStage> = stages.toList()
}