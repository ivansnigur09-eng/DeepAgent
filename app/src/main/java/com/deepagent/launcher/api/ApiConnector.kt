package com.deepagent.launcher.api

import com.deepagent.launcher.DeepAgentApplication
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Universal API Connector Framework
 * Supports multiple API services with authentication, rate limiting, and error handling
 */
abstract class ApiConnector(
    private val baseUrl: String,
    private val serviceName: String
) {
    
    protected val secureStorage = DeepAgentApplication.instance.secureStorage
    
    protected val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(createAuthInterceptor())
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(createRateLimitInterceptor())
            .build()
    }
    
    private fun createAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val apiKey = secureStorage.getApiKey(serviceName)
            val request = if (apiKey != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", getAuthHeader(apiKey))
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }
    }
    
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    private fun createRateLimitInterceptor(): Interceptor {
        return Interceptor { chain ->
            // Simple rate limiting - can be enhanced
            Thread.sleep(100) // 10 requests per second max
            chain.proceed(chain.request())
        }
    }
    
    protected open fun getAuthHeader(apiKey: String): String {
        return "Bearer $apiKey"
    }
    
    abstract suspend fun testConnection(): Boolean
}

/**
 * API Response wrapper for consistent error handling
 */
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}
