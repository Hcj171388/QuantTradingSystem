package com.quanttrading.domain.analysis

import com.quanttrading.data.model.StockData
import com.quanttrading.data.model.StockFactor
import com.quanttrading.data.model.TradingSignal
import java.util.*

class QuantAnalyzer {

    data class AnalysisResult(
        val signal: TradingSignal,
        val upProbability: Double,
        val downProbability: Double,
        val confidence: Double,
        val factors: Map<String, Double>
    )

    fun analyzeStock(historicalData: List<StockData>): AnalysisResult {
        if (historicalData.size < 20) {
            throw IllegalArgumentException("数据不足，至少需要20个交易日数据")
        }

        val factors = calculateAllFactors(historicalData)
        val signal = generateSignal(factors)
        val (upProb, downProb) = calculateProbability(factors)
        val confidence = calculateConfidence(factors, signal)

        return AnalysisResult(
            signal = signal,
            upProbability = upProb,
            downProbability = downProb,
            confidence = confidence,
            factors = factors
        )
    }

    private fun calculateAllFactors(data: List<StockData>): Map<String, Double> {
        val factors = mutableMapOf<String, Double>()

        factors["momentum"] = calculateMomentum(data)
        factors["volatility"] = calculateVolatility(data)
        factors["liquidity"] = calculateLiquidity(data)
        factors["trend"] = calculateTrend(data)
        factors["volumeRatio"] = calculateVolumeRatio(data)
        factors["pricePosition"] = calculatePricePosition(data)
        factors["maSignal"] = calculateMASignal(data)
        factors["rsi"] = calculateRSI(data)
        factors["macd"] = calculateMACD(data)
        factors["kdj"] = calculateKDJ(data)
        factors["bollinger"] = calculateBollingerPosition(data)
        factors["priceAction"] = calculatePriceAction(data)
        factors["turnoverRate"] = calculateTurnoverRate(data)
        factors["marketSentiment"] = calculateMarketSentiment(data)
        factors["relativeStrength"] = calculateRelativeStrength(data)
        factors["supportResistance"] = calculateSupportResistance(data)

        return factors
    }

    private fun calculateMomentum(data: List<StockData>): Double {
        if (data.size < 10) return 0.5

        val recent = data.takeLast(5)
        val momentum = (recent.last().price - recent.first().price) / recent.first().price

        return normalizeValue(momentum, -0.1, 0.1)
    }

    private fun calculateVolatility(data: List<StockData>): Double {
        if (data.size < 20) return 0.5

        val returns = mutableListOf<Double>()
        for (i in 1 until data.size) {
            returns.add((data[i].price - data[i-1].price) / data[i-1].price)
        }

        val mean = returns.average()
        val variance = returns.map { (it - mean) * (it - mean) }.average()
        val stdDev = Math.sqrt(variance)

        return normalizeValue(stdDev, 0.01, 0.05)
    }

    private fun calculateLiquidity(data: List<StockData>): Double {
        if (data.isEmpty()) return 0.5

        val avgVolume = data.takeLast(20).map { it.volume }.average()
        val recentVolume = data.last().volume

        val ratio = recentVolume / avgVolume
        return normalizeValue(ratio, 0.5, 2.0)
    }

    private fun calculateTrend(data: List<StockData>): Double {
        if (data.size < 20) return 0.5

        val shortMA = data.takeLast(5).map { it.price }.average()
        val longMA = data.takeLast(20).map { it.price }.average()

        val trend = (shortMA - longMA) / longMA
        return normalizeValue(trend, -0.05, 0.05)
    }

    private fun calculateVolumeRatio(data: List<StockData>): Double {
        if (data.size < 5) return 0.5

        val recentVolume = data.last().volume
        val avgVolume = data.takeLast(5).map { it.volume }.average()

        val ratio = recentVolume / avgVolume
        return normalizeValue(ratio, 0.5, 3.0)
    }

    private fun calculatePricePosition(data: List<StockData>): Double {
        if (data.size < 60) return 0.5

        val prices = data.takeLast(60).map { it.price }
        val maxPrice = prices.maxOrNull() ?: data.last().price
        val minPrice = prices.minOrNull() ?: data.last().price
        val currentPrice = data.last().price

        val position = (currentPrice - minPrice) / (maxPrice - minPrice)
        return position.coerceIn(0.0, 1.0)
    }

