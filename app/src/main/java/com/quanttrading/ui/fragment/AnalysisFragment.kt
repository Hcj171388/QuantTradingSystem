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
import com.quanttrading.data.repository.StockRepository
import com.quanttrading.domain.analysis.QuantAnalyzer
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class AnalysisFragment : Fragment() {

    private lateinit var stockCodeInput: TextInputEditText
    private lateinit var analyzeButton: MaterialButton
    private lateinit var signalCard: MaterialCardView
    private lateinit var factorsCard: MaterialCardView
    private lateinit var progressBar: CircularProgressIndicator
    
    private val repository = StockRepository()
    private val analyzer = QuantAnalyzer()
    private val decimalFormat = DecimalFormat("0.00")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        stockCodeInput = view.findViewById(R.id.stockCodeInput)
        analyzeButton = view.findViewById(R.id.analyzeButton)
        signalCard = view.findViewById(R.id.signalCard)
        factorsCard = view.findViewById(R.id.factorsCard)
        progressBar = view.findViewById(R.id.progressBar)
        
        analyzeButton.setOnClickListener {
            val input = stockCodeInput.text.toString().trim()
            if (input.isNotEmpty()) {
                analyzeStock(input)
            } else {
                Toast.makeText(requireContext(), "请输入股票代码", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun analyzeStock(keyword: String) {
        progressBar.visibility = View.VISIBLE
        signalCard.visibility = View.GONE
        factorsCard.visibility = View.GONE
        
        lifecycleScope.launch {
            try {
                val stocks = repository.searchStock(keyword)
                if (stocks.isSuccess && stocks.getOrNull()?.isNotEmpty() == true) {
                    val stock = stocks.getOrNull()!!.first()
                    performAnalysis(stock.secid, stock.name)
                } else {
                    Toast.makeText(requireContext(), "未找到股票", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "错误：${e.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun performAnalysis(secid: String, name: String) {
        lifecycleScope.launch {
            try {
                val result = repository.getRealTimeStock(secid)
                if (result.isSuccess) {
                    val stockData = result.getOrNull()!!
                    val mockHistorical = generateMockHistoricalData(stockData)
                    val analysisResult = analyzer.analyzeStock(mockHistorical)
                    displayAnalysisResult(analysisResult, stockData)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "分析失败：${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun generateMockHistoricalData(current: com.quanttrading.data.model.StockDataDto): List<com.quanttrading.data.model.StockData> {
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
    
    private fun displayAnalysisResult(
        result: QuantAnalyzer.AnalysisResult,
        stockData: com.quanttrading.data.model.StockDataDto
    ) {
        signalCard.visibility = View.VISIBLE
        factorsCard.visibility = View.VISIBLE
        
        view?.findViewById<TextView>(R.id.signalText)?.apply {
            text = when (result.signal) {
                com.quanttrading.data.model.TradingSignal.STRONG_BUY -> "强烈买入"
                com.quanttrading.data.model.TradingSignal.BUY -> "买入"
                com.quanttrading.data.model.TradingSignal.HOLD -> "持有"
                com.quanttrading.data.model.TradingSignal.SELL -> "卖出"
                com.quanttrading.data.model.TradingSignal.STRONG_SELL -> "强烈卖出"
            }
            setTextColor(getSignalColor(result.signal))
        }
        
        view?.findViewById<TextView>(R.id.upProbability)?.text = "${decimalFormat.format(result.upProbability)}%"
        view?.findViewById<TextView>(R.id.downProbability)?.text = "${decimalFormat.format(result.downProbability)}%"
        view?.findViewById<TextView>(R.id.confidence)?.text = "${decimalFormat.format(result.confidence)}%"
        
        displayFactors(result.factors)
    }
    
    private fun displayFactors(factors: Map<String, Double>) {
        view?.findViewById<TextView>(R.id.momentumFactor)?.text = decimalFormat.format(factors["momentum"] ?: 0.0)
        view?.findViewById<TextView>(R.id.volatilityFactor)?.text = decimalFormat.format(factors["volatility"] ?: 0.0)
        view?.findViewById<TextView>(R.id.trendFactor)?.text = decimalFormat.format(factors["trend"] ?: 0.0)
        view?.findViewById<TextView>(R.id.rsiFactor)?.text = decimalFormat.format((factors["rsi"] ?: 0.0) * 100)
        view?.findViewById<TextView>(R.id.macdFactor)?.text = decimalFormat.format(factors["macd"] ?: 0.0)
        view?.findViewById<TextView>(R.id.kdjFactor)?.text = decimalFormat.format((factors["kdj"] ?: 0.0) * 100)
        view?.findViewById<TextView>(R.id.volumeRatio)?.text = decimalFormat.format(factors["volumeRatio"] ?: 0.0)
    }
    
    private fun getSignalColor(signal: com.quanttrading.data.model.TradingSignal): Int {
        return when (signal) {
            com.quanttrading.data.model.TradingSignal.STRONG_BUY, 
            com.quanttrading.data.model.TradingSignal.BUY -> Color.parseColor("#4CAF50")
            com.quanttrading.data.model.TradingSignal.STRONG_SELL, 
            com.quanttrading.data.model.TradingSignal.SELL -> Color.parseColor("#F44336")
            else -> Color.parseColor("#FF9800")
        }
    }
}
