package com.deepagent.launcher.api.models

data class CryptoPrice(
    val symbol: String,
    val price: Double,
    val timestamp: Long
)

data class TradingSignal(
    val type: String, // BUY, SELL, HOLD
    val symbol: String,
    val price: Double,
    val confidence: Float, // 0.0 to 1.0
    val reason: String,
    val expectedProfit: Double, // in percentage
    val timestamp: Long = System.currentTimeMillis()
)

data class Trade(
    val id: String,
    val symbol: String,
    val type: String, // BUY or SELL
    val amount: Double,
    val price: Double,
    val timestamp: Long,
    val status: TradeStatus
)

enum class TradeStatus {
    PENDING,
    EXECUTED,
    FAILED,
    CANCELLED
}

data class Portfolio(
    val totalValue: Double,
    val holdings: Map<String, Double>, // symbol to amount
    val profitLoss: Double,
    val profitLossPercent: Double
)