    private fun calculateMASignal(data: List<StockData>): Double {
        if (data.size < 20) return 0.5

        val prices = data.map { it.price }
        val ma5 = prices.takeLast(5).average()
        val ma10 = prices.takeLast(10).average()
        val ma20 = prices.takeLast(20).average()
        val current = prices.last()

        var score = 0.0
        if (current > ma5) score += 0.3
        if (ma5 > ma10) score += 0.3
        if (ma10 > ma20) score += 0.4

        return score
    }

    private fun calculateRSI(data: List<StockData>, period: Int = 14): Double {
        if (data.size < period + 1) return 50.0

        val gains = mutableListOf<Double>()
        val losses = mutableListOf<Double>()

        for (i in data.size - period until data.size) {
            val change = data[i].price - data[i-1].price
            if (change > 0) {
                gains.add(change)
                losses.add(0.0)
            } else {
                gains.add(0.0)
                losses.add(-change)
            }
        }

        val avgGain = gains.average()
        val avgLoss = losses.average()

        if (avgLoss == 0.0) return 100.0

        val rs = avgGain / avgLoss
        val rsi = 100 - (100 / (1 + rs))

        return (rsi / 100).coerceIn(0.0, 1.0)
    }

    private fun calculateMACD(data: List<StockData>): Double {
        if (data.size < 26) return 0.5

        val prices = data.map { it.price }
        val ema12 = calculateEMA(prices, 12)
        val ema26 = calculateEMA(prices, 26)
        val macd = ema12 - ema26

        return normalizeValue(macd / prices.last(), -0.02, 0.02)
    }

    private fun calculateEMA(prices: List<Double>, period: Int): Double {
        if (prices.size < period) return prices.last()

        val multiplier = 2.0 / (period + 1)
        var ema = prices.take(period).average()

        for (i in period until prices.size) {
            ema = (prices[i] - ema) * multiplier + ema
        }

        return ema
    }

    private fun calculateKDJ(data: List<StockData>): Double {
        if (data.size < 9) return 0.5

        val recent = data.takeLast(9)
        val high = recent.map { it.high }.maxOrNull() ?: recent.last().high
        val low = recent.map { it.low }.minOrNull() ?: recent.last().low
        val close = recent.last().close

        val rsv = ((close - low) / (high - low)) * 100
        val k = (2.0 / 3) * 50 + (1.0 / 3) * rsv

        return (k / 100).coerceIn(0.0, 1.0)
    }

    private fun calculateBollingerPosition(data: List<StockData>): Double {
        if (data.size < 20) return 0.5

        val prices = data.takeLast(20).map { it.price }
        val mean = prices.average()
        val stdDev = Math.sqrt(prices.map { (it - mean) * (it - mean) }.average())

        val upper = mean + 2 * stdDev
        val lower = mean - 2 * stdDev
        val current = data.last().price

        val position = (current - lower) / (upper - lower)
        return position.coerceIn(0.0, 1.0)
    }

    private fun calculatePriceAction(data: List<StockData>): Double {
        if (data.size < 5) return 0.5

        val recent = data.takeLast(5)
        var positiveCandles = 0

        for (i in 1 until recent.size) {
            if (recent[i].close > recent[i].open) {
                positiveCandles++
            }
        }

        return positiveCandles.toDouble() / (recent.size - 1)
    }

    private fun calculateTurnoverRate(data: List<StockData>): Double {
        if (data.isEmpty()) return 0.5

        val recentTurnover = data.takeLast(5).map { it.turnover }.average()
        val historicalTurnover = data.takeLast(20).map { it.turnover }.average()

        val ratio = recentTurnover / historicalTurnover
        return normalizeValue(ratio, 0.5, 2.0)
    }

    private fun calculateMarketSentiment(data: List<StockData>): Double {
        if (data.size < 10) return 0.5

        val recent = data.takeLast(10)
        var upDays = 0

        for (i in 1 until recent.size) {
            if (recent[i].close > recent[i-1].close) {
                upDays++
            }
        }

        return upDays.toDouble() / (recent.size - 1)
    }

