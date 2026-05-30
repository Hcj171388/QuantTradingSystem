package com.quanttrading.data.repository

import com.quanttrading.data.api.ApiClient
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
    
    suspend fun getMarketStockList(market: String = "m"): Result<List<SearchStock>> {
        return withContext(Dispatchers.IO) {
            try {
                val allStocks = mutableListOf<SearchStock>()
                var page = 1
                var hasMore = true
                
                while (hasMore && page <= 5) {
                    val response = ApiClient.stockApiService.getMarketStockList(market, page)
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
}
