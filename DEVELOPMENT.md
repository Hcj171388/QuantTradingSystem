# 量化交易分析系统 - 完整开发文档

## 项目概述

这是一个基于Android的量化交易分析系统，提供股票数据查询、技术指标分析和买卖点预测功能。系统采用MVVM架构，使用Kotlin开发，集成了15个核心量化因子。

## 关于同花顺API的重要说明

### 同花顺iFinD API现状

**同花顺iFinD数据服务是付费产品，不提供公开的免费API接口。**

iFinD是同花顺面向机构客户提供的专业金融数据服务，需要购买授权才能使用。因此，本系统设计为支持多种数据源，您可以选择以下方案：

## 推荐的数据源方案

### 方案1：Tushare Pro API（推荐）

**优点：**
- 有免费额度（每分钟120次请求）
- 数据质量高，更新及时
- API文档完善，易于集成
- 支持A股、港股、美股等市场

**使用步骤：**

1. 注册账号：https://tushare.pro/
2. 获取API Token
3. 在项目中配置：

```kotlin
// 修改 data/api/StockApiService.kt
object ApiConfig {
    const val TUSHARE_TOKEN = "your_token_here"
    const val BASE_URL = "https://api.tushare.pro/"
}

// 修改数据请求逻辑
interface StockApiService {
    @POST("api")
    suspend fun getRealTimeStock(
        @Body request: TushareRequest
    ): TushareResponse<StockData>
    
    @POST("api")
    suspend fun getHistoricalData(
        @Body request: TushareRequest
    ): TushareResponse<List<StockData>>
}

data class TushareRequest(
    val api_name: String,
    val token: String,
    val params: Map<String, Any>,
    val fields: String
)
```

**API调用示例：**

```kotlin
// 获取实时行情
val request = TushareRequest(
    api_name = "daily",
    token = ApiConfig.TUSHARE_TOKEN,
    params = mapOf(
        "ts_code" to "000001.SZ",
        "trade_date" to getCurrentDate()
    ),
    fields = "ts_code,trade_date,open,high,low,close,vol,amount"
)
```

### 方案2：东方财富网页数据（免费但需自行爬取）

东方财富提供免费的网页数据接口，但需要解析HTML或JSON。

```kotlin
// 东方财富API示例
suspend fun fetchEastMoneyData(stockCode: String): StockData {
    val url = "http://push2.eastmoney.com/api/qt/stock/get"
    val response = httpClient.get {
        url {
            protocol = URLProtocol.HTTP
            host = "push2.eastmoney.com"
            path("api/qt/stock/get")
            parameters.append("secid", stockCode)
            parameters.append("fields", "f57,f58,f107,f152,f43,f46,f44,f45,f168,f169,f84,f85,f116,f117,f60")
        }
    }
    return parseResponse(response)
}
```

### 方案3：购买同花顺iFinD授权（专业用户）

如果需要使用同花顺的完整数据服务：

1. 联系同花顺销售团队
2. 购买iFinD授权
3. 获取API密钥
4. 配置到项目中

## 编译和安装指南

### 前置要求

- **Android Studio**: Hedgehog (2023.1.1) 或更高版本
- **JDK**: 8 或更高版本
- **Android SDK**: API 34
- **Gradle**: 8.0 或更高版本
- **Kotlin**: 1.9.0

### 完整编译步骤

1. **克隆项目**

```bash
git clone <your-repository-url>
cd QuantTradingSystem
```

2. **打开Android Studio**

- 选择 "Open an Existing Project"
- 选择项目根目录

3. **配置Gradle**

等待Gradle同步完成，首次同步可能需要下载依赖（5-10分钟）

4. **配置API密钥**

创建 `local.properties` 文件（在项目根目录）：

```properties
# API配置
API_KEY=your_api_key_here
API_BASE_URL=https://api.example.com
TUSHARE_TOKEN=your_tushare_token_here
```

5. **编译项目**

```bash
# 清理之前的构建
./gradlew clean

# 编译Debug版本
./gradlew assembleDebug

# 编译Release版本
./gradlew assembleRelease
```

6. **安装到设备**

```bash
# 通过USB连接设备后
./gradlew installDebug

# 或者直接安装APK文件
adb install app/build/outputs/apk/debug/app-debug.apk
```

### APK位置

编译成功后，APK文件位于：

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release.apk
```

## 项目结构详解

```
QuantTradingSystem/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/quanttrading/
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── StockData.kt          # 股票数据模型
│   │   │   │   │   │   ├── StockFactor.kt        # 因子数据模型
│   │   │   │   │   │   └── PredictionResult.kt   # 预测结果模型
│   │   │   │   │   ├── api/
│   │   │   │   │   │   └── StockApiService.kt    # API接口定义
│   │   │   │   │   └── repository/
│   │   │   │   │       └── StockRepository.kt    # 数据仓库
│   │   │   │   ├── domain/
│   │   │   │   │   └── analysis/
│   │   │   │   │       └── QuantAnalyzer.kt      # 量化分析核心逻辑
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MainActivity.kt           # 主Activity
│   │   │   │   │   ├── viewmodel/
│   │   │   │   │   │   └── StockViewModel.kt     # ViewModel
│   │   │   │   │   └── fragment/
│   │   │   │   │       ├── StockSearchFragment.kt
│   │   │   │   │       ├── AnalysisFragment.kt
│   │   │   │   │       └── BatchScreeningFragment.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── fragment_stock_search.xml
│   │   │   │   │   ├── fragment_analysis.xml
│   │   │   │   │   └── fragment_batch_screening.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── drawable/
│   │   │   ├── AndroidManifest.xml
│   │   └── assets/
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## 核心功能说明

