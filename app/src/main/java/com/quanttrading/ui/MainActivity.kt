package com.quanttrading.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.quanttrading.databinding.ActivityMainBinding
import com.quanttrading.ui.fragment.AnalysisFragment
import com.quanttrading.ui.fragment.BatchScreeningFragment
import com.quanttrading.ui.fragment.StockSearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int) = when (position) {
                0 -> StockSearchFragment()
                1 -> AnalysisFragment()
                2 -> BatchScreeningFragment()
                else -> throw IllegalStateException("Invalid position")
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "股票查询"
                1 -> "量化分析"
                2 -> "批量筛选"
                else -> "Unknown"
            }
        }.attach()
    }
}
