package com.deepagent.launcher.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.Locale

class VoiceController(private val context: Context) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isListening = false
    
    private val _voiceCommands = MutableSharedFlow<String>(replay = 0)
    val voiceCommands: SharedFlow<String> = _voiceCommands
    
    private val _voiceStatus = MutableSharedFlow<VoiceStatus>(replay = 1)
    val voiceStatus: SharedFlow<VoiceStatus> = _voiceStatus
    
    init {
        initializeSpeechRecognizer()
        initializeTextToSpeech()
    }
    
    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isListening = true
                        emitStatus(VoiceStatus.LISTENING)
                    }
                    
                    override fun onBeginningOfSpeech() {
                        emitStatus(VoiceStatus.PROCESSING)
                    }
                    
                    override fun onRmsChanged(rmsdB: Float) {}
                    
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    
                    override fun onEndOfSpeech() {
                        isListening = false
                    }
                    
                    override fun onError(error: Int) {
                        isListening = false
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                            else -> "Unknown error"
                        }
                        emitStatus(VoiceStatus.ERROR(errorMsg))
                    }
                    
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        matches?.firstOrNull()?.let { command ->
                            emitCommand(command)
                            emitStatus(VoiceStatus.COMMAND_RECEIVED(command))
                        }
                    }
                    
                    override fun onPartialResults(partialResults: Bundle?) {}
                    
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }
    
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
            }
        }
    }
    
    fun startListening() {
        if (!isListening && speechRecognizer != null) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            speechRecognizer?.startListening(intent)
        }
    }
    
    fun stopListening() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
            emitStatus(VoiceStatus.IDLE)
        }
    }
    
    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    
    fun processCommand(command: String): VoiceCommandResult {
        val lowerCommand = command.lowercase()
        
        return when {
            // Trading commands
            lowerCommand.contains("trade") || lowerCommand.contains("торгувати") -> {
                VoiceCommandResult(
                    action = VoiceAction.START_TRADING,
                    response = "Activating trading bot. Monitoring crypto markets...",
                    confidence = 0.95f
                )
            }
            lowerCommand.contains("stop trading") || lowerCommand.contains("зупинити торгівлю") -> {
                VoiceCommandResult(
                    action = VoiceAction.STOP_TRADING,
                    response = "Stopping trading bot. All positions secured.",
                    confidence = 0.95f
                )
            }
            
            // Job search commands
            lowerCommand.contains("find job") || lowerCommand.contains("знайти роботу") -> {
                VoiceCommandResult(
                    action = VoiceAction.SEARCH_JOBS,
                    response = "Searching for profitable jobs on Upwork...",
                    confidence = 0.90f
                )
            }
            
            // Screen analysis
            lowerCommand.contains("analyze") || lowerCommand.contains("аналізувати") -> {
                VoiceCommandResult(
                    action = VoiceAction.ANALYZE_SCREEN,
                    response = "Analyzing screen content with AI...",
                    confidence = 0.90f
                )
            }
            
            // Revenue check
            lowerCommand.contains("revenue") || lowerCommand.contains("дохід") || 
            lowerCommand.contains("earnings") || lowerCommand.contains("заробіток") -> {
                VoiceCommandResult(
                    action = VoiceAction.SHOW_REVENUE,
                    response = "Opening revenue dashboard...",
                    confidence = 0.85f
                )
            }
            
            // Help
            lowerCommand.contains("help") || lowerCommand.contains("допомога") -> {
                VoiceCommandResult(
                    action = VoiceAction.SHOW_HELP,
                    response = "I can help you with trading, job search, screen analysis, and revenue tracking. What would you like to do?",
                    confidence = 1.0f
                )
            }
            
            else -> {
                VoiceCommandResult(
                    action = VoiceAction.UNKNOWN,
                    response = "I didn't understand that command. Say 'help' for available commands.",
                    confidence = 0.3f
                )
            }
        }
    }
    
    private fun emitCommand(command: String) {
        kotlinx.coroutines.GlobalScope.launch {
            _voiceCommands.emit(command)
        }
    }
    
    private fun emitStatus(status: VoiceStatus) {
        kotlinx.coroutines.GlobalScope.launch {
            _voiceStatus.emit(status)
        }
    }
    
    fun cleanup() {
        speechRecognizer?.destroy()
        textToSpeech?.shutdown()
        speechRecognizer = null
        textToSpeech = null
    }
}

sealed class VoiceStatus {
    object IDLE : VoiceStatus()
    object LISTENING : VoiceStatus()
    object PROCESSING : VoiceStatus()
    data class COMMAND_RECEIVED(val command: String) : VoiceStatus()
    data class ERROR(val message: String) : VoiceStatus()
}

data class VoiceCommandResult(
    val action: VoiceAction,
    val response: String,
    val confidence: Float
)

enum class VoiceAction {
    START_TRADING,
    STOP_TRADING,
    SEARCH_JOBS,
    ANALYZE_SCREEN,
    SHOW_REVENUE,
    SHOW_HELP,
    UNKNOWN
}
