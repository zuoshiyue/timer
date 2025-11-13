package com.timer.workout.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.timer.workout.R
import com.timer.workout.data.model.WorkoutPlan
import com.timer.workout.data.model.WorkoutType
import com.timer.workout.databinding.DialogCreatePlanBinding
import java.util.Date

class CreatePlanDialog : BottomSheetDialogFragment() {

    private var _binding: DialogCreatePlanBinding? = null
    private val binding get() = _binding!!

    private var onPlanCreated: ((WorkoutPlan) -> Unit)? = null
    private var existingPlan: WorkoutPlan? = null

    companion object {
        private const val ARG_PLAN = "plan"

        fun newInstance(plan: WorkoutPlan? = null): CreatePlanDialog {
            val args = Bundle().apply {
                plan?.let { putParcelable(ARG_PLAN, it) }
            }
            return CreatePlanDialog().apply {
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
        _binding = DialogCreatePlanBinding.inflate(inflater, container, false)
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
        
        setupViews()
        setupListeners()
        populateExistingPlan()
    }

    private fun setupViews() {
        // 设置时长滑块监听
        binding.sliderDuration.addOnChangeListener { _, value, _ ->
            binding.tvDurationValue.text = "${value.toInt()}分钟"
        }

        // 设置默认选中简单类型
        binding.chipSimple.isChecked = true
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

        // 监听多阶段类型选择，显示多阶段对话框
        binding.chipMultiStage.setOnClickListener {
            if (binding.chipMultiStage.isChecked) {
                showMultiStageDialog()
            }
        }
    }

    private fun populateExistingPlan() {
        existingPlan?.let { plan ->
            binding.etPlanName.setText(plan.name)
            binding.etPlanDescription.setText(plan.description)
            binding.sliderDuration.value = plan.totalDuration.toFloat()
            binding.tvDurationValue.text = "${plan.totalDuration}分钟"
            binding.switchSound.isChecked = plan.soundEnabled
            binding.switchVibration.isChecked = plan.vibrationEnabled

            // 设置训练类型
            when (plan.type) {
                WorkoutType.SIMPLE -> binding.chipSimple.isChecked = true
                WorkoutType.MULTI_STAGE -> binding.chipMultiStage.isChecked = true
                WorkoutType.HIIT -> binding.chipHiit.isChecked = true
                WorkoutType.TABATA -> binding.chipTabata.isChecked = true
                WorkoutType.BODYWEIGHT -> binding.chipBodyweight.isChecked = true
                WorkoutType.INCLINE -> binding.chipIncline.isChecked = true
            }
        }
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

        return true
    }

    private fun savePlan() {
        val name = binding.etPlanName.text.toString().trim()
        val description = binding.etPlanDescription.text.toString().trim()
        val duration = binding.sliderDuration.value.toInt()
        val soundEnabled = binding.switchSound.isChecked
        val vibrationEnabled = binding.switchVibration.isChecked

        // 获取选中的训练类型
        val selectedType = when {
            binding.chipSimple.isChecked -> WorkoutType.SIMPLE
            binding.chipMultiStage.isChecked -> WorkoutType.MULTI_STAGE
            binding.chipHiit.isChecked -> WorkoutType.HIIT
            binding.chipTabata.isChecked -> WorkoutType.TABATA
            binding.chipBodyweight.isChecked -> WorkoutType.BODYWEIGHT
            binding.chipIncline.isChecked -> WorkoutType.INCLINE
            else -> WorkoutType.SIMPLE
        }

        val plan = existingPlan?.copy(
            name = name,
            description = description,
            type = selectedType,
            totalDuration = duration,
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
            updatedAt = Date()
        ) ?: WorkoutPlan(
            id = 0,
            name = name,
            description = description,
            type = selectedType,
            totalDuration = duration,
            stageCount = if (selectedType == WorkoutType.MULTI_STAGE) 0 else 1,
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled,
            createdAt = Date(),
            updatedAt = Date()
        )

        onPlanCreated?.invoke(plan)
        dismiss()
    }

    private fun showMultiStageDialog() {
        val multiStageDialog = MultiStagePlanDialog.newInstance(existingPlan)
        multiStageDialog.setOnPlanCreated { plan ->
            onPlanCreated?.invoke(plan)
            dismiss()
        }
        multiStageDialog.show(parentFragmentManager, "multi_stage_dialog")
    }

    fun setOnPlanCreated(listener: (WorkoutPlan) -> Unit) {
        onPlanCreated = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}