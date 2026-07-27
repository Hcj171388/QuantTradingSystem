package com.quanttrading.data.repository

import com.quanttrading.data.api.ApiClient
import com.quanttrading.data.model.IndustryRank
import com.quanttrading.data.model.IndustryStock
import com.quanttrading.data.model.IndustryStockDto
import com.quanttrading.data.model.StockData
import com.quanttrading.data.model.StockDataDto
import com.quanttrading.data.model.SearchStock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockRepository {

    suspend fun searchStock(keyword: String): Result<List<SearchStock>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.stockApiService.searchStock(keyword)
                val stocks = response.data?.stockList ?: emptyList()
                Result.success(stocks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getRealTimeStock(secid: String): Result<StockDataDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.stockApiService.getRealTimeStock(secid)
                val data = response.data
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("股票数据不存在"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getMarketStockList(): Result<List<SearchStock>> {
        return withContext(Dispatchers.IO) {
            try {
                val allStocks = mutableListOf<SearchStock>()
                var page = 1
                var hasMore = true

                while (hasMore && page <= 5) {
                    val response = ApiClient.stockApiService.getMarketStockList(page)
                    val stocks = response.data?.stockList ?: emptyList()
                    if (stocks.isNotEmpty()) {
                        allStocks.addAll(stocks)
                        page++
                    } else {
                        hasMore = false
                    }
                }
                Result.success(allStocks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 计算行业"上涨比例"排名。
     *
     * 公式（与用户需求一致）：
     *   ratio = (upCount / constituentCount) / totalAShareCount * 10000  （单位：万分之，记作 %）
     * 例如：新能源发电 35 只成份股，上涨 31 只，A股总数 5523，
     *      ratio = (31/35) / 5523 * 10000 = 1.60%
     *
     * 说明：东方财富 clist 接口未提供申万三级行业的一键过滤项，
     * 这里采用东方财富行业板块分类（约 100+ 个板块）作为可比口径，业务逻辑一致。
     */
    suspend fun getIndustryRankings(topN: Int = 3): Result<List<IndustryRank>> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. 取 A 股总数作为分母
                val aShareResp = ApiClient.stockApiService.getAShareTotalCount()
                val totalAShareCount = aShareResp?.data?.total?.takeIf { it > 0 } ?: 5523

                // 2. 取行业板块列表
                val industryResp = ApiClient.stockApiService.getIndustryList()
                val industries = industryResp.data?.diff ?: emptyList()

                // 3. 计算每个行业的上涨比例并排序
                val ranks = industries.mapNotNull { dto ->
                    val stockCount = dto.upCount + dto.downCount + dto.flatCount
                    if (dto.code.isBlank() || stockCount <= 0) return@mapNotNull null
                    val upRatio = dto.upCount.toDouble() / stockCount.toDouble()
                    val ratio = upRatio / totalAShareCount.toDouble() * 10000.0
                    IndustryRank(
                        code = dto.code,
                        name = dto.name,
                        stockCount = stockCount,
                        upCount = dto.upCount,
                        downCount = dto.downCount,
                        totalAShareCount = totalAShareCount,
                        ratio = ratio
                    )
                }.sortedByDescending { it.ratio }
                    .take(topN)

                Result.success(ranks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 取得指定行业板块成份股中，按股价由低到高排序后的前 [topN] 只股票。
     *
     * 返回每只股票的：名称、代码、股价、同比营收、同比净利润、持仓占比
     * 持仓占比 = 股票总市值 / 板块成份股总市值 * 100%
     *
     * @param boardCode 行业板块代码，形如 "BK0473"
     */
    suspend fun getTopStocksForIndustry(boardCode: String, topN: Int = 10): Result<List<IndustryStock>> {
        return withContext(Dispatchers.IO) {
            try {
                val fs = "b:$boardCode"
                val resp = ApiClient.stockApiService.getIndustryStocks(fs = fs)
                val dtos: List<IndustryStockDto> = resp.data?.diff ?: emptyList()

                // 过滤掉无效价格（停牌、未上市等）的股票
                val valid = dtos.filter { it.price > 0 && it.code.isNotBlank() }

                // 板块成份股总市值（用作持仓占比的分母）
                val totalCap = valid.sumOf { it.totalMarketCap.takeIf { c -> c > 0 } ?: 0.0 }

                // 按股价升序后取前 N 只
                val top = valid.sortedBy { it.price }.take(topN)

                val result = top.map { dto ->
                    val holdingRatio = if (totalCap > 0 && dto.totalMarketCap > 0) {
                        dto.totalMarketCap / totalCap * 100.0
                    } else 0.0
                    IndustryStock(
                        code = dto.code,
                        name = dto.name,
                        price = dto.price,
                        changePercent = dto.changePercent,
                        revenueYoy = dto.revenueYoy,
                        profitYoy = dto.profitYoy,
                        totalMarketCap = dto.totalMarketCap,
                        holdingRatio = holdingRatio
                    )
                }
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

