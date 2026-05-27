package com.quanttrading.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.quanttrading.R

class BatchScreeningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_batch_screening, container, false)
        
        view.findViewById<TextView>(R.id.featureName)?.text = "批量筛选"
        
        return view
    }
}
