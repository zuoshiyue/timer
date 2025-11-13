package com.timer.workout.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.timer.workout.R
import com.timer.workout.data.database.WorkoutDatabase
import com.timer.workout.ui.adapters.WorkoutRecordAdapter
import kotlinx.coroutines.launch

/**
 * 训练记录管理界面
 */
class RecordsFragment : Fragment() {

    private lateinit var adapter: WorkoutRecordAdapter
    private lateinit var database: WorkoutDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        database = WorkoutDatabase.getInstance(requireContext())
        setupViews(view)
        setupRecyclerView()
        loadRecords()
    }

    private fun setupViews(view: View) {
        // 设置筛选功能
        setupFilterOptions(view)
    }

    private fun setupFilterOptions(view: View) {
        // 时间筛选
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_filter_today).setOnClickListener {
            filterRecordsByDate("today")
        }
        
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_filter_week).setOnClickListener {
            filterRecordsByDate("week")
        }
        
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_filter_month).setOnClickListener {
            filterRecordsByDate("month")
        }
        
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_filter_all).setOnClickListener {
            filterRecordsByDate("all")
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = requireView().findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_records)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = WorkoutRecordAdapter(
            onRecordClick = { record ->
                showRecordDetails(record)
            },
            onRecordDelete = { record ->
                showDeleteConfirmation(record)
            }
        )
        
        recyclerView.adapter = adapter
    }

    private fun loadRecords() {
        lifecycleScope.launch {
            try {
                val records = database.workoutRecordDao().getAllRecords()
                records.collect { recordList ->
                    adapter.submitList(recordList)
                    updateEmptyState(recordList.isEmpty())
                    updateStatistics(recordList)
                }
            } catch (e: Exception) {
                showError("加载记录失败: ${e.message}")
            }
        }
    }

    private fun filterRecordsByDate(dateRange: String) {
        lifecycleScope.launch {
            try {
                val now = System.currentTimeMillis()
                val startDate = when (dateRange) {
                    "today" -> java.util.Date(now - 24 * 60 * 60 * 1000)
                    "week" -> java.util.Date(now - 7 * 24 * 60 * 60 * 1000)
                    "month" -> java.util.Date(now - 30 * 24 * 60 * 60 * 1000)
                    else -> java.util.Date(0) // 所有记录
                }
                
                val records = database.workoutRecordDao().getRecordsByDateRange(
                    startDate, java.util.Date()
                )
                
                records.collect { recordList ->
                    adapter.submitList(recordList)
                    updateEmptyState(recordList.isEmpty())
                    updateStatistics(recordList)
                }
            } catch (e: Exception) {
                showError("筛选失败: ${e.message}")
            }
        }
    }

    private fun updateStatistics(records: List<com.timer.workout.data.model.WorkoutRecord>) {
        val statsView = requireView().findViewById<View>(R.id.stats_container)
        val totalWorkouts = requireView().findViewById<android.widget.TextView>(R.id.tv_total_workouts)
        val totalDuration = requireView().findViewById<android.widget.TextView>(R.id.tv_total_duration)
        val avgDuration = requireView().findViewById<android.widget.TextView>(R.id.tv_avg_duration)
        val caloriesBurned = requireView().findViewById<android.widget.TextView>(R.id.tv_calories_burned)
        
        if (records.isEmpty()) {
            statsView.visibility = View.GONE
            return
        }
        
        statsView.visibility = View.VISIBLE
        
        val completedRecords = records.filter { it.status == com.timer.workout.data.model.WorkoutStatus.COMPLETED }
        val totalWorkoutsCount = completedRecords.size
        val totalDurationMs = completedRecords.sumOf { it.totalDuration ?: 0L }
        val avgDurationMs = if (totalWorkoutsCount > 0) totalDurationMs / totalWorkoutsCount else 0
        val totalCalories = completedRecords.sumOf { it.caloriesBurned?.toDouble() ?: 0.0 }
        
        totalWorkouts.text = totalWorkoutsCount.toString()
        totalDuration.text = formatDuration(totalDurationMs)
        avgDuration.text = formatDuration(avgDurationMs)
        caloriesBurned.text = "${totalCalories.toInt()} kcal"
    }

    private fun formatDuration(millis: Long): String {
        val hours = millis / (60 * 60 * 1000)
        val minutes = (millis % (60 * 60 * 1000)) / (60 * 1000)
        return if (hours > 0) "${hours}小时${minutes}分钟" else "${minutes}分钟"
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        val emptyState = requireView().findViewById<View>(R.id.empty_state)
        val recyclerView = requireView().findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_records)
        
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showRecordDetails(record: com.timer.workout.data.model.WorkoutRecord) {
        // 显示记录详情对话框
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("训练记录详情")
            .setMessage("""
                计划名称: ${record.planName}
                训练类型: ${record.type}
                开始时间: ${record.startTime}
                结束时间: ${record.endTime}
                训练时长: ${formatDuration(record.totalDuration ?: 0)}
                完成度: ${record.completionRate ?: 0}%
                消耗卡路里: ${record.caloriesBurned ?: 0} kcal
                训练强度: ${record.intensity ?: 3}/5
                备注: ${record.notes ?: "无"}
            """.trimIndent())
            .setPositiveButton("确定", null)
            .show()
    }

    private fun showDeleteConfirmation(record: com.timer.workout.data.model.WorkoutRecord) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("删除记录")
            .setMessage("确定要删除训练记录 "${record.planName}" 吗？此操作不可恢复。")
            .setPositiveButton("删除") { _, _ ->
                deleteRecord(record)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun deleteRecord(record: com.timer.workout.data.model.WorkoutRecord) {
        lifecycleScope.launch {
            try {
                database.workoutRecordDao().deleteRecord(record)
                loadRecords()
                showMessage("记录删除成功")
            } catch (e: Exception) {
                showError("删除失败: ${e.message}")
            }
        }
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }
}