package com.timer.workout.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.timer.workout.R
import com.timer.workout.data.database.WorkoutDatabase
import com.timer.workout.data.model.WorkoutType
import com.timer.workout.ui.adapters.WorkoutPlanManagementAdapter
import kotlinx.coroutines.launch

class PlansManagementFragment : Fragment() {

    private lateinit var adapter: WorkoutPlanManagementAdapter
    private lateinit var database: WorkoutDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plans_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        database = WorkoutDatabase.getInstance(requireContext())
        setupViews(view)
        setupRecyclerView()
        loadPlans()
    }

    private fun setupViews(view: View) {
        // 创建计划按钮
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_create_plan).setOnClickListener {
            showCreatePlanDialog()
        }

        // 搜索功能
        view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_search).setOnEditorActionListener { _, _, _ ->
            performSearch()
            false
        }

        // 筛选标签
        setupFilterChips(view)
    }

    private fun setupFilterChips(view: View) {
        val chipGroup = view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chip_group_filter)
        
        // 初始化筛选标签
        val chips = listOf(
            view.findViewById<Chip>(R.id.chip_all),
            view.findViewById<Chip>(R.id.chip_hiit),
            view.findViewById<Chip>(R.id.chip_tabata),
            view.findViewById<Chip>(R.id.chip_simple)
        )
        
        // 设置默认选中"全部"
        chips.find { it.text == "全部" }?.isChecked = true
        
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChip = group.findViewById<Chip>(checkedIds[0])
                filterPlansByType(selectedChip.text.toString())
            }
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = requireView().findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_plans)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = WorkoutPlanManagementAdapter(
            onPlanEdit = { plan ->
                showEditPlanDialog(plan)
            },
            onPlanDelete = { plan ->
                showDeleteConfirmation(plan)
            },
            onPlanDuplicate = { plan ->
                duplicatePlan(plan)
            }
        )
        
        recyclerView.adapter = adapter
    }

    private fun loadPlans() {
        lifecycleScope.launch {
            try {
                val plans = database.workoutPlanDao().getAllPlans()
                adapter.submitList(plans)
                updateEmptyState(plans.isEmpty())
            } catch (e: Exception) {
                showError("加载计划失败: ${e.message}")
            }
        }
    }

    private fun performSearch() {
        val query = requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_search).text.toString()
        
        lifecycleScope.launch {
            try {
                val plans = if (query.isBlank()) {
                    database.workoutPlanDao().getAllPlans()
                } else {
                    database.workoutPlanDao().getAllPlans().filter { plan ->
                        plan.name.contains(query, ignoreCase = true) ||
                        plan.description.contains(query, ignoreCase = true)
                    }
                }
                adapter.submitList(plans)
                updateEmptyState(plans.isEmpty())
            } catch (e: Exception) {
                showError("搜索失败: ${e.message}")
            }
        }
    }

    private fun filterPlansByType(type: String) {
        lifecycleScope.launch {
            try {
                val allPlans = database.workoutPlanDao().getAllPlans()
                val filteredPlans = when (type) {
                    "全部" -> allPlans
                    "HIIT" -> allPlans.filter { it.type == WorkoutType.HIIT }
                    "TABATA" -> allPlans.filter { it.type == WorkoutType.TABATA }
                    "简单" -> allPlans.filter { it.type == WorkoutType.SIMPLE }
                    else -> allPlans
                }
                adapter.submitList(filteredPlans)
                updateEmptyState(filteredPlans.isEmpty())
            } catch (e: Exception) {
                showError("筛选失败: ${e.message}")
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        val emptyState = requireView().findViewById<View>(R.id.empty_state)
        val recyclerView = requireView().findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_plans)
        
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showCreatePlanDialog() {
        val dialog = com.timer.workout.ui.dialogs.CreatePlanDialog.newInstance()
        dialog.setOnPlanCreated { plan ->
            savePlan(plan)
        }
        dialog.show(parentFragmentManager, "create_plan")
    }

    private fun showEditPlanDialog(plan: com.timer.workout.data.model.WorkoutPlan) {
        val dialog = com.timer.workout.ui.dialogs.CreatePlanDialog.newInstance(plan)
        dialog.setOnPlanCreated { updatedPlan ->
            updatePlan(updatedPlan)
        }
        dialog.show(parentFragmentManager, "edit_plan")
    }

    private fun savePlan(plan: com.timer.workout.data.model.WorkoutPlan) {
        lifecycleScope.launch {
            try {
                database.workoutPlanDao().insertPlan(plan)
                loadPlans()
                showMessage("计划创建成功")
            } catch (e: Exception) {
                showError("保存失败: ${e.message}")
            }
        }
    }

    private fun updatePlan(plan: com.timer.workout.data.model.WorkoutPlan) {
        lifecycleScope.launch {
            try {
                database.workoutPlanDao().updatePlan(plan)
                loadPlans()
                showMessage("计划更新成功")
            } catch (e: Exception) {
                showError("更新失败: ${e.message}")
            }
        }
    }

    private fun showDeleteConfirmation(plan: com.timer.workout.data.model.WorkoutPlan) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("删除计划")
            .setMessage("确定要删除计划 \"${plan.name}\" 吗？此操作不可恢复。")
            .setPositiveButton("删除") { _, _ ->
                deletePlan(plan)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deletePlan(plan: com.timer.workout.data.model.WorkoutPlan) {
        lifecycleScope.launch {
            try {
                database.workoutPlanDao().deletePlan(plan)
                loadPlans()
                showMessage("计划删除成功")
            } catch (e: Exception) {
                showError("删除失败: ${e.message}")
            }
        }
    }

    private fun duplicatePlan(plan: com.timer.workout.data.model.WorkoutPlan) {
        val duplicatedPlan = plan.copy(
            id = 0, // 新ID将由数据库自动生成
            name = "${plan.name} (副本)",
            createdAt = System.currentTimeMillis()
        )
        
        savePlan(duplicatedPlan)
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }
}