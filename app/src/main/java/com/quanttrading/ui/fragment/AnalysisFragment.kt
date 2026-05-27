package com.quanttrading.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.quanttrading.R

class AnalysisFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val stockCodeInput = view.findViewById<TextInputEditText>(R.id.stockCodeInput)
        val analyzeButton = view.findViewById<Button>(R.id.analyzeButton)
        val signalCard = view.findViewById<MaterialCardView>(R.id.signalCard)
        val factorsCard = view.findViewById<MaterialCardView>(R.id.factorsCard)
        val progressBar = view.findViewById<CircularProgressIndicator>(R.id.analysisProgressBar)
        
        progressBar.visibility = View.GONE
        signalCard.visibility = View.GONE
        factorsCard.visibility = View.GONE
        
        analyzeButton.setOnClickListener {
            val input = stockCodeInput.text.toString().trim()
            if (input.isNotEmpty()) {
                Toast.makeText(requireContext(), "分析：$input", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "请输入股票代码", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
