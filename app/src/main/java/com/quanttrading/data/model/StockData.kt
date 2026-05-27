package com.quanttrading.data.model

import com.google.gson.annotations.SerializedName

data class EastMoneyResponse(
    @SerializedName("data") val data: StockDataDto?
)

data class StockDataDto(
    @SerializedName("f57") val code: String,
    @SerializedName("f58") val name: String,
    @SerializedName("f2") val price: Double,
    @SerializedName("f3") val changePercent: Double,
    @SerializedName("f4") val change: Double,
    @SerializedName("f5") val volume: Double,
    @SerializedName("f6") val turnover: Double,
    @SerializedName("f43") val open: Double,
    @SerializedName("f44") val close: Double,
    @SerializedName("f45") val high: Double,
    @SerializedName("f46") val low: Double,
    @SerializedName("f84") val totalShares: Double,
    @SerializedName("f85") val floatShares: Double,
    @SerializedName("f107") val marketCap: Double
)

data class SearchResponse(
    @SerializedName("data") val data: SearchData?
)

data class SearchData(
    @SerializedName("quotation_code_list") val stockList: List<SearchStock>?
)

data class SearchStock(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("code") val code: String,
    @SerializedName("market_type") val marketType: String
)

data class StockInfo(
    val code: String,
    val name: String,
    val secid: String
)

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

enum class TradingSignal {
    STRONG_BUY,
    BUY,
    HOLD,
    SELL,
    STRONG_SELL
}
