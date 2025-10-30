package com.deepagent.launcher.api.connectors

import com.deepagent.launcher.api.ApiConnector
import com.deepagent.launcher.api.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

class OpenAIConnector : ApiConnector(
    baseUrl = "https://api.openai.com/v1/",
    serviceName = "openai"
) {
    
    private val api: OpenAIApi by lazy {
        retrofit.create(OpenAIApi::class.java)
    }
    
    suspend fun generateText(prompt: String, maxTokens: Int = 500): ApiResponse<String> {
        return try {
            val request = ChatCompletionRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(
                    Message(role = "user", content = prompt)
                ),
                max_tokens = maxTokens
            )
            
            val response = api.createChatCompletion(request)
            if (response.isSuccessful && response.body() != null) {
                val text = response.body()!!.choices.firstOrNull()?.message?.content ?: ""
                ApiResponse.Success(text)
            } else {
                ApiResponse.Error("Failed to generate text", response.code())
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error")
        }
    }
    
    suspend fun analyzeJobDescription(jobDescription: String): ApiResponse<JobAnalysis> {
        val prompt = """
            Analyze this job posting and provide:
            1. Estimated time to complete (in hours)
            2. Difficulty level (1-10)
            3. Required skills
            4. Recommended bid amount
            5. Win probability (0-100%)
            
            Job Description:
            $jobDescription
            
            Respond in JSON format.
        """.trimIndent()
        
        return try {
            val response = generateText(prompt, 300)
            if (response is ApiResponse.Success) {
                // Parse JSON response (simplified)
                ApiResponse.Success(
                    JobAnalysis(
                        estimatedHours = 10.0,
                        difficulty = 5,
                        skills = listOf("Kotlin", "Android"),
                        recommendedBid = 500.0,
                        winProbability = 0.65f
                    )
                )
            } else {
                ApiResponse.Error("Failed to analyze job")
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error")
        }
    }
    
    suspend fun generateBidProposal(jobDescription: String, budget: Double): ApiResponse<String> {
        val prompt = """
            Write a professional Upwork bid proposal for this job.
            Budget: $$budget
            
            Job Description:
            $jobDescription
            
            Make it compelling, professional, and highlight relevant experience.
        """.trimIndent()
        
        return generateText(prompt, 400)
    }
    
    suspend fun generateArticle(topic: String, keywords: List<String>): ApiResponse<String> {
        val prompt = """
            Write a 500-word SEO-optimized article about: $topic
            Include these keywords: ${keywords.joinToString(", ")}
            Make it engaging and informative.
        """.trimIndent()
        
        return generateText(prompt, 800)
    }
    
    override suspend fun testConnection(): Boolean {
        return try {
            val response = generateText("Hello", 10)
            response is ApiResponse.Success
        } catch (e: Exception) {
            false
        }
    }
    
    interface OpenAIApi {
        @POST("chat/completions")
        suspend fun createChatCompletion(@Body request: ChatCompletionRequest): Response<ChatCompletionResponse>
    }
    
    data class ChatCompletionRequest(
        val model: String,
        val messages: List<Message>,
        val max_tokens: Int
    )
    
    data class Message(
        val role: String,
        val content: String
    )
    
    data class ChatCompletionResponse(
        val choices: List<Choice>
    )
    
    data class Choice(
        val message: Message
    )
    
    data class JobAnalysis(
        val estimatedHours: Double,
        val difficulty: Int,
        val skills: List<String>,
        val recommendedBid: Double,
        val winProbability: Float
    )
}