### 1. 量化分析引擎（QuantAnalyzer.kt）

这是系统的核心，实现了15个量化因子计算：

```kotlin
class QuantAnalyzer {
    
    // 主分析方法
    fun analyzeStock(historicalData: List<StockData>): AnalysisResult
    
    // 因子计算方法
    private fun calculateMomentum(data: List<StockData>): Double
    private fun calculateVolatility(data: List<StockData>): Double
    private fun calculateRSI(data: List<StockData>): Double
    // ... 其他因子计算
    
    // 信号生成
    private fun generateSignal(factors: Map<String, Double>): TradingSignal
    
    // 概率计算
    private fun calculateProbability(factors: Map<String, Double>): Pair<Double, Double>
    
    // 置信度计算
    private fun calculateConfidence(factors: Map<String, Double>, signal: TradingSignal): Double
}
```

### 2. 因子权重配置

每个因子在信号生成中的权重：

| 因子 | 权重 | 说明 |
|------|------|------|
| 动量因子 | 2分 | 强烈动量倾向 |
| 趋势因子 | 2分 | 趋势确认 |
| 均线信号 | 2分 | 多头排列 |
| 量比 | 1分 | 成交量配合 |
| RSI | 1分 | 超买超卖 |
| 价格行为 | 1分 | K线形态 |
| 市场情绪 | 1分 | 近期涨跌 |

**总分标准：**
- 7分以上：强烈买入/卖出
- 4-6分：买入/卖出
- 低于4分：持有

### 3. 数据仓库（StockRepository.kt）

负责数据获取和业务逻辑封装：

```kotlin
class StockRepository(
    private val apiService: StockApiService,
    private val analyzer: QuantAnalyzer
) {
    suspend fun getRealTimeStock(code: String): Result<StockData>
    suspend fun getHistoricalData(code: String, count: Int): Result<List<StockData>>
    suspend fun analyzeStock(code: String): Result<AnalysisResult>
    suspend fun batchAnalyzeStocks(codes: List<String>): Result<List<PredictionResult>>
}
```

## 使用示例

### 查询单只股票

```kotlin
// 在Fragment中调用
viewModel.loadRealTimeStock("000001")
viewModel.analyzeStock("000001")
```

### 批量分析多只股票

```kotlin
val stockCodes = listOf("000001", "000002", "000003", "600000", "600519")
viewModel.batchAnalyzeStocks(stockCodes)
```

### 按条件筛选

```kotlin
// 获取分析结果后进行筛选
val strongBuyStocks = viewModel.batchAnalysisResults.value
    .filter { it.signal == TradingSignal.STRONG_BUY }
    .sortedByDescending { it.upProbability }

val highConfidenceStocks = viewModel.batchAnalysisResults.value
    .filter { it.confidence > 70 }
    .sortedByDescending { it.confidence }
```

## 注意事项

1. **数据源限制**
   - 免费API有调用频率限制
   - 建议合理设置请求间隔
   - 考虑本地缓存减少API调用

2. **风险管理**
   - 本应用仅供学习参考
   - 不构成投资建议
   - 股市有风险，投资需谨慎

3. **性能优化**
   - 大量数据时使用分页加载
   - 长时间任务使用后台线程
   - 合理使用缓存机制

4. **法律合规**
   - 遵守证券交易所规定
   - 尊重数据源使用条款
   - 不用于非法用途

## 常见问题

### Q1: 编译失败，提示依赖下载失败？

A: 检查网络连接，尝试使用国内镜像：

```gradle
// build.gradle
repositories {
    maven { url 'https://maven.aliyun.com/repository/google' }
    maven { url 'https://maven.aliyun.com/repository/public' }
    google()
    mavenCentral()
}
```

### Q2: API调用失败？

A: 检查以下几点：
- API密钥是否正确配置
- 网络连接是否正常
- 是否超过API调用频率限制
- 数据源服务是否正常

### Q3: 如何添加新的量化因子？

A: 在QuantAnalyzer.kt中添加新的计算方法：

```kotlin
private fun calculateNewFactor(data: List<StockData>): Double {
    // 实现因子计算逻辑
    val result = ...
    return normalizeValue(result, min, max)
}
```

然后在calculateAllFactors方法中调用。

## 技术支持

- 项目文档：README.md
- 问题反馈：GitHub Issues
- 技术交流：[邮箱地址]

## 免责声明

本应用提供的技术分析工具和量化分析方法仅供学习参考。股市有风险，投资需谨慎。本应用不构成任何投资建议，不对用户的投资决策和结果承担责任。用户应根据自身风险承受能力理性投资。