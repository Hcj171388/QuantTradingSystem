# 量化交易分析系统 - 项目交付说明

## 项目概述

已为您完成量化交易分析Android系统的开发，包含完整的源代码和文档。

## 关于同花顺API的重要说明

**同花顺iFinD是付费服务，不提供免费公开API接口。**

本系统设计为支持多种数据源，推荐使用Tushare Pro（有免费额度）或东方财富（免费但需自行爬取）。

## 已完成的功能模块

### 1. 核心分析引擎
- **QuantAnalyzer.kt** - 15个量化因子分析算法
- **StockRepository.kt** - 数据访问层
- **StockViewModel.kt** - MVVM架构的ViewModel

### 2. 15个量化因子
1. 动量因子（Momentum）
2. 波动率（Volatility）
3. 流动性（Liquidity）
4. 趋势因子（Trend）
5. 量比（Volume Ratio）
6. 价格位置（Price Position）
7. 均线信号（Moving Average Signal）
8. RSI相对强弱指标
9. MACD指标
10. KDJ指标
11. 布林带位置（Bollinger Position）
12. 价格行为（Price Action）
13. 换手率（Turnover Rate）
14. 市场情绪（Market Sentiment）
15. 相对强度（Relative Strength）

### 3. 智能预测功能
- 上涨/下跌概率计算
- 交易信号生成（5级：强烈买入/买入/持有/卖出/强烈卖出）
- 置信度评估
- 因子权重分析

### 4. UI界面
- 股票查询界面
- 量化分析界面
- 批量筛选界面
- Material Design风格

## 项目文件清单

### 核心代码文件
```
app/src/main/java/com/quanttrading/
├── data/
│   ├── model/
│   │   └── StockData.kt           # 数据模型
│   ├── api/
│   │   └── StockApiService.kt     # API接口
│   └── repository/
│       └── StockRepository.kt     # 数据仓库
├── domain/
│   └── analysis/
│       └── QuantAnalyzer.kt       # 量化分析核心逻辑
└── ui/
    └── viewmodel/
        └── StockViewModel.kt      # ViewModel
```

### 布局文件
```
app/src/main/res/layout/
├── activity_main.xml              # 主界面
├── fragment_stock_search.xml      # 股票查询
└── fragment_analysis.xml          # 量化分析
```

### 配置文件
```
app/build.gradle                    # 应用依赖配置
build.gradle                        # 项目配置
settings.gradle                     # Gradle设置
gradle.properties                   # Gradle属性
AndroidManifest.xml                 # 应用清单
```

### 文档文件
```
README.md                           # 项目说明
DEVELOPMENT.md                      # 完整开发文档
QUICKSTART.md                       # 快速开始指南
```

## 如何编译APK

### 前置要求
- Android Studio (Hedgehog或更高版本)
- JDK 8+
- Android SDK 34
- Gradle 8.0+

### 编译步骤

1. **打开项目**
   - 启动Android Studio
   - 选择 "Open an Existing Project"
   - 选择项目目录：`/workspace/QuantTradingSystem`

2. **等待Gradle同步**
   - 首次打开会自动同步Gradle
   - 等待依赖下载完成（5-10分钟）

3. **配置数据源**
   创建 `local.properties` 文件（在项目根目录）：
   ```properties
   # Tushare配置（推荐）
   TUSHARE_TOKEN=your_token_here
   API_BASE_URL=https://api.tushare.pro/
   ```

4. **编译项目**
   - 点击菜单：Build → Build Bundle(s) / APK(s) → Build APK(s)
   - 或在终端运行：
     ```bash
     cd /workspace/QuantTradingSystem
     ./gradlew assembleDebug
     ```

5. **获取APK**
   - 编译完成后，APK位于：
     ```
     app/build/outputs/apk/debug/app-debug.apk
     ```

6. **安装到设备**
   - 通过USB连接Android设备
   - 运行：
     ```bash
     ./gradlew installDebug
     ```
   - 或直接安装APK文件：
     ```bash
     adb install app/build/outputs/apk/debug/app-debug.apk
     ```

