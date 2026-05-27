# 量化交易分析系统

一个基于Android的量化交易分析应用，提供股票数据查询、技术指标分析和买卖点预测功能。

## 功能特性

### 1. 股票查询
- 实时股票价格查询
- 历史K线数据获取
- 股票搜索功能
- 支持股票代码和名称搜索

### 2. 量化分析
- 15个核心因子分析
  - 动量因子
  - 波动率
  - 流动性
  - 趋势因子
  - 量比
  - 价格位置
  - 均线信号
  - RSI相对强弱指标
  - MACD指标
  - KDJ指标
  - 布林带位置
  - 价格行为
  - 换手率
  - 市场情绪
  - 相对强度
  - 支撑阻力位

### 3. 智能预测
- 上涨/下跌概率计算
- 交易信号生成（强烈买入/买入/持有/卖出/强烈卖出）
- 置信度评估
- 因子权重分析

### 4. 批量筛选
- 多只股票批量分析
- 按信号强度排序
- 按上涨概率排序
- 自定义筛选条件

## 技术架构

### 前端技术栈
- **语言**: Kotlin
- **UI框架**: Material Design Components
- **架构**: MVVM + Repository Pattern
- **网络请求**: Retrofit2 + OkHttp
- **图表库**: MPAndroidChart
- **依赖注入**: Hilt
- **数据库**: Room
- **异步处理**: Kotlin Coroutines + Flow

### 后端API
- 支持接入多种数据源：
  - 同花顺iFinD API（需授权）
  - Tushare Pro API（推荐）
  - 东方财富网页数据
  - 新浪财经API

## 项目结构

```
QuantTradingSystem/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/quanttrading/
│   │       │   ├── data/
│   │       │   │   ├── model/          # 数据模型
│   │       │   │   ├── api/            # API接口
│   │       │   │   └── repository/     # 数据仓库
│   │       │   ├── domain/
│   │       │   │   └── analysis/       # 量化分析逻辑
│   │       │   ├── ui/
│   │       │   │   ├── viewmodel/      # ViewModel
│   │       │   │   ├── fragment/       # Fragment
│   │       │   │   └── adapter/        # Adapter
│   │       │   └── MainActivity.kt     # 主Activity
│   │       ├── res/
│   │       │   ├── layout/            # 布局文件
│   │       │   └── values/             # 资源文件
│   │       └── AndroidManifest.xml
│   └── build.gradle
└── README.md
```

## 编译和安装

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 8 或更高版本
- Android SDK API 34
- Gradle 8.0 或更高版本

### 编译步骤

1. 克隆项目
```bash
git clone <repository-url>
cd QuantTradingSystem
```

2. 打开Android Studio，导入项目

3. 配置API密钥（可选）
在 `local.properties` 中添加：
```properties
API_KEY=your_api_key_here
API_BASE_URL=https://api.example.com
```

4. 编译项目
```bash
./gradlew assembleDebug
```

5. 安装到设备
```bash
./gradlew installDebug
```

### 生成APK

调试版本：
```bash
./gradlew assembleDebug
```
APK位置：`app/build/outputs/apk/debug/app-debug.apk`

发布版本：
```bash
./gradlew assembleRelease
```
APK位置：`app/build/outputs/apk/release/app-release.apk`

## 数据源配置

### 使用Tushare API（推荐）

1. 注册账号：https://tushare.pro/
2. 获取API Token
3. 在项目中配置：

```kotlin
// data/api/StockApiService.kt
const val TUSHARE_TOKEN = "your_token_here"
const val TUSHARE_BASE_URL = "https://api.tushare.pro/"
```

### 使用东方财富数据

修改API请求地址和参数格式，参考东方财富的接口文档。

### 使用同花顺API

同花顺iFinD需要购买授权，获取API密钥后配置到项目中。

## 因子说明

### 动量因子（Momentum）
衡量股票价格的变化趋势，基于近期收益率计算。

### 波动率（Volatility）
反映价格波动的剧烈程度，基于历史收益率标准差。

### 流动性（Liquidity）
基于成交量评估股票的交易活跃度。

### 趋势因子（Trend）
通过均线系统判断股票的整体趋势方向。

### RSI（相对强弱指标）
超买超卖指标，范围0-100，70以上超买，30以下超卖。

### MACD（指数平滑异同移动平均线）
趋势跟踪指标，由DIF线和DEA线组成。

### KDJ（随机指标）
超买超卖指标，结合了价格位置和动量。

## 风险提示

1. **投资有风险，入市需谨慎**
2. 本应用仅供学习和研究使用，不构成投资建议
3. 量化分析结果仅供参考，不保证准确性
4. 请结合基本面分析和市场环境综合判断
5. 建议设置止损和仓位管理

## 法律声明

1. 本软件为开源项目，仅供技术交流使用
2. 不提供任何投资建议和收益保证
3. 用户使用本软件产生的任何损失，开发者不承担责任
4. 请遵守当地法律法规和证券交易所规定

## 开源协议

MIT License

## 联系方式

- 项目地址：[GitHub Repository]
- 问题反馈：[Issues]
- 邮箱：[Email]

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 实现基础股票查询功能
- 实现15个核心因子分析
- 实现智能买卖点预测
- 实现批量筛选功能

## 贡献指南

欢迎提交Pull Request或Issue，帮助改进项目。

1. Fork本仓库
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 免责声明

本应用提供的技术分析工具和量化分析方法仅供学习参考。股市有风险，投资需谨慎。本应用不构成任何投资建议，不对用户的投资决策和结果承担责任。用户应根据自身风险承受能力理性投资。