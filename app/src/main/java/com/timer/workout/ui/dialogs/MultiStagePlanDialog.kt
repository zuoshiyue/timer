package com.timer.workout.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timer.workout.R
import com.timer.workout.data.model.TrainingStage
import com.timer.workout.data.model.WorkoutPlan
import com.timer.workout.data.model.WorkoutType
import com.timer.workout.databinding.DialogMultiStagePlanBinding
import com.timer.workout.ui.adapters.TrainingStageAdapter
import java.util.Date

class MultiStagePlanDialog : BottomSheetDialogFragment() {

    private var _binding: DialogMultiStagePlanBinding? = null
    private val binding get() = _binding!!

    private lateinit var stageAdapter: TrainingStageAdapter
    private val stages = mutableListOf<TrainingStage>()

    private var onPlanCreated: ((WorkoutPlan) -> Unit)? = null
    private var existingPlan: WorkoutPlan? = null

    companion object {
        private const val ARG_PLAN = "plan"

        fun newInstance(plan: WorkoutPlan? = null): MultiStagePlanDialog {
            val args = Bundle().apply {
                plan?.let { putParcelable(ARG_PLAN, it) }
            }
            return MultiStagePlanDialog().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            existingPlan = it.getParcelable(ARG_PLAN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMultiStagePlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                // 设置对话框高度
                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let { sheet ->
                    val layoutParams = sheet.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    sheet.layoutParams = layoutParams
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupListeners()
        populateExistingPlan()
    }

    private fun setupRecyclerView() {
        stageAdapter = TrainingStageAdapter(stages) { stage ->
            // 删除阶段
            val index = stages.indexOf(stage)
            if (index != -1) {
                stages.removeAt(index)
                stageAdapter.notifyItemRemoved(index)
                updateStageTitles()
            }
        }

        binding.rvStages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stageAdapter
        }
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                savePlan()
            }
        }

        binding.btnAddStage.setOnClickListener {
            addNewStage()
        }
    }

    private fun populateExistingPlan() {
        existingPlan?.let { plan ->
            binding.etPlanName.setText(plan.name)
            binding.etPlanDescription.setText(plan.description)
            binding.switchSound.isChecked = plan.soundEnabled
            binding.switchVibration.isChecked = plan.vibrationEnabled

            // 如果有阶段数据，加载阶段
            plan.stages?.let { existingStages ->
                stages.clear()
                stages.addAll(existingStages)
                stageAdapter.notifyDataSetChanged()
                updateStageTitles()
            }
        }

        // 如果没有阶段，添加一个默认阶段
        if (stages.isEmpty()) {
            addNewStage()
        }
    }

    private fun addNewStage() {
        val newStage = TrainingStage(
            id = stages.size + 1,
            name = "阶段 ${stages.size + 1}",
            type = TrainingStage.Type.WORK,
            duration = 60,
            repeats = 1,
            order = stages.size
        )
        
        stages.add(newStage)
        stageAdapter.notifyItemInserted(stages.size - 1)
        updateStageTitles()
    }

    private fun updateStageTitles() {
        stages.forEachIndexed { index, stage ->
            stage.name = "阶段 ${index + 1}"
            stage.order = index
        }
        stageAdapter.notifyDataSetChanged()
    }

    private fun validateInput(): Boolean {
        val name = binding.etPlanName.text.toString().trim()
        
        if (name.isEmpty()) {
            binding.etPlanName.error = "请输入计划名称"
            return false
        }

        if (name.length > 50) {
            binding.etPlanName.error = "计划名称不能超过50个字符"
            return false
        }

        val description = binding.etPlanDescription.text.toString().trim()
        if (description.length > 200) {
            binding.etPlanDescription.error = "描述不能超过200个字符"
            return false
        }

        if (stages.isEmpty()) {
            Toast.makeText(requireContext(), "请至少添加一个训练阶段", Toast.LENGTH_SHORT).show()
            return false
        }

        // 验证每个阶段
        stages.forEach { stage ->
            if (stage.name.isEmpty()) {
                Toast.makeText(requireContext(), "请填写所有阶段的名称", Toast.LENGTH_SHORT).show()
                return false
            }
            if (stage.duration <= 0) {
                Toast.makeText(requireContext(), "阶段时长必须大于0", Toast.LENGTH_SHORT).show()
                return false
            }
            if (stage.repeats <= 0) {
                Toast.makeText(requireContext(), "重复次数必须大于0", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        return true
    }

    private fun savePlan() {
        val name = binding.etPlanName.text.toString().trim()
        val description = binding.etPlanDescription.text.toString().trim()
        val soundEnabled = binding.switchSound.isChecked
        val vibrationEnabled = binding.switchVibration.isChecked

        // 计算总时长
        val totalDuration = stages.sumOf { stage -> stage.duration * stage.repeats } / 60

        val plan = existingPlan?.copy(
            name = name,
            description = description,
            type = WorkoutType.MULTI_STAGE,
            totalDuration = totalDuration,
            stageCount = stages.size,
            stages = stages.toList(),
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
            updatedAt = Date()
        ) ?: WorkoutPlan(
            id = 0,
            name = name,
            description = description,
            type = WorkoutType.MULTI_STAGE,
            totalDuration = totalDuration,
            stageCount = stages.size,
            stages = stages.toList(),
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
            createdAt = Date(),
            updatedAt = Date()
        )

        onPlanCreated?.invoke(plan)
        dismiss()
    }

    fun setOnPlanCreated(listener: (WorkoutPlan) -> Unit) {
        onPlanCreated = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}