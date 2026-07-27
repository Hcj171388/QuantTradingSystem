package com.quanttrading.data.api

import com.quanttrading.data.model.EastMoneyResponse
import com.quanttrading.data.model.IndustryListResponse
import com.quanttrading.data.model.IndustryStockListResponse
import com.quanttrading.data.model.SearchResponse
import com.quanttrading.data.model.SearchResponse as MarketListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApiService {

    @GET("api/qt/stock/get")
    suspend fun getRealTimeStock(
        @Query("secid") secid: String,
        @Query("fields") fields: String = "f57,f58,f107,f152,f43,f46,f44,f45,f168,f169,f84,f85,f116,f117,f60,f49,f2,f3,f4,f5,f6,f7,f8,f15,f221,f186,f187"
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
    
    @GET("api/qt/dev/list")
    suspend fun getMarketStockList(
        @Query("pn") pn: Int,
        @Query("pz") pz: Int = 100,
        @Query("po") po: String = "1",
        @Query("np") np: Int = 1,
        @Query("fltt") fltt: Int = 2,
        @Query("invt") invt: Int = 2,
        @Query("fid") fid: String = "f12",
        @Query("fs") fs: String = "m:0 t:81 s:2048,m:0 t:80,m:1 t:2 s:2048",
        @Query("fields") fields: String = "f12,f14,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f15,f16,f17,f18,f19,f20,f22,f23,f24,f25,f26,f32,f33,f34,f35,f36,f37,f38,f39,f40,f41,f42,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f53,f54,f55,f56,f57,f60,f61,f62,f63,f64,f65,f66,f67,f68,f69,f70,f71,f72,f73,f74,f75,f76,f77,f78,f79,f80,f81,f82,f83,f84,f85,f86,f87,f88,f89,f90,f91,f92,f93,f94,f95,f96,f97,f98,f99,f100,f116,f117,f118,f119,f120,f121,f122,f123,f124,f125,f126,f127,f128,f129,f130,f131,f132,f133,f134,f135,f136,f137,f138,f139,f140,f141,f142,f143,f144,f145,f146,f147,f148,f149,f150,f151,f152,f153,f154,f155,f156,f157,f158,f159,f160,f161,f162,f163,f164,f165,f166,f167,f168,f169,f170,f171,f172,f173,f174,f175,f176,f177,f178,f179,f180,f181,f182,f183,f184,f185,f186,f187,f188,f189,f190,f191,f192,f193,f194,f195,f196,f197,f198,f199,f200,f201,f209,f210,f211,f212,f213,f214,f215,f216,f217,f218,f219,f220,f221,f222,f223,f224,f225,f226,f227"
    ): MarketListResponse

    /**
     * 获取行业板块列表（东方财富行业分类，约 100+ 个板块）
     * - fs=m:90+t:1 表示"行业板块"
     * - f104/f105: 上涨/下跌家数
     * - f140/f141: 总市值/流通市值
     *
     * 说明：东方财富 clist 接口对于申万三级行业没有公开的 m+t 一键过滤项，
     * 这里采用东方财富行业板块分类作为可比口径，业务逻辑一致。
     */
    @GET("api/qt/clist/get")
    suspend fun getIndustryList(
        @Query("pn") pn: Int = 1,
        @Query("pz") pz: Int = 500,
        @Query("po") po: String = "1",
        @Query("np") np: Int = 1,
        @Query("fltt") fltt: Int = 2,
        @Query("invt") invt: Int = 2,
        @Query("fid") fid: String = "f3",
        @Query("fs") fs: String = "m:90+t:1",
        @Query("fields") fields: String = "f12,f14,f3,f104,f105,f106,f128,f136,f140,f141"
    ): IndustryListResponse

    /**
     * 获取指定行业板块的成份股
     * - boardCode 形如 "BK0473"
     * - 字段含 f186/f187 同比营收、同比净利润（best-effort，可能为空）
     * - f20 总市值用于计算"持仓占比 = 个股市值 / 板块总市值 * 100%"
     */
    @GET("api/qt/clist/get")
    suspend fun getIndustryStocks(
        @Query("pn") pn: Int = 1,
        @Query("pz") pz: Int = 200,
        @Query("po") po: String = "1",
        @Query("np") np: Int = 1,
        @Query("fltt") fltt: Int = 2,
        @Query("invt") invt: Int = 2,
        @Query("fid") fid: String = "f2",
        @Query("fs") fs: String,
        @Query("fields") fields: String = "f2,f3,f4,f5,f6,f7,f8,f9,f12,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f33,f34,f35,f115,f128,f136,f173,f184,f185,f186,f187"
    ): IndustryStockListResponse

    /**
     * 获取沪深 A 股总数（用于计算"上涨比例"分母）
     * 通过 pz=1 + 读取响应中的 total 字段实现。
     */
    @GET("api/qt/clist/get")
    suspend fun getAShareTotalCount(
        @Query("pn") pn: Int = 1,
        @Query("pz") pz: Int = 1,
        @Query("po") po: String = "1",
        @Query("np") np: Int = 1,
        @Query("fltt") fltt: Int = 2,
        @Query("invt") invt: Int = 2,
        @Query("fid") fid: String = "f12",
        @Query("fs") fs: String = "m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23,m:0+t:81+s:2048",
        @Query("fields") fields: String = "f12"
    ): IndustryListResponse
}
