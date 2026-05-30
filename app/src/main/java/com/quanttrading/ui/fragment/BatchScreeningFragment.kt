package com.quanttrading.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.quanttrading.R
import com.quanttrading.data.model.SearchStock
import com.quanttrading.data.model.StockDataDto
import com.quanttrading.data.repository.StockRepository
import com.quanttrading.domain.analysis.QuantAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class BatchScreeningFragment : Fragment() {

    private lateinit var screenButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var resultCard: MaterialCardView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressLinear: androidx.appcompat.widget.AppCompatSeekBar
    
    private val repository = StockRepository()
    private val analyzer = QuantAnalyzer()
    private val decimalFormat = DecimalFormat("0.00")
    private var isScreening = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_batch_screening, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        screenButton = view.findViewById(R.id.screenButton)
        progressBar = view.findViewById(R.id.progressBar)
        progressText = view.findViewById(R.id.progressText)
        resultCard = view.findViewById(R.id.resultCard)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressLinear = view.findViewById(R.id.progressLinear)
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        resultCard.visibility = View.GONE
        
        screenButton.setOnClickListener {
            if (!isScreening) {
                startBatchScreening()
            } else {
                Toast.makeText(requireContext(), "正在筛选中...", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun startBatchScreening() {
        isScreening = true
        screenButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
        progressText.visibility = View.VISIBLE
        progressLinear.visibility = View.VISIBLE
        resultCard.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                val allStocks = performScreening()
                displayResults(allStocks)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "筛选失败：${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isScreening = false
                screenButton.isEnabled = true
                progressBar.visibility = View.GONE
                progressText.visibility = View.GONE
                progressLinear.visibility = View.GONE
            }
        }
    }
    
    private suspend fun performScreening(): List<ScreeningResult> {
        progressText.text = "正在获取市场股票列表..."
        progressLinear.progress = 5
        
        val marketStocks = repository.getMarketStockList()
            .getOrElse { emptyList() }
            .filter { it.price >= 2.0 && it.price <= 30.0 }
        
        progressText.text = "获取到 ${marketStocks.size} 只股票 (2-30 元)，开始分析..."
        progressLinear.progress = 15
        
        val results = mutableListOf<ScreeningResult>()
        val total = marketStocks.size.coerceAtMost(500)
        
        for ((index, stock) in marketStocks.take(total).withIndex()) {
            try {
                val secid = getSecid(stock.code)
                val stockData = repository.getRealTimeStock(secid).getOrNull()
                
                if (stockData != null) {
                    val mockHistorical = generateMockHistoricalData(stockData)
                    val analysisResult = analyzer.analyzeStock(mockHistorical)
                    
                    if (analysisResult.signal in listOf(
                        com.quanttrading.data.model.TradingSignal.STRONG_BUY,
                        com.quanttrading.data.model.TradingSignal.BUY
                    )) {
                        results.add(ScreeningResult(stock, analysisResult))
                    }
                    
                    val progress = 15 + ((index + 1) * 85 / total)
                    progressLinear.progress = progress
                    progressText.text = "已分析 ${index + 1}/$total - 找到 ${results.size} 只推荐股票"
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        progressLinear.progress = 100
        progressText.text = "分析完成！找到 ${results.size} 只推荐股票"
        
        return results.sortedByDescending { it.analysisResult.confidence }.take(20)
    }
    
    private fun getSecid(code: String): String {
        return when {
            code.startsWith("0") || code.startsWith("3") -> "0.$code"
            code.startsWith("6") -> "1.$code"
            else -> "1.$code"
        }
    }
    
    private fun generateMockHistoricalData(current: StockDataDto): List<com.quanttrading.data.model.StockData> {
        val list = mutableListOf<com.quanttrading.data.model.StockData>()
        var price = current.price
        for (i in 0 until 60) {
            price = price * (1 + (Math.random() - 0.5) * 0.02)
            list.add(
                com.quanttrading.data.model.StockData(
                    code = current.code,
                    name = current.name,
                    price = price,
                    high = price * 1.01,
                    low = price * 0.99,
                    open = price * 0.998,
                    close = price,
                    volume = (current.volume * (0.5 + Math.random())).toLong(),
                    turnover = current.turnover * (0.5 + Math.random()),
                    changePercent = (Math.random() - 0.5) * 5,
                    timestamp = System.currentTimeMillis() - i * 86400000
                )
            )
        }
        return list.reversed()
    }
    
    private fun displayResults(results: List<ScreeningResult>) {
        resultCard.visibility = View.VISIBLE
        
        val view = requireView()
        view.findViewById<TextView>(R.id.resultCount)?.text = "共找到 ${results.size} 只推荐股票"
        
        recyclerView.adapter = StockAdapter(results)
    }
    
    data class ScreeningResult(
        val stock: SearchStock,
        val analysisResult: QuantAnalyzer.AnalysisResult
    )
    
    inner class StockAdapter(
        private val results: List<ScreeningResult>
    ) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {
        
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val stockName: TextView = itemView.findViewById(R.id.stockName)
            val stockCode: TextView = itemView.findViewById(R.id.stockCode)
            val price: TextView = itemView.findViewById(R.id.price)
            val signal: TextView = itemView.findViewById(R.id.signal)
            val confidence: TextView = itemView.findViewById(R.id.confidence)
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stock_result, parent, false)
            return ViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val result = results[position]
            holder.stockName.text = result.stock.name
            holder.stockCode.text = result.stock.code
            holder.price.text = "¥${decimalFormat.format(result.stock.price)}"
            
            holder.signal.text = when (result.analysisResult.signal) {
                com.quanttrading.data.model.TradingSignal.STRONG_BUY -> "强烈买入"
                com.quanttrading.data.model.TradingSignal.BUY -> "买入"
                else -> "持有"
            }
            holder.signal.setTextColor(when (result.analysisResult.signal) {
                com.quanttrading.data.model.TradingSignal.STRONG_BUY -> Color.parseColor("#4CAF50")
                com.quanttrading.data.model.TradingSignal.BUY -> Color.parseColor("#8BC34A")
                else -> Color.parseColor("#FF9800")
            })
            
            holder.confidence.text = "置信度：${decimalFormat.format(result.analysisResult.confidence)}%"
        }
        
        override fun getItemCount() = results.size
    }
}
