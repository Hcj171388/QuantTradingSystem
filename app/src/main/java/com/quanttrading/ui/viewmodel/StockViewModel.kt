package com.quanttrading.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quanttrading.data.model.PredictionResult
import com.quanttrading.data.model.StockData
import com.quanttrading.data.repository.StockRepository
import com.quanttrading.domain.analysis.QuantAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StockViewModel(
    private val repository: StockRepository
) : ViewModel() {

    private val _stockData = MutableStateFlow<StockData?>(null)
    val stockData: StateFlow<StockData?> = _stockData.asStateFlow()

    private val _historicalData = MutableStateFlow<List<StockData>>(emptyList())
    val historicalData: StateFlow<List<StockData>> = _historicalData.asStateFlow()

    private val _analysisResult = MutableStateFlow<QuantAnalyzer.AnalysisResult?>(null)
    val analysisResult: StateFlow<QuantAnalyzer.AnalysisResult?> = _analysisResult.asStateFlow()

    private val _stockList = MutableStateFlow<List<StockData>>(emptyList())
    val stockList: StateFlow<List<StockData>> = _stockList.asStateFlow()

    private val _searchResults = MutableStateFlow<List<StockData>>(emptyList())
    val searchResults: StateFlow<List<StockData>> = _searchResults.asStateFlow()

    private val _batchAnalysisResults = MutableStateFlow<List<PredictionResult>>(emptyList())
    val batchAnalysisResults: StateFlow<List<PredictionResult>> = _batchAnalysisResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadRealTimeStock(code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getRealTimeStock(code)
                .onSuccess { data ->
                    _stockData.value = data
                }
                .onFailure { e ->
                    _error.value = "加载股票数据失败: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    fun loadHistoricalData(code: String, count: Int = 100) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getHistoricalData(code, count)
                .onSuccess { data ->
                    _historicalData.value = data
                }
                .onFailure { e ->
                    _error.value = "加载历史数据失败: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    fun analyzeStock(code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.analyzeStock(code)
                .onSuccess { result ->
                    _analysisResult.value = result
                }
                .onFailure { e ->
                    _error.value = "分析股票失败: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    fun loadStockList(page: Int = 1, size: Int = 100) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getStockList(page, size)
                .onSuccess { list ->
                    _stockList.value = list
                }
                .onFailure { e ->
                    _error.value = "加载股票列表失败: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    fun searchStock(keyword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.searchStock(keyword)
                .onSuccess { results ->
                    _searchResults.value = results
                }
                .onFailure { e ->
                    _error.value = "搜索失败: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    fun batchAnalyzeStocks(codes: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.batchAnalyzeStocks(codes)
                .onSuccess { results ->
                    _batchAnalysisResults.value = results
                }
                .onFailure { e ->
                    _error.value = "批量分析失败: ${e.message}"
                }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}