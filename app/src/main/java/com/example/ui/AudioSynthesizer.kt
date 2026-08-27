package com.example.ui

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

data class MusicTrack(
    val id: String,
    val title: String,
    val composer: String,
    val emoji: String,
    val tempoDescription: String,
    val description: String
)

object MusicTrackCatalog {
    val tracks = listOf(
        MusicTrack(
            id = "chopin_waltz",
            title = "Grande Valse Brillante",
            composer = "Frédéric Chopin",
            emoji = "🌸",
            tempoDescription = "Lively & Graceful 3/4",
            description = "A warm, floating waltz ideal for beginner pliés and expressive toe painting."
        ),
        MusicTrack(
            id = "swan_lake",
            title = "Swan Lake Scene (Act II)",
            composer = "Pyotr Tchaikovsky",
            emoji = "🦢",
            tempoDescription = "Emotional & Majestic",
            description = "The iconic classical ballet masterpiece for fluid arm movements and swan leaps."
        ),
        MusicTrack(
            id = "bach_minuet",
            title = "Minuet in G Major",
            composer = "J.S. Bach",
            emoji = "👑",
            tempoDescription = "Steady & Regal",
            description = "Crisp rhythmic court dance perfect for first & second position footwork drills."
        ),
        MusicTrack(
            id = "nutcracker_flutes",
            title = "Dance of the Reed Flutes",
            composer = "Pyotr Tchaikovsky",
            emoji = "🧚",
            tempoDescription = "Playful & Bouncy",
            description = "Lighthearted, festive staccato melody perfect for jumping like a butterfly."
        ),
        MusicTrack(
            id = "moonlight_sonata",
            title = "Moonlight Adagio",
            composer = "Ludwig van Beethoven",
            emoji = "🌙",
            tempoDescription = "Serene & Continuous",
            description = "Slow, sustained harmony engineered for balance holding and space barre extensions."
        ),
        MusicTrack(
            id = "spring_allegro",
            title = "Spring Allegro (Four Seasons)",
            composer = "Antonio Vivaldi",
            emoji = "🌻",
            tempoDescription = "Joyful & Energetic",
            description = "Bright, celebratory Baroque rhythm for Grand Allegro leaps and dynamic combinations."
        )
    )

    fun getTrackById(id: String): MusicTrack {
        return tracks.find { it.id == id } ?: tracks[0]
    }
}

class AudioSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _currentTrackId = MutableStateFlow("chopin_waltz")
    val currentTrackId: StateFlow<String> = _currentTrackId.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    var isMuted: Boolean = false
        set(value) {
            field = value
            if (value) {
                stop()
            }
        }

    // Note frequencies in Hz
    private val NOTES = mapOf(
        "C4" to 261.63, "C#4" to 277.18, "D4" to 293.66, "D#4" to 311.13, "E4" to 329.63, "F4" to 349.23, "F#4" to 369.99,
        "G4" to 392.00, "G#4" to 415.30, "A4" to 440.00, "A#4" to 466.16, "B4" to 493.88,
        "C5" to 523.25, "C#5" to 554.37, "D5" to 587.33, "D#5" to 622.25, "E5" to 659.25, "F5" to 698.46, "F#5" to 739.99,
        "G5" to 783.99, "G#5" to 830.61, "A5" to 880.00, "A#5" to 932.33, "B5" to 987.77,
        "C6" to 1046.50, "C#6" to 1108.73, "D6" to 1174.66, "E6" to 1318.51, "G6" to 1567.98, "A6" to 1760.00
    )

    // Classical Melodies (Note Name to Duration in ms)
    private val CHOPIN_WALTZ = listOf(
        "E5" to 380, "G#5" to 380, "B5" to 380, "E6" to 550, "B5" to 380, "G#5" to 380,
        "A5" to 380, "C#5" to 380, "E5" to 380, "A6" to 550, "E5" to 380, "C#5" to 380,
        "F#5" to 380, "A5" to 380, "C6" to 380, "F#6" to 550, "C6" to 380, "A5" to 380,
        "E5" to 380, "G#5" to 380, "B5" to 380, "E6" to 700
    )

    private val SWAN_LAKE = listOf(
        "F#4" to 480, "B4" to 280, "C#5" to 280, "D5" to 480, "E5" to 280, "D5" to 280,
        "C#5" to 280, "B4" to 480, "F#4" to 380, "B4" to 480, "D5" to 480, "B4" to 750,
        "D5" to 480, "F#5" to 280, "G5" to 280, "A5" to 480, "B5" to 280, "A5" to 280,
        "G5" to 280, "F#5" to 480, "D5" to 380, "B4" to 750
    )

    private val BACH_MINUET = listOf(
        "D5" to 280, "G4" to 140, "A4" to 140, "B4" to 140, "C5" to 140, "D5" to 280,
        "G4" to 280, "G4" to 280, "E5" to 280, "C5" to 140, "D5" to 140, "E5" to 140,
        "F#5" to 140, "G5" to 280, "G4" to 280, "G4" to 280, "C5" to 280, "D5" to 140,
        "C5" to 140, "B4" to 140, "A4" to 140, "B4" to 280, "C5" to 140, "B4" to 140,
        "A4" to 140, "G4" to 140, "F#4" to 280, "G4" to 280, "A4" to 280, "D4" to 550
    )

    private val NUTCRACKER_FLUTES = listOf(
        "G5" to 180, "E5" to 180, "C5" to 180, "E5" to 180, "G5" to 360, "C6" to 360,
        "B5" to 180, "A5" to 180, "G5" to 180, "F5" to 180, "E5" to 360, "G5" to 360,
        "F5" to 180, "D5" to 180, "B4" to 180, "D5" to 180, "F5" to 360, "B5" to 360,
        "A5" to 180, "G5" to 180, "F5" to 180, "E5" to 180, "C5" to 500
    )

    private val MOONLIGHT_SONATA = listOf(
        "C#4" to 240, "E4" to 240, "G#4" to 240, "C#4" to 240, "E4" to 240, "G#4" to 240,
        "C#4" to 240, "E4" to 240, "G#4" to 240, "C#4" to 240, "E4" to 240, "G#4" to 240,
        "B3" to 240, "E4" to 240, "G#4" to 240, "B3" to 240, "E4" to 240, "G#4" to 240,
        "A3" to 240, "C#4" to 240, "E4" to 240, "A3" to 240, "C#4" to 240, "E4" to 240,
        "G#3" to 240, "C#4" to 240, "E4" to 240, "G#3" to 240, "B#3" to 240, "D#4" to 240,
        "C#4" to 700
    )

    private val SPRING_ALLEGRO = listOf(
        "E5" to 350, "G#5" to 180, "G#5" to 180, "G#5" to 180, "F#5" to 180, "E5" to 180, "B4" to 350,
        "B4" to 180, "B4" to 180, "E5" to 350, "G#5" to 180, "G#5" to 180, "G#5" to 180, "F#5" to 180,
        "E5" to 180, "B4" to 450, "B4" to 200, "C#5" to 200, "D#5" to 200, "E5" to 600
    )

    private val CELEBRATION_FANFARE = listOf(
        "C5" to 120, "E5" to 120, "G5" to 120, "C6" to 350,
        "G5" to 120, "C6" to 500
    )

    private val SPARKLE_CHIME = listOf(
        "G5" to 80, "B5" to 80, "D6" to 80, "G6" to 200
    )

    fun setTrackAndPlay(trackId: String) {
        _currentTrackId.value = trackId
        playMelody(trackId)
    }

    fun togglePlayback() {
        if (_isPlaying.value) {
            stop()
        } else {
            playMelody(_currentTrackId.value)
        }
    }

    fun playMelody(trackId: String) {
        stop()
        if (isMuted) return
        _currentTrackId.value = trackId
        _isPlaying.value = true

        val melody = when (trackId) {
            "chopin_waltz", "chopin" -> CHOPIN_WALTZ
            "swan_lake" -> SWAN_LAKE
            "bach_minuet", "bach" -> BACH_MINUET
            "nutcracker_flutes" -> NUTCRACKER_FLUTES
            "moonlight_sonata" -> MOONLIGHT_SONATA
            "spring_allegro" -> SPRING_ALLEGRO
            else -> CHOPIN_WALTZ
        }

        synthJob = scope.launch {
            try {
                initAudioTrack()
                audioTrack?.play()

                while (_isPlaying.value) {
                    for ((note, duration) in melody) {
                        if (!_isPlaying.value) break
                        val freq = NOTES[note] ?: 440.0
                        generateAndPlayTone(freq, duration)
                        delay(duration.toLong() + 35)
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioSynthesizer", "Error playing synthesizer: ${e.message}")
            }
        }
    }

    fun playCelebrationFanfare() {
        scope.launch {
            try {
                initAudioTrack()
                audioTrack?.play()
                for ((note, duration) in CELEBRATION_FANFARE) {
                    val freq = NOTES[note] ?: 523.25
                    generateAndPlayTone(freq, duration)
                    delay(duration.toLong() + 30)
                }
            } catch (e: Exception) {
                Log.e("AudioSynthesizer", "Error playing fanfare: ${e.message}")
            }
        }
    }

    fun playSparkleChime() {
        scope.launch {
            try {
                initAudioTrack()
                audioTrack?.play()
                for ((note, duration) in SPARKLE_CHIME) {
                    val freq = NOTES[note] ?: 783.99
                    generateAndPlayTone(freq, duration)
                    delay(duration.toLong() + 20)
                }
            } catch (e: Exception) {
                Log.e("AudioSynthesizer", "Error playing sparkle chime: ${e.message}")
            }
        }
    }

    private fun initAudioTrack() {
        if (audioTrack != null && audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
            return
        }
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    private fun generateAndPlayTone(frequency: Double, durationMs: Int) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val sample = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Exponential decay to sound like an acoustic piano / chime pluck
            val decay = exp(-4.0 * t)
            // Main sine wave + a warm sub-harmonic and pleasant harmonic overtones
            val wave = sin(2 * PI * frequency * t) +
                    0.4 * sin(2 * PI * (frequency / 2) * t) +
                    0.2 * sin(2 * PI * (frequency * 2) * t)

            val value = (wave * decay * Short.MAX_VALUE * 0.4).toInt()
            sample[i] = value.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        audioTrack?.write(sample, 0, numSamples)
    }

    fun stop() {
        _isPlaying.value = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioSynthesizer", "Error releasing track: ${e.message}")
        }
        audioTrack = null
    }
}
