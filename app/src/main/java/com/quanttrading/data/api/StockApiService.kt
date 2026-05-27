package com.quanttrading.data.api

import com.quanttrading.data.model.StockData
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApiService {
    
    @GET("api/stock/realtime")
    suspend fun getRealTimeStock(
        @Query("code") code: String
    ): StockData
    
    @GET("api/stock/historical")
    suspend fun getHistoricalData(
        @Query("code") code: String,
        @Query("period") period: String = "daily",
        @Query("count") count: Int = 100
    ): List<StockData>
    
    @GET("api/stock/list")
    suspend fun getStockList(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 100
    ): List<StockData>
    
    @GET("api/stock/search")
    suspend fun searchStock(
        @Query("keyword") keyword: String
    ): List<StockData>
}