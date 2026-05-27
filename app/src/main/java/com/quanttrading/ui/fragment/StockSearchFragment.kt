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

class StockSearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val stockCodeInput = view.findViewById<TextInputEditText>(R.id.stockCodeInput)
        val searchButton = view.findViewById<Button>(R.id.searchButton)
        val stockInfoCard = view.findViewById<MaterialCardView>(R.id.stockInfoCard)
        val progressBar = view.findViewById<CircularProgressIndicator>(R.id.progressBar)
        
        progressBar.visibility = View.GONE
        stockInfoCard.visibility = View.GONE
        
        searchButton.setOnClickListener {
            val input = stockCodeInput.text.toString().trim()
            if (input.isNotEmpty()) {
                Toast.makeText(requireContext(), "搜索：$input", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "请输入股票代码或名称", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
