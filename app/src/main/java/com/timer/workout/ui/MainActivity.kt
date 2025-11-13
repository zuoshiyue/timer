package com.timer.workout.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.timer.workout.R
import com.timer.workout.ui.fragments.HistoryFragment
import com.timer.workout.ui.fragments.PlansFragment
import com.timer.workout.ui.fragments.TimerFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupBottomNavigation()
        
        // 默认显示计时器界面
        if (savedInstanceState == null) {
            loadFragment(TimerFragment())
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_nav)
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_plans -> {
                    loadFragment(PlansFragment())
                    true
                }
                R.id.nav_presets -> {
                    loadFragment(TimerFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}