## 数据源配置

### 方案1：Tushare Pro（推荐）

**优点：**
- 有免费额度（每分钟120次）
- 数据质量高
- API文档完善

**步骤：**

1. 注册账号：https://tushare.pro/
2. 获取API Token
3. 在项目中配置

修改 `StockApiService.kt`：

```kotlin
object ApiConfig {
    const val TUSHARE_TOKEN = "你的token"
    const val BASE_URL = "https://api.tushare.pro/"
}

interface StockApiService {
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

### 方案2：东方财富（免费）

需要实现数据爬取逻辑，参考 `DEVELOPMENT.md` 中的示例代码。

### 方案3：同花顺iFinD（付费）

需要购买授权，获取API密钥后配置。

## 功能使用说明

### 1. 查询单只股票

```kotlin
// 加载实时数据
viewModel.loadRealTimeStock("000001")

// 执行量化分析
viewModel.analyzeStock("000001")
```

### 2. 批量分析多只股票

```kotlin
val stockCodes = listOf("000001", "000002", "600000", "600519")
viewModel.batchAnalyzeStocks(stockCodes)
```

### 3. 按条件筛选

```kotlin
// 筛选强烈买入的股票
val strongBuyStocks = results
    .filter { it.signal == TradingSignal.STRONG_BUY }
    .sortedByDescending { it.upProbability }

// 筛选高置信度股票
val highConfidence = results
    .filter { it.confidence > 70 }
    .sortedByDescending { it.confidence }
```

## 技术架构

- **架构模式**: MVVM + Repository Pattern
- **编程语言**: Kotlin
- **UI框架**: Material Design Components
- **网络请求**: Retrofit2 + OkHttp
- **异步处理**: Kotlin Coroutines + Flow
- **依赖注入**: Hilt
- **数据库**: Room
- **图表库**: MPAndroidChart

## 注意事项

### 1. API限制
- 免费API有调用频率限制
- 建议合理设置请求间隔
- 考虑实现本地缓存

### 2. 风险提示
- 本应用仅供学习参考
- 不构成投资建议
- 股市有风险，投资需谨慎
- 请设置止损和仓位管理

### 3. 性能优化
- 大量数据时使用分页
- 长时间任务使用后台线程
- 合理使用缓存机制

### 4. 法律合规
- 遵守证券交易所规定
- 尊重数据源使用条款
- 不得用于非法用途

## 常见问题

### Q: 编译失败，提示依赖下载失败？
A: 配置国内镜像源，参考 `DEVELOPMENT.md` 中的说明。

### Q: API调用失败？
A: 检查：
- API密钥是否正确
- 网络连接是否正常
- 是否超过调用频率限制

### Q: 如何添加新的因子？
A: 在 `QuantAnalyzer.kt` 中添加新的计算方法，并在 `calculateAllFactors` 中调用。

## 项目特色

1. **完整的量化分析框架**
   - 15个核心因子
   - 智能信号生成
   - 概率预测算法

2. **专业的技术指标**
   - RSI、MACD、KDJ等经典指标
   - 自定义趋势和动量因子
   - 布林带等高级指标

3. **灵活的数据源**
   - 支持多种API
   - 易于扩展
   - 可配置化

4. **现代化架构**
   - MVVM模式
   - 响应式编程
   - 类型安全

## 文档索引

- **README.md** - 项目基本介绍
- **DEVELOPMENT.md** - 完整开发文档（推荐阅读）
- **QUICKSTART.md** - 快速开始指南

## 免责声明

本应用提供的技术分析工具和量化分析方法仅供学习参考。股市有风险，投资需谨慎。本应用不构成任何投资建议，不对用户的投资决策和结果承担责任。用户应根据自身风险承受能力理性投资。

## 联系支持

如需技术支持或有问题反馈，请查看项目文档或提交Issue。

---

**项目已完成，祝使用愉快！**