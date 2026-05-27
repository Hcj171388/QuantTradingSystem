package com.quanttrading.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    
    private const val EASTMONEY_BASE_URL = "https://push2.eastmoney.com/"
    private const val EASTMONEY_SEARCH_URL = "https://searchapi.eastmoney.com/"
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(EASTMONEY_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val stockApiService: StockApiService = retrofit.create(StockApiService::class.java)
}
