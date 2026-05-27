package com.quanttrading.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.quanttrading.ui.fragment.AnalysisFragment
import com.quanttrading.ui.fragment.BatchScreeningFragment
import com.quanttrading.ui.fragment.StockSearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        setupViewPager()
    }

    private fun setupViewPager() {
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int) = when (position) {
                0 -> StockSearchFragment()
                1 -> AnalysisFragment()
                2 -> BatchScreeningFragment()
                else -> throw IllegalStateException("Invalid position")
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "股票查询"
                1 -> "量化分析"
                2 -> "批量筛选"
                else -> "Unknown"
            }
        }.attach()
    }
}
