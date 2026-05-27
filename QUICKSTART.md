# 量化交易分析系统

## 重要说明

**关于同花顺API：同花顺iFinD是付费服务，不提供免费公开API。本系统设计为支持多种数据源。**

## 快速开始

### 编译APK

1. 确保已安装Android Studio
2. 克隆项目
3. 配置数据源（推荐Tushare）
4. 运行编译命令

```bash
./gradlew assembleDebug
```

APK位置：`app/build/outputs/apk/debug/app-debug.apk`

## 推荐数据源

### Tushare Pro（推荐，有免费额度）
- 注册：https://tushare.pro/
- 免费额度：每分钟120次请求
- 配置方法见 `DEVELOPMENT.md`

### 东方财富（免费）
- 需要自行实现数据爬取
- 参考DEVELOPMENT.md中的示例

## 功能特性

- 15个量化因子分析
- 智能买卖点预测
- 批量股票筛选
- 实时行情查询

## 文档

- 完整开发文档：`DEVELOPMENT.md`
- 项目说明：`README.md`

## 免责声明

本应用仅供学习参考，不构成投资建议。股市有风险，投资需谨慎。