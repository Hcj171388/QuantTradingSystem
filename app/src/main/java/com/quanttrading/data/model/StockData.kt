package com.quanttrading.data.model

data class StockData(
    val code: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val volume: Long,
    val turnover: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val close: Double,
    val timestamp: Long
)

data class StockFactor(
    val momentum: Double,
    val volatility: Double,
    val liquidity: Double,
    val trend: Double,
    val volumeRatio: Double,
    val pricePosition: Double,
    val movingAverage: Double,
    val rsi: Double,
    val macd: Double,
    val kdj: Double
)

data class PredictionResult(
    val code: String,
    val name: String,
    val upProbability: Double,
    val downProbability: Double,
    val confidence: Double,
    val factors: StockFactor,
    val signal: TradingSignal
)

enum class TradingSignal {
    STRONG_BUY,
    BUY,
    HOLD,
    SELL,
    STRONG_SELL
}