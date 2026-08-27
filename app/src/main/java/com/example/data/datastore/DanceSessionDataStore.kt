package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "dance_session_prefs")

data class LastPlayedSession(
    val lessonId: String = "garden_toes",
    val lessonTitle: String = "Garden Toe Points & Soft Relevé",
    val moduleRoute: String = "toddler_story", // "toddler_story", "ballet_basics", "academy_training"
    val stepIndex: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val progressRatio: Float = 0f
)

class DanceSessionDataStore(private val context: Context) {

    companion object {
        private val KEY_LESSON_ID = stringPreferencesKey("last_lesson_id")
        private val KEY_LESSON_TITLE = stringPreferencesKey("last_lesson_title")
        private val KEY_MODULE_ROUTE = stringPreferencesKey("last_module_route")
        private val KEY_STEP_INDEX = intPreferencesKey("last_step_index")
        private val KEY_TIMESTAMP = longPreferencesKey("last_timestamp")
        private val KEY_PROGRESS_RATIO = floatPreferencesKey("last_progress_ratio")
        private val KEY_MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        private val KEY_VOICE_GUIDE_ENABLED = booleanPreferencesKey("voice_guide_enabled")
        private val KEY_NIGHT_MODE_ENABLED = booleanPreferencesKey("night_mode_enabled")
        private val KEY_HAS_SUGGESTED_RATE = booleanPreferencesKey("has_suggested_rate")
        private val KEY_HAS_SUGGESTED_SHARE = booleanPreferencesKey("has_suggested_share")
    }

    val isRateSuggested: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_HAS_SUGGESTED_RATE] ?: false
    }

    val isShareSuggested: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_HAS_SUGGESTED_SHARE] ?: false
    }

    suspend fun setRateSuggested(suggested: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_HAS_SUGGESTED_RATE] = suggested
        }
    }

    suspend fun setShareSuggested(suggested: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_HAS_SUGGESTED_SHARE] = suggested
        }
    }

    val isMusicEnabled: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_MUSIC_ENABLED] ?: true
    }

    val isVoiceGuideEnabled: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_VOICE_GUIDE_ENABLED] ?: true
    }

    val isNightModeEnabled: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_NIGHT_MODE_ENABLED] ?: false
    }


    val lastPlayedSession: Flow<LastPlayedSession> = context.sessionDataStore.data.map { prefs ->
        LastPlayedSession(
            lessonId = prefs[KEY_LESSON_ID] ?: "garden_toes",
            lessonTitle = prefs[KEY_LESSON_TITLE] ?: "Garden Toe Points & Soft Relevé",
            moduleRoute = prefs[KEY_MODULE_ROUTE] ?: "toddler_story",
            stepIndex = prefs[KEY_STEP_INDEX] ?: 0,
            timestamp = prefs[KEY_TIMESTAMP] ?: System.currentTimeMillis(),
            progressRatio = prefs[KEY_PROGRESS_RATIO] ?: 0f
        )
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_MUSIC_ENABLED] = enabled
        }
    }

    suspend fun setVoiceGuideEnabled(enabled: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_VOICE_GUIDE_ENABLED] = enabled
        }
    }

    suspend fun setNightModeEnabled(enabled: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_NIGHT_MODE_ENABLED] = enabled
        }
    }


    suspend fun saveLastSession(
        lessonId: String,
        lessonTitle: String,
        moduleRoute: String,
        stepIndex: Int = 0,
        progressRatio: Float = 0f
    ) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_LESSON_ID] = lessonId
            prefs[KEY_LESSON_TITLE] = lessonTitle
            prefs[KEY_MODULE_ROUTE] = moduleRoute
            prefs[KEY_STEP_INDEX] = stepIndex
            prefs[KEY_TIMESTAMP] = System.currentTimeMillis()
            prefs[KEY_PROGRESS_RATIO] = progressRatio
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
