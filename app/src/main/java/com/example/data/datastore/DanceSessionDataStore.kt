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
        
        // Smart Rating prompt tracking
        private val KEY_HAS_RATED_APP = booleanPreferencesKey("has_rated_app")
        private val KEY_LAST_RATE_PROMPT_TIME = longPreferencesKey("last_rate_prompt_time")
        private val KEY_RATE_PROMPT_DISMISS_COUNT = intPreferencesKey("rate_prompt_dismiss_count")

        // Smart Sharing prompt tracking
        private val KEY_HAS_SHARED_APP = booleanPreferencesKey("has_shared_app")
        private val KEY_LAST_SHARE_PROMPT_TIME = longPreferencesKey("last_share_prompt_time")
        private val KEY_SHARE_PROMPT_DISMISS_COUNT = intPreferencesKey("share_prompt_dismiss_count")

        // Smart Update prompt tracking
        private val KEY_IGNORED_UPDATE_VERSION = intPreferencesKey("ignored_update_version")
        private val KEY_LAST_UPDATE_PROMPT_TIME = longPreferencesKey("last_update_prompt_time")
    }

    val hasRatedApp: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_HAS_RATED_APP] ?: false
    }

    val lastRatePromptTime: Flow<Long> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_LAST_RATE_PROMPT_TIME] ?: 0L
    }

    val ratePromptDismissCount: Flow<Int> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_RATE_PROMPT_DISMISS_COUNT] ?: 0
    }

    val hasSharedApp: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_HAS_SHARED_APP] ?: false
    }

    val lastSharePromptTime: Flow<Long> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_LAST_SHARE_PROMPT_TIME] ?: 0L
    }

    val sharePromptDismissCount: Flow<Int> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_SHARE_PROMPT_DISMISS_COUNT] ?: 0
    }

    val ignoredUpdateVersion: Flow<Int> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_IGNORED_UPDATE_VERSION] ?: 0
    }

    val lastUpdatePromptTime: Flow<Long> = context.sessionDataStore.data.map { prefs ->
        prefs[KEY_LAST_UPDATE_PROMPT_TIME] ?: 0L
    }

    suspend fun recordRateAction(rated: Boolean) {
        context.sessionDataStore.edit { prefs ->
            if (rated) {
                prefs[KEY_HAS_RATED_APP] = true
            } else {
                val count = prefs[KEY_RATE_PROMPT_DISMISS_COUNT] ?: 0
                prefs[KEY_RATE_PROMPT_DISMISS_COUNT] = count + 1
                prefs[KEY_LAST_RATE_PROMPT_TIME] = System.currentTimeMillis()
            }
        }
    }

    suspend fun recordShareAction(shared: Boolean) {
        context.sessionDataStore.edit { prefs ->
            if (shared) {
                prefs[KEY_HAS_SHARED_APP] = true
            } else {
                val count = prefs[KEY_SHARE_PROMPT_DISMISS_COUNT] ?: 0
                prefs[KEY_SHARE_PROMPT_DISMISS_COUNT] = count + 1
                prefs[KEY_LAST_SHARE_PROMPT_TIME] = System.currentTimeMillis()
            }
        }
    }

    suspend fun recordUpdateAction(later: Boolean, skipVersionCode: Int = 0) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_LAST_UPDATE_PROMPT_TIME] = System.currentTimeMillis()
            if (skipVersionCode > 0) {
                prefs[KEY_IGNORED_UPDATE_VERSION] = skipVersionCode
            }
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
