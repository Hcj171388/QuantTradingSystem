package com.quanttrading.data.api

import com.quanttrading.data.model.EastMoneyResponse
import com.quanttrading.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApiService {
    
    @GET("api/qt/stock/get")
    suspend fun getRealTimeStock(
        @Query("secid") secid: String,
        @Query("fields") fields: String = "f57,f58,f107,f152,f43,f46,f44,f45,f168,f169,f84,f85,f116,f117,f60,f49,f2,f3,f4,f5,f6,f7,f8,f15,f221"
    ): EastMoneyResponse
    
    @GET("api/qt/stock/kline")
    suspend fun getHistoricalData(
        @Query("secid") secid: String,
        @Query("klt") klt: String = "101",
        @Query("fqt") fqt: String = "1",
        @Query("fields1") fields1: String = "f1,f2,f3,f4,f5,f6",
        @Query("fields2") fields2: String = "f51,f52,f53,f54,f55,f56,f57,f58",
        @Query("beg") beg: String = "19700101",
        @Query("end") end: String = "20991231"
    ): EastMoneyResponse
    
    @GET("api/suggest")
    suspend fun searchStock(
        @Query("key") key: String,
        @Query("type") type: String = "14",
        @Query("token") token: String = "D43BF722C8E33BDC906FE8DC9D82C9D3"
    ): SearchResponse
}
