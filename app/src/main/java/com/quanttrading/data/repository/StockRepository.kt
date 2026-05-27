package com.quanttrading.data.repository

import com.quanttrading.data.api.StockApiService
import com.quanttrading.data.model.StockData
import com.quanttrading.domain.analysis.QuantAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockRepository(
    private val apiService: StockApiService,
    private val analyzer: QuantAnalyzer
) {

    suspend fun getRealTimeStock(code: String): Result<StockData> {
        return withContext(Dispatchers.IO) {
            try {
                val stockData = apiService.getRealTimeStock(code)
                Result.success(stockData)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getHistoricalData(code: String, count: Int = 100): Result<List<StockData>> {
        return withContext(Dispatchers.IO) {
            try {
                val historicalData = apiService.getHistoricalData(code, "daily", count)
                Result.success(historicalData)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun analyzeStock(code: String): Result<QuantAnalyzer.AnalysisResult> {
        return withContext(Dispatchers.IO) {
            try {
                val historicalData = apiService.getHistoricalData(code, "daily", 100)
                val result = analyzer.analyzeStock(historicalData)
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getStockList(page: Int = 1, size: Int = 100): Result<List<StockData>> {
        return withContext(Dispatchers.IO) {
            try {
                val stockList = apiService.getStockList(page, size)
                Result.success(stockList)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun searchStock(keyword: String): Result<List<StockData>> {
        return withContext(Dispatchers.IO) {
            try {
                val searchResult = apiService.searchStock(keyword)
                Result.success(searchResult)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun batchAnalyzeStocks(codes: List<String>): Result<List<com.quanttrading.data.model.PredictionResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val results = mutableListOf<com.quanttrading.data.model.PredictionResult>()
                
                for (code in codes) {
                    try {
                        val historicalData = apiService.getHistoricalData(code, "daily", 100)
                        val analysisResult = analyzer.analyzeStock(historicalData)
                        
                        val stockData = apiService.getRealTimeStock(code)
                        
                        val predictionResult = com.quanttrading.data.model.PredictionResult(
                            code = code,
                            name = stockData.name,
                            upProbability = analysisResult.upProbability,
                            downProbability = analysisResult.downProbability,
                            confidence = analysisResult.confidence,
                            factors = com.quanttrading.data.model.StockFactor(
                                momentum = analysisResult.factors["momentum"] ?: 0.0,
                                volatility = analysisResult.factors["volatility"] ?: 0.0,
                                liquidity = analysisResult.factors["liquidity"] ?: 0.0,
                                trend = analysisResult.factors["trend"] ?: 0.0,
                                volumeRatio = analysisResult.factors["volumeRatio"] ?: 0.0,
                                pricePosition = analysisResult.factors["pricePosition"] ?: 0.0,
                                movingAverage = analysisResult.factors["maSignal"] ?: 0.0,
                                rsi = analysisResult.factors["rsi"] ?: 0.0,
                                macd = analysisResult.factors["macd"] ?: 0.0,
                                kdj = analysisResult.factors["kdj"] ?: 0.0
                            ),
                            signal = analysisResult.signal
                        )
                        
                        results.add(predictionResult)
                    } catch (e: Exception) {
                        continue
                    }
                }
                
                Result.success(results)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}