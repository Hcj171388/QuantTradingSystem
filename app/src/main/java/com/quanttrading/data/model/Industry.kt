package com.quanttrading.data.model

import com.google.gson.annotations.SerializedName

/**
 * Eastmoney 行业板块列表响应
 */
data class IndustryListResponse(
    @SerializedName("data") val data: IndustryListData?
)

data class IndustryListData(
    @SerializedName("total") val total: Int = 0,
    @SerializedName("diff") val diff: List<IndustryDto>?
)

/**
 * 行业板块 DTO
 * f12: 板块代码 (如 BK0473)
 * f14: 板块名称
 * f3:  涨跌幅 (%)
 * f104: 上涨家数
 * f105: 下跌家数
 * f106: 平盘家数
 * f128: 领涨股名称
 * f136: 领涨股涨跌幅
 * f140: 总市值
 * f141: 流通市值
 */
data class IndustryDto(
    @SerializedName("f12") val code: String,
    @SerializedName("f14") val name: String,
    @SerializedName("f3") val changePercent: Double = 0.0,
    @SerializedName("f104") val upCount: Int = 0,
    @SerializedName("f105") val downCount: Int = 0,
    @SerializedName("f106") val flatCount: Int = 0,
    @SerializedName("f128") val leadingStock: String? = null,
    @SerializedName("f136") val leadingChange: Double = 0.0,
    @SerializedName("f140") val totalMarketCap: Double = 0.0,
    @SerializedName("f141") val floatMarketCap: Double = 0.0
)

/**
 * 行业板块成分股响应（与行业板块列表共用 Eastmoney clist 结构，但 diff 内字段不同）
 */
data class IndustryStockListResponse(
    @SerializedName("data") val data: IndustryStockListData?
)

data class IndustryStockListData(
    @SerializedName("total") val total: Int = 0,
    @SerializedName("diff") val diff: List<IndustryStockDto>?
)

/**
 * 行业成分股扩展字段（包含同比营收、同比净利润、市值等）
 * f2:  最新价
 * f3:  涨跌幅 (%)
 * f12: 代码
 * f14: 名称
 * f20: 总市值
 * f21: 流通市值
 * f6:  成交额
 * f8:  换手率
 * f9:  市盈率(动态)
 * f115:PE-TTM
 * f173:ROE
 * f184:主力净流入额
 * f185:主力净流入占比
 * f186:营业收入同比 (%)（best-effort）
 * f187:净利润同比 (%)（best-effort）
 */
data class IndustryStockDto(
    @SerializedName("f12") val code: String,
    @SerializedName("f14") val name: String,
    @SerializedName("f2") val price: Double = 0.0,
    @SerializedName("f3") val changePercent: Double = 0.0,
    @SerializedName("f6") val turnover: Double = 0.0,
    @SerializedName("f8") val turnoverRate: Double = 0.0,
    @SerializedName("f9") val peDynamic: Double = 0.0,
    @SerializedName("f20") val totalMarketCap: Double = 0.0,
    @SerializedName("f21") val floatMarketCap: Double = 0.0,
    @SerializedName("f115") val peTtm: Double = 0.0,
    @SerializedName("f173") val roe: Double = 0.0,
    @SerializedName("f184") val mainNetInflow: Double = 0.0,
    @SerializedName("f185") val mainNetInflowRatio: Double = 0.0,
    @SerializedName("f186") val revenueYoy: Double = 0.0,
    @SerializedName("f187") val profitYoy: Double = 0.0
)

/**
 * 行业排名结果（含原始统计字段与最终计算出的"上涨比例"）
 *
 * 上涨比例计算公式（与用户需求一致）：
 *   ratio = (upCount / stockCount) / totalAShareCount * 10000  （单位：万分之，记作 %）
 * 例如：新能源发电 35 只成份股，上涨 31 只，A股总数 5523，
 *      ratio = (31/35) / 5523 * 10000 = 1.60%
 */
data class IndustryRank(
    val code: String,
    val name: String,
    val stockCount: Int,          // 行业成份股数量（= upCount + downCount + flatCount，这里用接口返回的家数和近似）
    val upCount: Int,            // 上涨成份股数量
    val downCount: Int,          // 下跌成份股数量
    val totalAShareCount: Int,   // A股总数量
    val ratio: Double            // 上涨比例 (%)
)

/**
 * 单只股票展示数据（行业成份股，按股价由低到高排序后取前 N）
 */
data class IndustryStock(
    val code: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val revenueYoy: Double,      // 同比营收增长率 (%)
    val profitYoy: Double,       // 同比净利润增长率 (%)
    val totalMarketCap: Double,  // 股票总市值
    val holdingRatio: Double     // 持仓占比 = 股票市值 / 板块总市值 * 100%
)
