package com.deepagent.launcher.ai

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

data class ScreenAnalysisResult(
    val text: String,
    val labels: List<String>,
    val objects: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

class ScreenAnalyzer {
    
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    private val objectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
    )
    
    suspend fun analyzeScreen(bitmap: Bitmap): ScreenAnalysisResult {
        val image = InputImage.fromBitmap(bitmap, 0)
        
        // Run all analyses in parallel
        val textResult = recognizeText(image)
        val labelsResult = detectLabels(image)
        val objectsResult = detectObjects(image)
        
        return ScreenAnalysisResult(
            text = textResult,
            labels = labelsResult,
            objects = objectsResult
        )
    }
    
    private suspend fun recognizeText(image: InputImage): String {
        return try {
            val result = textRecognizer.process(image).await()
            result.text
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
    
    private suspend fun detectLabels(image: InputImage): List<String> {
        return try {
            val result = imageLabeler.process(image).await()
            result.map { it.text }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    private suspend fun detectObjects(image: InputImage): List<String> {
        return try {
            val result = objectDetector.process(image).await()
            result.mapNotNull { it.labels.firstOrNull()?.text }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    fun close() {
        textRecognizer.close()
        imageLabeler.close()
        objectDetector.close()
    }
}
