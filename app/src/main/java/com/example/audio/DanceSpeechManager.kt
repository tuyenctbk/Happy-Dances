package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class DanceSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _lastSpokenText = MutableStateFlow("")
    val lastSpokenText: StateFlow<String> = _lastSpokenText.asStateFlow()

    init {
        try {
            tts = TextToSpeech(appContext, this)
        } catch (e: Exception) {
            Log.e("DanceSpeechManager", "Failed to initialize TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.language = Locale.US
                }
                // Gentle, warm tone suited for toddlers and young children
                engine.setPitch(1.12f)
                engine.setSpeechRate(0.92f)

                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                        Log.e("DanceSpeechManager", "TTS error code: $errorCode for utterance: $utteranceId")
                    }
                })

                _isReady.value = true
                Log.d("DanceSpeechManager", "TextToSpeech successfully initialized")
            }
        } else {
            _isReady.value = false
            Log.e("DanceSpeechManager", "TextToSpeech initialization failed with status $status")
        }
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (text.isBlank()) return
        _lastSpokenText.value = text

        val cleanText = sanitizeForSpeech(text)
        if (_isReady.value && tts != null) {
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "dance_step_${System.currentTimeMillis()}")
            }
            tts?.speak(cleanText, queueMode, params, "dance_step_${System.currentTimeMillis()}")
        } else {
            Log.w("DanceSpeechManager", "TTS not ready yet. Skipping speech.")
        }
    }

    fun stop() {
        try {
            tts?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e("DanceSpeechManager", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            _isReady.value = false
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e("DanceSpeechManager", "Error shutting down TTS", e)
        }
    }

    /**
     * Cleans up emojis and special characters so Text-to-Speech speaks smoothly
     */
    private fun sanitizeForSpeech(input: String): String {
        return input
            .replace(Regex("[\\p{So}\\p{Cn}]"), "") // Strip emoji symbols
            .replace("1st", "first")
            .replace("2nd", "second")
            .replace("3rd", "third")
            .replace("plié", "plee-ay", ignoreCase = true)
            .replace("plie", "plee-ay", ignoreCase = true)
            .replace("tendu", "tahn-doo", ignoreCase = true)
            .replace("relevé", "rell-eh-vay", ignoreCase = true)
            .replace("releve", "rell-eh-vay", ignoreCase = true)
            .replace("battement", "baht-mahn", ignoreCase = true)
            .replace("développé", "day-vlo-pay", ignoreCase = true)
            .replace("jeté", "zheh-tay", ignoreCase = true)
            .replace("chassé", "shah-say", ignoreCase = true)
            .replace("port de bras", "por-duh-brah", ignoreCase = true)
            .trim()
    }
}