    private fun calculateRelativeStrength(data: List<StockData>): Double {
        if (data.size < 20) return 0.5

        val stockReturn = (data.last().price - data[data.size - 20].price) / data[data.size - 20].price

        return normalizeValue(stockReturn, -0.1, 0.1)
    }

    private fun calculateSupportResistance(data: List<StockData>): Double {
        if (data.size < 20) return 0.5

        val prices = data.takeLast(20).map { it.price }
        val highs = data.takeLast(20).map { it.high }
        val lows = data.takeLast(20).map { it.low }

        val resistanceLevel = highs.average()
        val supportLevel = lows.average()
        val current = data.last().price

        val position = (current - supportLevel) / (resistanceLevel - supportLevel)
        return position.coerceIn(0.0, 1.0)
    }

    private fun generateSignal(factors: Map<String, Double>): TradingSignal {
        var bullishScore = 0.0
        var bearishScore = 0.0

        when {
            factors["momentum"]!! > 0.6 -> bullishScore += 2
            factors["momentum"]!! < 0.4 -> bearishScore += 2
        }

        when {
            factors["trend"]!! > 0.6 -> bullishScore += 2
            factors["trend"]!! < 0.4 -> bearishScore += 2
        }

        when {
            factors["maSignal"]!! > 0.6 -> bullishScore += 2
            factors["maSignal"]!! < 0.4 -> bearishScore += 2
        }

        when {
            factors["volumeRatio"]!! > 1.2 -> bullishScore += 1
            factors["volumeRatio"]!! < 0.8 -> bearishScore += 1
        }

        when {
            factors["rsi"]!! > 0.7 -> bearishScore += 1
            factors["rsi"]!! < 0.3 -> bullishScore += 1
        }

        when {
            factors["priceAction"]!! > 0.6 -> bullishScore += 1
            factors["priceAction"]!! < 0.4 -> bearishScore += 1
        }

        when {
            factors["marketSentiment"]!! > 0.6 -> bullishScore += 1
            factors["marketSentiment"]!! < 0.4 -> bearishScore += 1
        }

        return when {
            bullishScore >= 7 -> TradingSignal.STRONG_BUY
            bullishScore >= 4 -> TradingSignal.BUY
            bearishScore >= 7 -> TradingSignal.STRONG_SELL
            bearishScore >= 4 -> TradingSignal.SELL
            else -> TradingSignal.HOLD
        }
    }

    private fun calculateProbability(factors: Map<String, Double>): Pair<Double, Double> {
        val bullishFactors = factors.filter { (key, value) ->
            (key in listOf("momentum", "trend", "maSignal", "priceAction", "marketSentiment") && value > 0.5) ||
            (key == "volumeRatio" && value > 1.0)
        }

        val bearishFactors = factors.filter { (key, value) ->
            (key in listOf("momentum", "trend", "maSignal", "priceAction", "marketSentiment") && value < 0.5) ||
            (key == "volumeRatio" && value < 1.0)
        }

        val totalFactors = factors.size
        val bullishCount = bullishFactors.size
        val bearishCount = bearishFactors.size

        val upProbability = (bullishCount.toDouble() / totalFactors) * 100
        val downProbability = (bearishCount.toDouble() / totalFactors) * 100

        return Pair(upProbability, downProbability)
    }

    private fun calculateConfidence(factors: Map<String, Double>, signal: TradingSignal): Double {
        val volatility = factors["volatility"]!!

        val signalStrength = when (signal) {
            TradingSignal.STRONG_BUY, TradingSignal.STRONG_SELL -> 0.9
            TradingSignal.BUY, TradingSignal.SELL -> 0.7
            TradingSignal.HOLD -> 0.5
        }

        val volatilityPenalty = volatility * 0.3
        val confidence = signalStrength - volatilityPenalty

        return (confidence * 100).coerceIn(0.0, 100.0)
    }

    private fun normalizeValue(value: Double, min: Double, max: Double): Double {
        val normalized = (value - min) / (max - min)
        return normalized.coerceIn(0.0, 1.0)
    }
}