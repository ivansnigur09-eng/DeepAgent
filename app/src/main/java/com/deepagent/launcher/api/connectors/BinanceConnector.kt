package com.deepagent.launcher.api.connectors

import com.deepagent.launcher.api.ApiConnector
import com.deepagent.launcher.api.ApiResponse
import com.deepagent.launcher.api.models.CryptoPrice
import com.deepagent.launcher.api.models.TradingSignal
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

class BinanceConnector : ApiConnector(
    baseUrl = "https://api.binance.com",
    serviceName = "binance"
) {
    
    private val api: BinanceApi by lazy {
        retrofit.create(BinanceApi::class.java)
    }
    
    suspend fun getPrice(symbol: String): ApiResponse<CryptoPrice> {
        return try {
            val response = api.getPrice(symbol)
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                ApiResponse.Success(
                    CryptoPrice(
                        symbol = data.symbol,
                        price = data.price.toDouble(),
                        timestamp = System.currentTimeMillis()
                    )
                )
            } else {
                ApiResponse.Error("Failed to fetch price", response.code())
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error")
        }
    }
    
    suspend fun get24hrTicker(symbol: String): ApiResponse<Ticker24hr> {
        return try {
            val response = api.get24hrTicker(symbol)
            if (response.isSuccessful && response.body() != null) {
                ApiResponse.Success(response.body()!!)
            } else {
                ApiResponse.Error("Failed to fetch ticker", response.code())
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error")
        }
    }
    
    suspend fun getAllPrices(): ApiResponse<List<CryptoPrice>> {
        return try {
            val response = api.getAllPrices()
            if (response.isSuccessful && response.body() != null) {
                val prices = response.body()!!.map {
                    CryptoPrice(
                        symbol = it.symbol,
                        price = it.price.toDouble(),
                        timestamp = System.currentTimeMillis()
                    )
                }
                ApiResponse.Success(prices)
            } else {
                ApiResponse.Error("Failed to fetch prices", response.code())
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error")
        }
    }
    
    /**
     * Detect arbitrage opportunities between trading pairs
     */
    suspend fun detectArbitrage(): ApiResponse<List<TradingSignal>> {
        return try {
            val allPrices = getAllPrices()
            if (allPrices is ApiResponse.Success) {
                val signals = analyzeArbitrage(allPrices.data)
                ApiResponse.Success(signals)
            } else {
                ApiResponse.Error("Failed to detect arbitrage")
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error")
        }
    }
    
    private fun analyzeArbitrage(prices: List<CryptoPrice>): List<TradingSignal> {
        val signals = mutableListOf<TradingSignal>()
        
        // Simple triangular arbitrage detection
        // BTC/USDT -> ETH/BTC -> ETH/USDT
        val btcUsdt = prices.find { it.symbol == "BTCUSDT" }
        val ethBtc = prices.find { it.symbol == "ETHBTC" }
        val ethUsdt = prices.find { it.symbol == "ETHUSDT" }
        
        if (btcUsdt != null && ethBtc != null && ethUsdt != null) {
            val calculatedEthUsdt = btcUsdt.price * ethBtc.price
            val actualEthUsdt = ethUsdt.price
            val difference = ((calculatedEthUsdt - actualEthUsdt) / actualEthUsdt) * 100
            
            if (Math.abs(difference) > 0.5) { // 0.5% threshold
                signals.add(
                    TradingSignal(
                        type = if (difference > 0) "BUY" else "SELL",
                        symbol = "ETHUSDT",
                        price = actualEthUsdt,
                        confidence = 0.75f,
                        reason = "Triangular arbitrage opportunity: ${String.format("%.2f", difference)}%",
                        expectedProfit = Math.abs(difference)
                    )
                )
            }
        }
        
        return signals
    }
    
    override suspend fun testConnection(): Boolean {
        return try {
            val response = api.ping()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
    
    interface BinanceApi {
        @GET("/api/v3/ping")
        suspend fun ping(): Response<Unit>
        
        @GET("/api/v3/ticker/price")
        suspend fun getPrice(@Query("symbol") symbol: String): Response<PriceResponse>
        
        @GET("/api/v3/ticker/price")
        suspend fun getAllPrices(): Response<List<PriceResponse>>
        
        @GET("/api/v3/ticker/24hr")
        suspend fun get24hrTicker(@Query("symbol") symbol: String): Response<Ticker24hr>
    }
    
    data class PriceResponse(
        val symbol: String,
        val price: String
    )
    
    data class Ticker24hr(
        val symbol: String,
        val priceChange: String,
        val priceChangePercent: String,
        val lastPrice: String,
        val volume: String,
        val quoteVolume: String,
        val highPrice: String,
        val lowPrice: String
    )
}
