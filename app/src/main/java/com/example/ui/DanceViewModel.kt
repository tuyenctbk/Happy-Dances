package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BadgeAchievement
import com.example.data.CompletedLesson
import com.example.data.DailyDanceSession
import com.example.data.DanceLesson
import com.example.data.DanceRepository
import com.example.data.DanceStreakInfo
import com.example.data.DancerProfile
import com.example.data.EarnedSticker
import com.example.data.Progress
import com.example.data.DanceLessonEntity
import com.example.notifications.DanceNotificationScheduler
import com.example.data.datastore.DanceSessionDataStore
import com.example.data.datastore.LastPlayedSession
import com.example.data.StudentMilestone
import com.example.data.FavoriteDanceMove
import com.example.data.DailyChallengeRecord
import com.example.data.DailyDanceChallenge
import com.example.data.DailyChallengesCatalog
import com.example.data.FavoriteDanceLesson
import com.example.data.RecentlyViewedLesson
import com.example.audio.DanceSpeechManager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DanceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository: DanceRepository = DanceRepository(database)
    private val sessionDataStore: DanceSessionDataStore = DanceSessionDataStore(application)
    val synthesizer = AudioSynthesizer()
    val speechManager = DanceSpeechManager(application)

    val isTtsReady: StateFlow<Boolean> = speechManager.isReady
    val isTtsSpeaking: StateFlow<Boolean> = speechManager.isSpeaking

    val isMusicEnabled: StateFlow<Boolean> = sessionDataStore.isMusicEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isVoiceGuideEnabled: StateFlow<Boolean> = sessionDataStore.isVoiceGuideEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val isNightModeEnabled: StateFlow<Boolean> = sessionDataStore.isNightModeEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isRateSuggested: StateFlow<Boolean> = sessionDataStore.isRateSuggested
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isShareSuggested: StateFlow<Boolean> = sessionDataStore.isShareSuggested
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val recentlyViewedLessons: StateFlow<List<RecentlyViewedLesson>> = repository.recentlyViewedLessonsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setNightModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            sessionDataStore.setNightModeEnabled(enabled)
        }
    }

    fun setRateSuggested(suggested: Boolean) {
        viewModelScope.launch {
            sessionDataStore.setRateSuggested(suggested)
        }
    }

    fun setShareSuggested(suggested: Boolean) {
        viewModelScope.launch {
            sessionDataStore.setShareSuggested(suggested)
        }
    }


    val favoriteDanceLessons: StateFlow<List<FavoriteDanceLesson>> = repository.allFavoriteDanceLessonsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteLessonIds: StateFlow<Set<String>> = repository.allFavoriteDanceLessonsFlow
        .map { list -> list.map { it.lessonId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    fun toggleFavoriteLesson(lesson: DanceLesson) {
        viewModelScope.launch {
            val isNowFav = repository.toggleFavoriteDanceLesson(lesson)
            if (isNowFav) {
                synthesizer.playSparkleChime()
            }
        }
    }

    fun isLessonFavorite(lessonId: String): Boolean {
        return favoriteLessonIds.value.contains(lessonId)
    }

    fun speakVoiceInstruction(text: String, force: Boolean = false) {
        if (force || isVoiceGuideEnabled.value) {
            speechManager.speak(text)
        }
    }

    fun stopVoiceInstruction() {
        speechManager.stop()
    }

    fun setBackgroundMusicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            sessionDataStore.setMusicEnabled(enabled)
            synthesizer.isMuted = !enabled
            if (!enabled) {
                synthesizer.stop()
            }
        }
    }

    fun setVoiceGuideEnabled(enabled: Boolean) {
        viewModelScope.launch {
            sessionDataStore.setVoiceGuideEnabled(enabled)
            if (!enabled) {
                speechManager.stop()
            }
        }
    }

    init {
        viewModelScope.launch {
            sessionDataStore.isMusicEnabled.collect { enabled ->
                synthesizer.isMuted = !enabled
            }
        }
        viewModelScope.launch {
            repository.initializeDefaultBadgesIfNeeded()
            val current = repository.getProfile()
            if (current.dailyReminderEnabled) {
                DanceNotificationScheduler.scheduleDailyReminder(
                    application,
                    current.dailyReminderHour,
                    current.dailyReminderMinute
                )
            }
        }
        viewModelScope.launch {
            try {
                val existing = repository.allDanceLessonEntitiesFlow.first()
                if (existing.isEmpty()) {
                    LessonsProvider.lessons.forEach { lesson ->
                        repository.updateDanceLessonEntityStatus(
                            lessonId = lesson.id,
                            lessonName = lesson.title,
                            category = lesson.ageCategory,
                            status = "Not Started"
                        )
                    }
                }
            } catch (e: Exception) {
                // Safeguard initial flow fetching
            }
        }
    }

    val todayDailyChallenge: DailyDanceChallenge = DailyChallengesCatalog.getTodayChallenge()

    val todayDailyChallengeRecord: StateFlow<DailyChallengeRecord?> = repository.getTodayDailyChallengeRecordFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val completedDailyChallenges: StateFlow<List<DailyChallengeRecord>> = repository.completedDailyChallengesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _showCertificateDialog = MutableStateFlow(false)
    val showCertificateDialog: StateFlow<Boolean> = _showCertificateDialog.asStateFlow()

    private val _showRateAppDialog = MutableStateFlow(false)
    val showRateAppDialog: StateFlow<Boolean> = _showRateAppDialog.asStateFlow()

    private val _showShareAppDialog = MutableStateFlow(false)
    val showShareAppDialog: StateFlow<Boolean> = _showShareAppDialog.asStateFlow()

    fun dismissRateAppDialog() {
        _showRateAppDialog.value = false
    }

    fun dismissShareAppDialog() {
        _showShareAppDialog.value = false
    }

    private val _selectedCertificateMilestone = MutableStateFlow<String>("Grand Ballet Star Award 🌸")
    val selectedCertificateMilestone: StateFlow<String> = _selectedCertificateMilestone.asStateFlow()

    fun openCertificateDialog(milestoneTitle: String = "Grand Ballet Star Award 🌸") {
        _selectedCertificateMilestone.value = milestoneTitle
        _showCertificateDialog.value = true
        synthesizer.playSparkleChime()
    }

    fun closeCertificateDialog() {
        _showCertificateDialog.value = false
    }

    fun completeDailyChallenge(challenge: DailyDanceChallenge) {
        viewModelScope.launch {
            val result = repository.recordDailyChallengeCompleted(challenge)
            synthesizer.stop()
            synthesizer.playCelebrationFanfare()

            val unlockedBadgeText = if (result.newlyUnlockedBadges.isNotEmpty()) {
                "🎖️ Unlocked: " + result.newlyUnlockedBadges.joinToString(", ")
            } else "⭐ Daily Movement Champion!"

            _celebrationEvent.value = CelebrationData(
                title = "🎉 Daily Dance Challenge Conquered!",
                subtitle = "You mastered today's '${challenge.moveName}' move! 🔥 Daily Streak: ${result.currentStreak} Days!",
                badgeUnlockedText = unlockedBadgeText,
                stickerTitle = "Daily Star: ${challenge.emoji}",
                isFullLessonCompletion = false
            )
        }
    }

    val lastPlayedSession: StateFlow<LastPlayedSession> = sessionDataStore.lastPlayedSession
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LastPlayedSession()
        )

    val studentMilestones: StateFlow<List<StudentMilestone>> = repository.allMilestonesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Expose flows from Repository as StateFlows
    val profileState: StateFlow<DancerProfile> = repository.profileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DancerProfile()
        )

    val progressRecords: StateFlow<List<Progress>> = repository.allProgressFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val completedLessons: StateFlow<List<CompletedLesson>> = repository.completedLessonsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allDanceLessonEntities: StateFlow<List<DanceLessonEntity>> = repository.allDanceLessonEntitiesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val completedLessonIds: StateFlow<Set<String>> = repository.completedLessonsFlow
        .map { list -> list.map { it.lessonId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val earnedStickers: StateFlow<List<EarnedSticker>> = repository.earnedStickersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val streakState: StateFlow<DanceStreakInfo> = repository.streakFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DanceStreakInfo()
        )

    val dailySessions: StateFlow<List<DailyDanceSession>> = repository.dailySessionsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allBadges: StateFlow<List<BadgeAchievement>> = repository.allBadgesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unlockedBadges: StateFlow<List<BadgeAchievement>> = repository.unlockedBadgesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteMoves: StateFlow<List<FavoriteDanceMove>> = repository.favoriteMovesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current screen navigation state
    // "intro_level", "map", "story_mode", "passport", "avatar", "parent", "lesson"
    private val _currentScreen = MutableStateFlow<String>("map")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Mode state: "storytelling" (Toddler focus) or "academy" (Technical Academy focus)
    private val _activeMode = MutableStateFlow<String>("storytelling")
    val activeMode: StateFlow<String> = _activeMode.asStateFlow()

    fun setMode(mode: String) {
        _activeMode.value = mode
    }

    fun selectLevelAndMode(level: String) {
        viewModelScope.launch {
            val current = repository.getProfile()
            val newMode = if (level == "3-5") "storytelling" else "academy"
            _activeMode.value = newMode
            repository.updateProfile(current.copy(ageGroup = level))
            _currentScreen.value = "map"
            synthesizer.playSparkleChime()
        }
    }

    fun recordSessionProgress(
        lessonId: String,
        lessonTitle: String,
        danceLevel: String,
        sessionMode: String,
        durationSeconds: Int = 180,
        accuracyScore: Float = 96f
    ) {
        viewModelScope.launch {
            repository.recordProgress(
                Progress(
                    lessonId = lessonId,
                    lessonTitle = lessonTitle,
                    danceLevel = danceLevel,
                    sessionMode = sessionMode,
                    isCompleted = true,
                    completionStatus = "completed",
                    repetitionsCount = 1,
                    durationSeconds = durationSeconds,
                    accuracyScore = accuracyScore,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun saveLastSessionState(
        lessonId: String,
        lessonTitle: String,
        moduleRoute: String,
        stepIndex: Int = 0,
        progressRatio: Float = 0f
    ) {
        viewModelScope.launch {
            sessionDataStore.saveLastSession(
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                moduleRoute = moduleRoute,
                stepIndex = stepIndex,
                progressRatio = progressRatio
            )
        }
    }

    fun resumeSavedSession() {
        viewModelScope.launch {
            val session = lastPlayedSession.value
            if (session.lessonId.isNotEmpty()) {
                selectLessonById(session.lessonId)
                _currentScreen.value = "lesson"
                synthesizer.playSparkleChime()
            }
        }
    }

    // Active story adventure state for Story Mode
    private val _activeStory = MutableStateFlow<StoryAdventure?>(null)
    val activeStory: StateFlow<StoryAdventure?> = _activeStory.asStateFlow()

    private val _storyChapterIndex = MutableStateFlow(0)
    val storyChapterIndex: StateFlow<Int> = _storyChapterIndex.asStateFlow()

    // Current active lesson state
    private val _activeLesson = MutableStateFlow<Lesson?>(null)
    val activeLesson: StateFlow<Lesson?> = _activeLesson.asStateFlow()

    // Milestone celebration state
    private val _celebrationEvent = MutableStateFlow<CelebrationData?>(null)
    val celebrationEvent: StateFlow<CelebrationData?> = _celebrationEvent.asStateFlow()

    fun selectLesson(lesson: Lesson) {
        _activeLesson.value = lesson
        _currentScreen.value = "lesson"
        viewModelScope.launch {
            repository.recordRecentlyViewed(lesson)
        }
        saveLastSessionState(

            lessonId = lesson.id,
            lessonTitle = lesson.title,
            moduleRoute = when (lesson.stage) {
                "3-5" -> "toddler_story"
                "6-8" -> "ballet_basics"
                else -> "academy_training"
            },
            stepIndex = 0
        )
        viewModelScope.launch {
            try {
                val entities = repository.allDanceLessonEntitiesFlow.first()
                val currentEntity = entities.find { it.id == lesson.id }
                if (currentEntity == null || currentEntity.completionStatus != "Completed") {
                    repository.updateDanceLessonEntityStatus(
                        lessonId = lesson.id,
                        lessonName = lesson.title,
                        category = lesson.ageCategory,
                        status = "In Progress"
                    )
                }
            } catch (e: Exception) {
                // Safeguard DB transaction
            }
        }
        val profile = profileState.value
        val trackToPlay = profile.selectedMusicTrack.ifEmpty {
            when (lesson.stage) {
                "3-5" -> "chopin_waltz"
                "6-8" -> "swan_lake"
                else -> "bach_minuet"
            }
        }
        synthesizer.playMelody(trackToPlay)
    }

    fun selectLessonById(lessonId: String) {
        val found = LessonsProvider.getLessonById(lessonId)
        if (found != null) {
            selectLesson(found)
        } else {
            LessonsProvider.lessons.firstOrNull()?.let { selectLesson(it) }
        }
    }

    fun startStoryAdventure(story: StoryAdventure) {
        _activeStory.value = story
        _storyChapterIndex.value = 0
        _currentScreen.value = "story_mode"
        synthesizer.playMelody(story.soundtrackTrackId)
    }

    fun completeStoryChapter() {
        val story = _activeStory.value ?: return
        val currentIdx = _storyChapterIndex.value
        if (currentIdx + 1 < story.chapters.size) {
            _storyChapterIndex.value = currentIdx + 1
            synthesizer.playSparkleChime()
            _celebrationEvent.value = CelebrationData(
                title = "✨ Chapter Step Completed!",
                subtitle = "You helped ${story.heroName} advance to Chapter ${currentIdx + 2}!",
                isFullLessonCompletion = false
            )
        } else {
            // Completed entire story adventure!
            viewModelScope.launch {
                repository.unlockStoryBadge()
                val result = repository.recordLessonCompletion("story_${story.id}", "all", practiceDurationMinutes = 6)
                val stickerId = "stk_story_${story.id}"
                repository.awardSticker(
                    stickerId = stickerId,
                    title = story.rewardStickerTitle,
                    description = story.rewardStickerDescription,
                    stage = "all"
                )

                synthesizer.stop()
                synthesizer.playCelebrationFanfare()

                val unlockedBadgeText = if (result.newlyUnlockedBadges.isNotEmpty()) {
                    "🎖️ Unlocked: " + result.newlyUnlockedBadges.joinToString(", ")
                } else "📖 Storybook Dancer Badge Unlocked!"

                _celebrationEvent.value = CelebrationData(
                    title = "🎉 Story Adventure Triumph!",
                    subtitle = "You completed '${story.title}'! Added '${story.rewardStickerTitle}' to your Passport! 🔥 Daily Streak: ${result.currentStreak} Days!",
                    badgeUnlockedText = unlockedBadgeText,
                    stickerTitle = story.rewardStickerTitle,
                    isFullLessonCompletion = true
                )
            }
        }
    }

    fun exitStory() {
        _activeStory.value = null
        _storyChapterIndex.value = 0
        _currentScreen.value = "map"
        _celebrationEvent.value = null
        synthesizer.stop()
    }

    fun exitLesson() {
        _activeLesson.value = null
        _currentScreen.value = "map"
        _celebrationEvent.value = null
        synthesizer.stop()
    }

    fun triggerMilestoneCelebration(milestoneTitle: String, milestoneSubtext: String) {
        synthesizer.playSparkleChime()
        _celebrationEvent.value = CelebrationData(
            title = milestoneTitle,
            subtitle = milestoneSubtext,
            isFullLessonCompletion = false
        )
    }

    fun completeActiveLesson() {
        val lesson = _activeLesson.value ?: return
        viewModelScope.launch {
            val result = repository.recordLessonCompletion(lesson.id, lesson.stage, practiceDurationMinutes = 4)
            
            // Save status to our new Room Entity table representing DanceLesson
            repository.updateDanceLessonEntityStatus(
                lessonId = lesson.id,
                lessonName = lesson.title,
                category = lesson.ageCategory,
                status = "Completed"
            )

            // Award corresponding sticker
            val stickerId = "stk_${lesson.id}"
            repository.awardSticker(
                stickerId = stickerId,
                title = lesson.stickerTitle,
                description = lesson.stickerDescription,
                stage = lesson.stage
            )

            synthesizer.stop()
            synthesizer.playCelebrationFanfare()

            val unlockedBadgeText = if (result.newlyUnlockedBadges.isNotEmpty()) {
                "🎖️ Unlocked: " + result.newlyUnlockedBadges.joinToString(", ")
            } else ""

            _celebrationEvent.value = CelebrationData(
                title = "🌟 Outstanding Dance, Star!",
                subtitle = "You completed '${lesson.title}' and added the '${lesson.stickerTitle}' sticker to your Passport! 🔥 Daily Streak: ${result.currentStreak} Days!",
                badgeUnlockedText = unlockedBadgeText,
                stickerTitle = lesson.stickerTitle,
                isFullLessonCompletion = true
            )
        }
    }

    fun dismissCelebrationAndGoHome() {
        val lastEvent = _celebrationEvent.value
        _celebrationEvent.value = null

        // Calculate if we should display Rating or Sharing Suggestion based on UX metrics
        val completedCount = completedLessons.value.size
        if (completedCount >= 2 && !isRateSuggested.value) {
            _showRateAppDialog.value = true
            setRateSuggested(true)
        } else if (completedCount >= 3 && !isShareSuggested.value) {
            _showShareAppDialog.value = true
            setShareSuggested(true)
        }

        if (_activeStory.value != null) {
            exitStory()
        } else {
            exitLesson()
        }
    }

    fun onMirrorToggled(isMirrored: Boolean) {
        if (isMirrored) {
            viewModelScope.launch {
                repository.unlockMirrorBadge()
            }
        }
    }

    fun selectMusicTrack(trackId: String) {
        viewModelScope.launch {
            val current = repository.getProfile()
            repository.updateProfile(current.copy(selectedMusicTrack = trackId))
            repository.unlockMusicBadge()
            synthesizer.setTrackAndPlay(trackId)
        }
    }

    fun updateDailyReminder(hour: Int, minute: Int, enabled: Boolean) {
        viewModelScope.launch {
            val current = repository.getProfile()
            repository.updateProfile(
                current.copy(
                    dailyReminderHour = hour,
                    dailyReminderMinute = minute,
                    dailyReminderEnabled = enabled
                )
            )
            val app = getApplication<Application>()
            if (enabled) {
                DanceNotificationScheduler.scheduleDailyReminder(app, hour, minute)
            } else {
                DanceNotificationScheduler.cancelReminder(app)
            }
        }
    }

    fun triggerTestNotification() {
        val app = getApplication<Application>()
        DanceNotificationScheduler.triggerTestNotification(app)
    }

    fun changeAgeGroup(ageGroup: String) {
        viewModelScope.launch {
            val current = repository.getProfile()
            repository.updateProfile(current.copy(ageGroup = ageGroup))
        }
    }

    fun updateDancerName(name: String) {
        viewModelScope.launch {
            val current = repository.getProfile()
            repository.updateProfile(current.copy(name = name))
        }
    }

    fun updateAvatar(outfit: String, hair: String, background: String) {
        viewModelScope.launch {
            val current = repository.getProfile()
            repository.updateProfile(
                current.copy(
                    avatarOutfit = outfit,
                    avatarHair = hair,
                    avatarBackground = background
                )
            )
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetProgress()
            _currentScreen.value = "map"
            synthesizer.stop()
        }
    }

    // Helper methods for user's progress state management
    val allDanceLessons: List<DanceLesson> get() = LessonsProvider.lessons

    fun isLessonCompleted(lessonId: String): Boolean {
        return completedLessonIds.value.contains(lessonId)
    }

    fun markLessonCompleted(
        lessonId: String,
        stage: String = "3-5",
        lessonTitle: String = "",
        accuracyScore: Float = 98f
    ) {
        val lesson = LessonsProvider.getLessonById(lessonId)
        val title = lessonTitle.ifEmpty { lesson?.title ?: lessonId.replace("_", " ").replaceFirstChar { it.uppercase() } }
        val targetStage = lesson?.stage ?: stage
        val durationMins = lesson?.durationMinutes ?: 4

        viewModelScope.launch {
            val result = repository.recordLessonCompletion(
                lessonId = lessonId,
                stage = targetStage,
                lessonTitle = title,
                sessionMode = _activeMode.value,
                practiceDurationMinutes = durationMins,
                accuracyScore = accuracyScore
            )
            
            // Save status to our new Room Entity table representing DanceLesson
            repository.updateDanceLessonEntityStatus(
                lessonId = lessonId,
                lessonName = title,
                category = lesson?.ageCategory ?: "All",
                status = "Completed"
            )
            lesson?.let {
                repository.awardSticker(
                    stickerId = "stk_${it.id}",
                    title = it.stickerTitle,
                    description = it.stickerDescription,
                    stage = it.stage
                )
            }
            synthesizer.playSparkleChime()
        }
    }

    fun markLessonAsCompleted(lessonId: String) {
        markLessonCompleted(lessonId)
    }

    fun markDanceLessonCompleted(danceLesson: DanceLesson) {
        markLessonCompleted(
            lessonId = danceLesson.id,
            stage = danceLesson.stage,
            lessonTitle = danceLesson.title
        )
    }

    fun selectDanceLesson(danceLesson: DanceLesson) {
        selectLesson(danceLesson)
    }

    fun getDanceLessonById(id: String): DanceLesson? {
        return LessonsProvider.getLessonById(id)
    }

    // Favorite dance moves handling
    fun toggleFavoriteMove(move: FavoriteDanceMove) {
        viewModelScope.launch {
            repository.toggleFavoriteMove(move)
            synthesizer.playSparkleChime()
        }
    }

    fun toggleFavoriteCurrentLesson() {
        val current = _activeLesson.value ?: return
        val move = FavoriteDanceMove(
            moveId = current.id,
            moveName = current.title,
            category = when (current.stage) {
                "3-5" -> "Toddler Story Movement"
                "6-8" -> "Ballet Basics & Technique"
                else -> "Junior Virtuoso Academy"
            },
            description = current.description,
            iconEmoji = current.emoji
        )
        toggleFavoriteMove(move)
    }

    fun isMoveFavorite(moveId: String): Boolean {
        return favoriteMoves.value.any { it.moveId == moveId }
    }

    // Voice command parser and execution for child navigation & studio interaction
    fun executeVoiceCommand(spokenText: String): String {
        val clean = spokenText.trim().lowercase()
        return when {
            clean.contains("map") || clean.contains("home") || clean.contains("world") -> {
                _currentScreen.value = "map"
                synthesizer.playSparkleChime()
                "Navigating to World Map 🗺️"
            }
            clean.contains("toddler") || clean.contains("story") || clean.contains("curriculum") -> {
                _currentScreen.value = "curriculum_graph"
                synthesizer.playSparkleChime()
                "Opening Storytelling & Curriculum Studio 🌸"
            }
            clean.contains("ballet") || clean.contains("basic") || clean.contains("library") || clean.contains("lesson") -> {
                _currentScreen.value = "library"
                synthesizer.playSparkleChime()
                "Opening Ballet Basics Library 🩰"
            }
            clean.contains("passport") || clean.contains("troph") || clean.contains("stat") || clean.contains("badge") || clean.contains("star") -> {
                _currentScreen.value = "passport"
                synthesizer.playSparkleChime()
                "Opening Dancer Passport & Stars 🌟"
            }
            clean.contains("avatar") || clean.contains("dress") || clean.contains("costume") || clean.contains("outfit") -> {
                _currentScreen.value = "avatar"
                synthesizer.playSparkleChime()
                "Opening Ballerina Dress Up Wardrobe 👗"
            }
            clean.contains("parent") || clean.contains("setting") -> {
                _currentScreen.value = "parent"
                synthesizer.playSparkleChime()
                "Opening Parent Dashboard 👨‍👩‍👧"
            }
            clean.contains("resume") || clean.contains("continue") || clean.contains("play") -> {
                resumeSavedSession()
                "Resuming your last dance session ⏯️"
            }
            clean.contains("favorite") || clean.contains("save") || clean.contains("heart") -> {
                if (_activeLesson.value != null) {
                    toggleFavoriteCurrentLesson()
                    "Toggled favorite on ${_activeLesson.value?.title}! ⭐"
                } else {
                    "Select a dance lesson first to mark it as favorite!"
                }
            }
            clean.contains("music") || clean.contains("song") || clean.contains("melody") -> {
                val profile = profileState.value
                val track = profile.selectedMusicTrack.ifEmpty { "swan_lake" }
                synthesizer.playMelody(track)
                "Playing classical melody 🎵"
            }
            clean.contains("celebrate") || clean.contains("cheer") || clean.contains("fanfare") -> {
                synthesizer.playCelebrationFanfare()
                _celebrationEvent.value = CelebrationData(
                    title = "🌟 Bravo Dancer! Voice Cheer! 🌟",
                    subtitle = "You are dancing wonderfully today!",
                    isFullLessonCompletion = false
                )
                "Cheering for your dance! 🏆"
            }
            else -> {
                "Heard: \"$spokenText\". Try: 'Map', 'Ballet', 'Story', 'Passport', 'Resume', 'Celebrate', or 'Favorite'!"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        synthesizer.stop()
        speechManager.shutdown()
    }
}

data class CelebrationData(
    val title: String,
    val subtitle: String,
    val badgeUnlockedText: String = "",
    val stickerTitle: String = "",
    val isFullLessonCompletion: Boolean = false
)

// Primary DanceLesson alias for seamless compatibility
typealias Lesson = DanceLesson

// Story Mode Models
data class StoryChapter(
    val chapterNumber: Int,
    val chapterTitle: String,
    val narrativeText: String,
    val characterEmoji: String,
    val danceActionPrompt: String,
    val movementInstruction: String,
    val visualClue: String
)

data class StoryAdventure(
    val id: String,
    val title: String,
    val subtitle: String,
    val heroName: String,
    val bannerEmoji: String,
    val soundtrackTrackId: String,
    val rewardStickerTitle: String,
    val rewardStickerDescription: String,
    val chapters: List<StoryChapter>
)

object StoryModeProvider {
    val stories = listOf(
        StoryAdventure(
            id = "swan_princess_rescue",
            title = "The Enchanted Swan Lake Quest",
            subtitle = "Awaken the crystal lake with classical ballet postures!",
            heroName = "Princess Odette",
            bannerEmoji = "🦢",
            soundtrackTrackId = "swan_lake",
            rewardStickerTitle = "Swan Lake Hero",
            rewardStickerDescription = "Awarded for completing the full 4-chapter narrative to rescue the magic enchanted swan lake.",
            chapters = listOf(
                StoryChapter(
                    chapterNumber = 1,
                    chapterTitle = "The Whispering Forest Trail",
                    narrativeText = "Odette steps quietly into the misty moonlit forest. To tiptoe past the sleeping woodland creatures without waking them, she must balance silently on the tips of her toes.",
                    characterEmoji = "🌲",
                    danceActionPrompt = "Tiptoe Relevé Rise",
                    movementInstruction = "Press onto the balls of your feet, lift your heels high, and take 5 soft, floating steps like a whisper in the wind.",
                    visualClue = "Tiptoe along the sparkling forest path on the floor!"
                ),
                StoryChapter(
                    chapterNumber = 2,
                    chapterTitle = "Spreading Feathery Wings",
                    narrativeText = "At the water's edge, a gentle breeze begins to blow. Odette must spread her graceful swan wings to catch the wind and glide across the crystal ripples.",
                    characterEmoji = "🦢",
                    danceActionPrompt = "Port de Bras (Wing Carriage)",
                    movementInstruction = "Raise your arms slowly from your sides into a curved 5th position overhead, then flutter your fingers gently down like falling feathers.",
                    visualClue = "Trace the sweeping wing arcs in the air!"
                ),
                StoryChapter(
                    chapterNumber = 3,
                    chapterTitle = "The Crystal Reflection Bow",
                    narrativeText = "The magic moon shines directly over the center of the lake! To break the nocturnal spell, Odette offers a royal, courtly bow of pure gratitude.",
                    characterEmoji = "💎",
                    danceActionPrompt = "Grand Plié & Reverence",
                    movementInstruction = "Cross your feet gracefully, bend both knees outward into a deep, gentle Plié, and bow your head with a proud, tall posture.",
                    visualClue = "Hold the royal bow steady for 3 musical beats!"
                ),
                StoryChapter(
                    chapterNumber = 4,
                    chapterTitle = "Flight into Freedom",
                    narrativeText = "The spell is broken! Golden light floods the lake as flocks of white swans take flight into the dawn sky in celebration!",
                    characterEmoji = "✨",
                    danceActionPrompt = "Grand Allegro Celebration Leap",
                    movementInstruction = "Take two bouncy steps and leap joyfully into the air with outstretched arms, landing as soft as cloud cotton!",
                    visualClue = "Leap over the golden morning rays!"
                )
            )
        ),
        StoryAdventure(
            id = "secret_garden_bloom",
            title = "The Secret Meadow Paint Quest",
            subtitle = "Restore color to the sleepy flowers with playful footwork!",
            heroName = "Flora the Garden Sprite",
            bannerEmoji = "🌸",
            soundtrackTrackId = "chopin_waltz",
            rewardStickerTitle = "Master Garden Bloomer",
            rewardStickerDescription = "Awarded for restoring vibrant rainbow colors to the sleepy flower kingdom.",
            chapters = listOf(
                StoryChapter(
                    chapterNumber = 1,
                    chapterTitle = "Waking the Sleepy Seedlings",
                    narrativeText = "The winter was long, and all the baby seeds are curled up tight under the soil. Flora curls down small to whisper a warm wake-up song.",
                    characterEmoji = "🌱",
                    danceActionPrompt = "Tiny Seed Plié Crouch",
                    movementInstruction = "Squat down small like a tiny acorn, tucking your elbows in. Then rise up slowly like a green sprout reaching for the sun!",
                    visualClue = "Crouch low, then stretch tall toward the sun!"
                ),
                StoryChapter(
                    chapterNumber = 2,
                    chapterTitle = "Mixing Rainbow Dew",
                    narrativeText = "Morning dew covers the petals, but they need bright colors! Flora uses her pointed ballet toes to stir and mix the rainbow dew drops.",
                    characterEmoji = "🎨",
                    danceActionPrompt = "Toe Pointing Tendu Swirls",
                    movementInstruction = "Point your right toe forward, glide it in a smooth semi-circle to the side (Rond de jambe), and tap back into first position.",
                    visualClue = "Draw circular rainbow swirls on the ground!"
                ),
                StoryChapter(
                    chapterNumber = 3,
                    chapterTitle = "The Butterfly Waltz",
                    narrativeText = "The colorful roses and tulips have blossomed! Swarms of playful monarch butterflies invite Flora into their sunny coronation dance.",
                    characterEmoji = "🦋",
                    danceActionPrompt = "Fluttering Changement Jumps",
                    movementInstruction = "Jump lightly 4 times in place, switching your front foot on each jump while fluttering your hands like butterfly wings!",
                    visualClue = "Land softly on each jump like a butterfly on a petal!"
                )
            )
        ),
        StoryAdventure(
            id = "cosmic_constellation_voyage",
            title = "The Cosmic Stardust Odyssey",
            subtitle = "Navigate the galaxy with strength, poise, and zero-gravity balance!",
            heroName = "Astro-Dancer Nova",
            bannerEmoji = "🚀",
            soundtrackTrackId = "moonlight_sonata",
            rewardStickerTitle = "Galactic Pathfinder",
            rewardStickerDescription = "Awarded for completing technical zero-gravity balance sequences across the starry cosmos.",
            chapters = listOf(
                StoryChapter(
                    chapterNumber = 1,
                    chapterTitle = "Zero-Gravity Space Barre",
                    narrativeText = "Nova floats into the Lunar Observation Dome. With microgravity drifting all around, holding the Space Barre is crucial for steady alignment.",
                    characterEmoji = "🛰️",
                    danceActionPrompt = "Adagio Leg Extension (Développé)",
                    movementInstruction = "Stand with straight spine, draw one toe up your standing knee (Passé), and extend your leg straight out in front. Hold for 3 seconds!",
                    visualClue = "Lock your core and hold your floating leg straight!"
                ),
                StoryChapter(
                    chapterNumber = 2,
                    chapterTitle = "Orbiting the Asteroid Belt",
                    narrativeText = "Floating stardust crystals are drifting past the spaceship window! Nova must pivot gracefully to dodge the gentle cosmic dust.",
                    characterEmoji = "🪐",
                    danceActionPrompt = "Pirouette Prep & Spotting Balance",
                    movementInstruction = "Stand on one leg in Relevé, bring your arms into first circle, and spot a single star on your screen without swaying.",
                    visualClue = "Keep your eyes locked on the glowing target star!"
                ),
                StoryChapter(
                    chapterNumber = 3,
                    chapterTitle = "Supernova Finale Leap",
                    narrativeText = "The galaxy is powered up! Nova unleashes a spectacular burst of starlight with an explosive grand allegro combination across the starry deck.",
                    characterEmoji = "⭐",
                    danceActionPrompt = "Grand Jeté Cosmic Leap",
                    movementInstruction = "Chassé forward with power, brush your lead leg high, and leap like a soaring shooting star across the cosmic horizon!",
                    visualClue = "Time your big leap to cross the planetary ring!"
                )
            )
        )
    )

    fun getStoryById(id: String): StoryAdventure {
        return stories.find { it.id == id } ?: stories[0]
    }
}

// Static Curated Ballet & Creative Movement Lessons
object LessonsProvider {
    val lessons: List<DanceLesson> = listOf(
        // Ages 3-5 (The Imagination Stage - Creative Movement)
        DanceLesson(
            id = "garden_toes",
            stage = "3-5",
            title = "Paint the Floor with Your Toes",
            ageCategory = "Ages 3–5",
            description = "Point your toes and paint colorful garden rainbows across the studio floor with your magical feet.",
            thumbnailUrl = "https://images.unsplash.com/photo-1518834107812-67b0b7c58434?w=600&auto=format&fit=crop&q=80",
            storyTitle = "The Magic Garden Paint Party",
            storyContent = "Today we are visiting a magical secret garden! But wait—there's no paint! To paint beautiful flowers and trees, you must use your magical toes as a paintbrush.",
            actionGuide = "Point your toes and extend your leg out gently. Glide your toes back and forth across the floor, painting wonderful rainbows!",
            visualPrompt = "Drag your toes along the curved lines on the floor to mix the magical colors!",
            stickerTitle = "Rainbow Painter",
            stickerDescription = "Awarded for painting beautiful flowers using perfectly pointed toes!",
            durationMinutes = 3,
            emoji = "🌸",
            musicTrack = "chopin_waltz",
            difficulty = "Beginner"
        ),
        DanceLesson(
            id = "garden_mouse",
            stage = "3-5",
            title = "Hide Like a Mouse (Plié)",
            ageCategory = "Ages 3–5",
            description = "Learn gentle pliés by bending your knees outward to hide quietly like a woodland mouse from the wise owl.",
            thumbnailUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            storyTitle = "The Wise Owl and the Little Mouse",
            storyContent = "Shh... Look up! A friendly, wise old owl is flying overhead searching for dancers. To stay safe and playful, we need to hide very quietly like tiny forest mice!",
            actionGuide = "With your heels together, bend your knees outward slowly to drop down like a little mouse hiding in the grass. Then, stand up tall as the owl flies away!",
            visualPrompt = "Keep your back straight and heels touching as you bend down!",
            stickerTitle = "Quiet Little Mouse",
            stickerDescription = "Awarded for completing quiet, elegant knee bends (Pliés) to hide from the wise owl.",
            durationMinutes = 4,
            emoji = "🐭",
            musicTrack = "chopin_waltz",
            difficulty = "Beginner"
        ),
        DanceLesson(
            id = "garden_butterfly",
            stage = "3-5",
            title = "Butterfly Jumps",
            ageCategory = "Ages 3–5",
            description = "Spring up high into the sunny sky with fluttery butterfly wings and land as softly as a cloud feather.",
            thumbnailUrl = "https://images.unsplash.com/photo-1547153760-18fc86324498?w=600&auto=format&fit=crop&q=80",
            storyTitle = "The Butterfly Ballet Festival",
            storyContent = "It's time for the grand festival! All the butterflies are waking up and taking off into the bright blue sky. Spread your wings and leap with joy!",
            actionGuide = "Stand tall, bend your knees, and spring high into the air! Flutter your hands gently like wings. Land softly like a feather.",
            visualPrompt = "Spring upward from the ground, pointing your toes in the air!",
            stickerTitle = "Sky Butterfly",
            stickerDescription = "Awarded for soft, bouncy jumps (Changement) and fluttery wing movements.",
            durationMinutes = 4,
            emoji = "🦋",
            musicTrack = "chopin_waltz",
            difficulty = "Beginner"
        ),

        // Ages 6-8 (The Foundation Stage - Ballet Basics)
        DanceLesson(
            id = "castle_pli",
            stage = "6-8",
            title = "Plié Practice (1st & 2nd Position)",
            ageCategory = "Ages 6–8",
            description = "Master classical 1st and 2nd position knee bends with royal posture and balance.",
            thumbnailUrl = "https://images.unsplash.com/photo-1516307365426-bea591f05011?w=600&auto=format&fit=crop&q=80",
            storyTitle = "The Royal Ball Welcome",
            storyContent = "Welcome to the Royal Palace Ballroom! To greet the guests, we must practice our formal courtly bows. This starts with a beautiful, controlled Plié.",
            actionGuide = "Place your heels together with toes pointing out (1st Position) and bend your knees outwards. Try again with your feet spaced wide (2nd Position). Ensure your spine stays perfectly tall like a castle pillar!",
            visualPrompt = "Step your feet into the on-screen footprints. Stay balanced!",
            stickerTitle = "Royal Courtier",
            stickerDescription = "Awarded for mastering proper turnout and straight posture in 1st and 2nd position Pliés.",
            durationMinutes = 5,
            emoji = "👑",
            musicTrack = "swan_lake",
            difficulty = "Intermediate"
        ),
        DanceLesson(
            id = "castle_tendu",
            stage = "6-8",
            title = "Tendu & Foot Alignment",
            ageCategory = "Ages 6–8",
            description = "Slide and point through the arches with straight knee alignment to polish the mirror ballroom.",
            thumbnailUrl = "https://images.unsplash.com/photo-1508807526345-15e9b5f4eaff?w=600&auto=format&fit=crop&q=80",
            storyTitle = "Polishing the Mirror Ballroom",
            storyContent = "The Grand Ballroom floor needs to be absolutely flawless for tonight's dance. Let's help polish the mirror-like floors with our ballet slippers!",
            actionGuide = "Slide one foot straight out, stretching from your arch and pointing your toes until only the tip touches the floor. Do not lift your foot! Slide it back with control.",
            visualPrompt = "Align your toes with the gold alignment lines shown on the screen.",
            stickerTitle = "Mirror Polisher",
            stickerDescription = "Awarded for flawless, straight leg extension and proper ankle stretch (Tendu).",
            durationMinutes = 5,
            emoji = "✨",
            musicTrack = "swan_lake",
            difficulty = "Intermediate"
        ),
        DanceLesson(
            id = "castle_releve",
            stage = "6-8",
            title = "Relevé & Rise (Finding Center)",
            ageCategory = "Ages 6–8",
            description = "Rise onto the balls of your feet with poise, balance, and rounded port de bras arm carriage.",
            thumbnailUrl = "https://images.unsplash.com/photo-1524594152303-9fd13543fe6e?w=600&auto=format&fit=crop&q=80",
            storyTitle = "Reaching for the Castle Chandelier",
            storyContent = "Look at the twinkling crystal chandeliers high above the castle! To make them glow brighter, we must rise up and reach towards the sky.",
            actionGuide = "From 1st position, press down into the floor and rise onto the balls of your feet. Hold your arms in a beautiful circle in front of you. Balance!",
            visualPrompt = "Focus your eyes on a single spot on the screen to stay perfectly steady.",
            stickerTitle = "Crystal Balancer",
            stickerDescription = "Awarded for rising with strength and maintaining perfect balance in Relevé.",
            durationMinutes = 5,
            emoji = "💎",
            musicTrack = "swan_lake",
            difficulty = "Intermediate"
        ),

        // Ages 9-12 (The Technique Stage - Junior Academy)
        DanceLesson(
            id = "moon_barre",
            stage = "9-12",
            title = "Barre Workout: Plié & Battement",
            ageCategory = "Ages 9–12",
            description = "Technical barre exercises featuring controlled demi-pliés and high-kick battements in lunar gravity.",
            thumbnailUrl = "https://images.unsplash.com/photo-1518834107812-67b0b7c58434?w=600&auto=format&fit=crop&q=80",
            storyTitle = "Defying Gravity at Lunar Station",
            storyContent = "Prepare for zero-gravity training! Today, we use the Space Barre to build muscle strength and control. Holding our support keeps us centered in the low gravity environment.",
            actionGuide = "Place your hand on a chair or wall for support. Perform 2 slow demi-pliés, followed by a controlled high kick (Grand Battement) to the front, side, and back. Point your toes with explosive power!",
            visualPrompt = "Activate the Pose Mirror to trace your back alignment. Keep your support light!",
            stickerTitle = "Gravity Defier",
            stickerDescription = "Completed technical barre workouts, demonstrating incredible control and high leg extension.",
            durationMinutes = 6,
            emoji = "🚀",
            musicTrack = "bach_minuet",
            difficulty = "Advanced"
        ),
        DanceLesson(
            id = "moon_adagio",
            stage = "9-12",
            title = "Adagio Flow & Extension",
            ageCategory = "Ages 9–12",
            description = "Continuous, fluid slow-motion leg extensions and expressive port de bras arm movements.",
            thumbnailUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            storyTitle = "Dancing in Slow Motion Orbit",
            storyContent = "We are drifting through the calm stars in slow motion. Here, every movement must be incredibly fluid, continuous, and controlled.",
            actionGuide = "Slowly lift your leg to the side (Développé) and hold for 3 seconds. Keep your arms in a graceful overhead curve. Move seamlessly like planetary dust.",
            visualPrompt = "Align your leg with the expanding silver orbital circle to optimize balance.",
            stickerTitle = "Cosmic Orbit",
            stickerDescription = "Awarded for exceptional leg control, slow-motion balance, and graceful carriage of arms (Port de bras).",
            durationMinutes = 6,
            emoji = "🪐",
            musicTrack = "bach_minuet",
            difficulty = "Advanced"
        ),
        DanceLesson(
            id = "moon_allegro",
            stage = "9-12",
            title = "Grand Allegro Leap sequence",
            ageCategory = "Ages 9–12",
            description = "Explosive Grand Jeté ballet leap combinations flying across starry planetary rings.",
            thumbnailUrl = "https://images.unsplash.com/photo-1547153760-18fc86324498?w=600&auto=format&fit=crop&q=80",
            storyTitle = "Leaping Over Moon Craters",
            storyContent = "Look out! The crater fields are ahead. We must cross them using grand, powerful ballet leaps to travel gracefully across the lunar plains.",
            actionGuide = "Take a step (Chassé), brush your leg forward, and push off into a grand leap (Grand Jeté), splitting your legs wide in the air before landing softly. Repeat the sequence!",
            visualPrompt = "Time your leap perfectly as you cross the shining target rings!",
            stickerTitle = "Lunar Leaper",
            stickerDescription = "Awarded for sequence memory and demonstrating dynamic power in Grand Allegro leaps.",
            durationMinutes = 6,
            emoji = "⭐",
            musicTrack = "bach_minuet",
            difficulty = "Advanced"
        )
    )

    fun getLessonsForStage(stage: String): List<DanceLesson> {
        return lessons.filter { it.stage == stage }
    }

    fun getLessonById(id: String): DanceLesson? {
        return lessons.find { it.id == id }
    }
}

class DanceViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DanceViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
