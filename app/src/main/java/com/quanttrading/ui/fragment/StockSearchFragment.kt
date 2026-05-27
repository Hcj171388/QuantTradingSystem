package com.quanttrading.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.quanttrading.R
import com.quanttrading.data.api.ApiClient
import com.quanttrading.data.model.SearchStock
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class StockSearchFragment : Fragment() {

    private lateinit var stockCodeInput: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var stockInfoCard: MaterialCardView
    private lateinit var progressBar: CircularProgressIndicator
    
    private val decimalFormat = DecimalFormat("0.00")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        stockCodeInput = view.findViewById(R.id.stockCodeInput)
        searchButton = view.findViewById(R.id.searchButton)
        stockInfoCard = view.findViewById(R.id.stockInfoCard)
        progressBar = view.findViewById(R.id.progressBar)
        
        searchButton.setOnClickListener {
            val input = stockCodeInput.text.toString().trim()
            if (input.isNotEmpty()) {
                searchStock(input)
            } else {
                Toast.makeText(requireContext(), "请输入股票代码或名称", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun searchStock(keyword: String) {
        progressBar.visibility = View.VISIBLE
        stockInfoCard.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                val response = ApiClient.stockApiService.searchStock(keyword)
                val stocks = response.data?.stockList ?: emptyList()
                
                if (stocks.isNotEmpty()) {
                    val stock = stocks.first()
                    loadStockDetail(stock)
                } else {
                    Toast.makeText(requireContext(), "未找到相关股票", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "搜索失败：${e.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun loadStockDetail(stock: SearchStock) {
        lifecycleScope.launch {
            try {
                val secid = getSecid(stock.code)
                val response = ApiClient.stockApiService.getRealTimeStock(secid)
                val data = response.data
                
                if (data != null) {
                    displayStockInfo(data)
                } else {
                    Toast.makeText(requireContext(), "获取数据失败", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "错误：${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun getSecid(code: String): String {
        return when {
            code.startsWith("0") || code.startsWith("3") -> "0.$code"
            code.startsWith("6") -> "1.$code"
            else -> "1.$code"
        }
    }
    
    private fun displayStockInfo(data: com.quanttrading.data.model.StockDataDto) {
        stockInfoCard.visibility = View.VISIBLE
        
        val view = requireView()
        val color = if (data.changePercent >= 0) 
            Color.parseColor("#D32F2F") 
        else 
            Color.parseColor("#388E3C")
        
        view.findViewById<TextView>(R.id.stockName)?.text = "${data.name} (${data.code})"
        view.findViewById<TextView>(R.id.currentPrice)?.text = decimalFormat.format(data.price)
        
        view.findViewById<TextView>(R.id.changePercent)?.apply {
            text = "${if (data.changePercent >= 0) "+" else ""}${decimalFormat.format(data.changePercent)}%"
            setTextColor(color)
        }
        
        view.findViewById<TextView>(R.id.highPrice)?.text = decimalFormat.format(data.high)
        view.findViewById<TextView>(R.id.lowPrice)?.text = decimalFormat.format(data.low)
        view.findViewById<TextView>(R.id.volume)?.text = formatVolume(data.volume)
    }
    
    private fun formatVolume(volume: Double): String {
        return when {
            volume > 100000000 -> String.format("%.2f 亿手", volume / 100000000)
            volume > 10000 -> String.format("%.2f 万手", volume / 10000)
            else -> "%.0f 手".format(volume)
        }
    }
}
