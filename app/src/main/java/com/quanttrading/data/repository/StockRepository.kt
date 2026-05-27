package com.quanttrading.data.repository

import com.quanttrading.data.api.ApiClient
import com.quanttrading.data.model.StockInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockRepository {
    
    suspend fun searchStock(keyword: String): Result<List<StockInfo>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.stockApiService.searchStock(keyword)
                val stocks = response.data?.stockList?.map { stock ->
                    val secid = when {
                        stock.marketType.startsWith("0") || stock.marketType.startsWith("3") -> "0.${stock.code}"
                        stock.marketType.startsWith("6") -> "1.${stock.code}"
                        else -> "1.${stock.code}"
                    }
                    StockInfo(stock.code, stock.displayName, secid)
                } ?: emptyList()
                Result.success(stocks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getRealTimeStock(secid: String): Result<com.quanttrading.data.model.StockDataDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.stockApiService.getRealTimeStock(secid)
                if (response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception("股票数据不存在"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
