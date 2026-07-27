package com.quanttrading.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.quanttrading.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        val clazz = Class.forName("com.quanttrading.ui.fragment.StockSearchFragment")
                        clazz.newInstance() as Fragment
                    }
                    1 -> {
                        val clazz = Class.forName("com.quanttrading.ui.fragment.AnalysisFragment")
                        clazz.newInstance() as Fragment
                    }
                    2 -> {
                        val clazz = Class.forName("com.quanttrading.ui.fragment.BatchScreeningFragment")
                        clazz.newInstance() as Fragment
                    }
                    3 -> {
                        val clazz = Class.forName("com.quanttrading.ui.fragment.IndustryRankingFragment")
                        clazz.newInstance() as Fragment
                    }
                    else -> {
                        val clazz = Class.forName("com.quanttrading.ui.fragment.StockSearchFragment")
                        clazz.newInstance() as Fragment
                    }
                }
            }
        }
    }
}
