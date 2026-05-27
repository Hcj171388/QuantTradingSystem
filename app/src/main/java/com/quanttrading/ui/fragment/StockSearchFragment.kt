package com.quanttrading.ui.fragment

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
import com.quanttrading.data.repository.StockRepository
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class StockSearchFragment : Fragment() {

    private lateinit var stockCodeInput: TextInputEditText
    private lateinit var searchButton: MaterialButton
    private lateinit var stockInfoCard: MaterialCardView
    private lateinit var progressBar: CircularProgressIndicator
    
    private val repository = StockRepository()
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
                val stocks = repository.searchStock(keyword)
                if (stocks.isSuccess && stocks.getOrNull()?.isNotEmpty() == true) {
                    val stock = stocks.getOrNull()!!.first()
                    loadStockDetail(stock.secid, stock.name)
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
    
    private fun loadStockDetail(secid: String, name: String) {
        lifecycleScope.launch {
            try {
                val result = repository.getRealTimeStock(secid)
                if (result.isSuccess) {
                    val data = result.getOrNull()!!
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
    
    private fun displayStockInfo(data: com.quanttrading.data.model.StockDataDto) {
        stockInfoCard.visibility = View.VISIBLE
        
        val color = if (data.changePercent >= 0) 
            requireContext().getColor(R.color.red) 
        else 
            requireContext().getColor(R.color.green)
        
        view?.findViewById<TextView>(R.id.stockName)?.apply {
            text = "${data.name} (${data.code})"
            setTextColor(color)
        }
        
        view?.findViewById<TextView>(R.id.currentPrice)?.text = decimalFormat.format(data.price)
        view?.findViewById<TextView>(R.id.changePercent)?.apply {
            text = "${if (data.changePercent >= 0) "+" else ""}${decimalFormat.format(data.changePercent)}%"
            setTextColor(color)
        }
        view?.findViewById<TextView>(R.id.highPrice)?.text = decimalFormat.format(data.high)
        view?.findViewById<TextView>(R.id.lowPrice)?.text = decimalFormat.format(data.low)
        view?.findViewById<TextView>(R.id.volume)?.text = formatVolume(data.volume)
    }
    
    private fun formatVolume(volume: Double): String {
        return when {
            volume > 100000000 -> String.format("%.2f亿手", volume / 100000000)
            volume > 10000 -> String.format("%.2f万手", volume / 10000)
            else -> "$volume 手"
        }
    }
}
