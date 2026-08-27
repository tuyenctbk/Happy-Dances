package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DanceRepository(private val database: AppDatabase) {

    private val profileDao = database.dancerProfileDao()
    private val lessonDao = database.completedLessonDao()
    private val stickerDao = database.earnedStickerDao()
    private val streakDao = database.danceStreakDao()
    private val badgeDao = database.badgeAchievementDao()
    private val progressDao = database.progressDao()
    private val milestoneDao = database.studentMilestoneDao()
    private val favoriteMoveDao = database.favoriteDanceMoveDao()
    private val dailyChallengeDao = database.dailyChallengeDao()
    private val danceLessonEntityDao = database.danceLessonEntityDao()
    private val favoriteDanceLessonDao = database.favoriteDanceLessonDao()
    private val recentlyViewedDao = database.recentlyViewedLessonDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val recentlyViewedLessonsFlow: Flow<List<RecentlyViewedLesson>> = recentlyViewedDao.getRecentlyViewedLessonsFlow()
        .distinctUntilChanged()

    suspend fun recordRecentlyViewed(lesson: DanceLesson) {
        val record = RecentlyViewedLesson(
            lessonId = lesson.id,
            title = lesson.title,
            ageCategory = lesson.ageCategory,
            stage = lesson.stage,
            thumbnailUrl = lesson.thumbnailUrl,
            durationMinutes = lesson.durationMinutes,
            emoji = lesson.emoji,
            viewedAt = System.currentTimeMillis()
        )
        recentlyViewedDao.insertRecentlyViewed(record)
    }


    val allFavoriteDanceLessonsFlow: Flow<List<FavoriteDanceLesson>> = favoriteDanceLessonDao.getAllFavoriteLessonsFlow()
        .distinctUntilChanged()

    suspend fun toggleFavoriteDanceLesson(lesson: DanceLesson): Boolean {
        val isFav = favoriteDanceLessonDao.isLessonFavorite(lesson.id)
        if (isFav) {
            favoriteDanceLessonDao.deleteFavoriteLesson(lesson.id)
            return false
        } else {
            val fav = FavoriteDanceLesson(
                lessonId = lesson.id,
                title = lesson.title,
                ageCategory = lesson.ageCategory,
                difficulty = lesson.difficulty,
                thumbnailUrl = lesson.thumbnailUrl,
                durationMinutes = lesson.durationMinutes,
                emoji = lesson.emoji,
                addedAt = System.currentTimeMillis()
            )
            favoriteDanceLessonDao.insertFavoriteLesson(fav)
            return true
        }
    }

    suspend fun isDanceLessonFavorite(lessonId: String): Boolean {
        return favoriteDanceLessonDao.isLessonFavorite(lessonId)
    }

    fun isDanceLessonFavoriteFlow(lessonId: String): Flow<Boolean> {
        return favoriteDanceLessonDao.isLessonFavoriteFlow(lessonId).distinctUntilChanged()
    }

    suspend fun deleteFavoriteDanceLesson(lessonId: String) {
        favoriteDanceLessonDao.deleteFavoriteLesson(lessonId)
    }

    val allDanceLessonEntitiesFlow: Flow<List<DanceLessonEntity>> = danceLessonEntityDao.getAllLessonsFlow()
        .distinctUntilChanged()

    suspend fun updateDanceLessonEntityStatus(lessonId: String, lessonName: String, category: String, status: String) {
        val record = DanceLessonEntity(
            id = lessonId,
            lessonName = lessonName,
            category = category,
            completionStatus = status,
            timestamp = System.currentTimeMillis()
        )
        danceLessonEntityDao.insertOrUpdateLesson(record)
    }

    val favoriteMovesFlow: Flow<List<FavoriteDanceMove>> = favoriteMoveDao.getAllFavoriteMovesFlow()
        .distinctUntilChanged()

    val completedDailyChallengesFlow: Flow<List<DailyChallengeRecord>> = dailyChallengeDao.getAllCompletedChallengesFlow()
        .distinctUntilChanged()

    fun getTodayDailyChallengeRecordFlow(): Flow<DailyChallengeRecord?> {
        val todayStr = dateFormat.format(Date())
        return dailyChallengeDao.getChallengeByDateFlow(todayStr).distinctUntilChanged()
    }

    suspend fun getTodayDailyChallengeRecord(): DailyChallengeRecord? {
        val todayStr = dateFormat.format(Date())
        return dailyChallengeDao.getChallengeByDate(todayStr)
    }

    suspend fun recordDailyChallengeCompleted(challenge: DailyDanceChallenge): CompletedResult {
        val todayStr = dateFormat.format(Date())
        val record = DailyChallengeRecord(
            date = todayStr,
            challengeId = challenge.id,
            title = challenge.title,
            moveName = challenge.moveName,
            description = challenge.description,
            starsAwarded = 5,
            completedAt = System.currentTimeMillis()
        )
        dailyChallengeDao.insertChallengeRecord(record)

        // Also record as a dance lesson completion with streak & badge evaluation
        return recordLessonCompletion(
            lessonId = "daily_${challenge.id}",
            stage = challenge.stage,
            lessonTitle = "Daily Move: ${challenge.moveName}",
            sessionMode = "daily_challenge",
            practiceDurationMinutes = 2,
            accuracyScore = 99f
        )
    }

    val allMilestonesFlow: Flow<List<StudentMilestone>> = milestoneDao.getAllMilestonesFlow()
        .distinctUntilChanged()

    val profileFlow: Flow<DancerProfile> = profileDao.getProfileFlow()
        .map { it ?: DancerProfile() }
        .distinctUntilChanged()

    val completedLessonsFlow: Flow<List<CompletedLesson>> = lessonDao.getCompletedLessonsFlow()
        .distinctUntilChanged()

    val earnedStickersFlow: Flow<List<EarnedSticker>> = stickerDao.getEarnedStickersFlow()
        .distinctUntilChanged()

    val streakFlow: Flow<DanceStreakInfo> = streakDao.getStreakFlow()
        .map { it ?: DanceStreakInfo() }
        .distinctUntilChanged()

    val dailySessionsFlow: Flow<List<DailyDanceSession>> = streakDao.getAllSessionsFlow()
        .distinctUntilChanged()

    val allBadgesFlow: Flow<List<BadgeAchievement>> = badgeDao.getAllBadgesFlow()
        .distinctUntilChanged()

    val unlockedBadgesFlow: Flow<List<BadgeAchievement>> = badgeDao.getUnlockedBadgesFlow()
        .distinctUntilChanged()

    val allProgressFlow: Flow<List<Progress>> = progressDao.getAllProgressFlow()
        .distinctUntilChanged()

    suspend fun initializeDefaultBadgesIfNeeded() {
        val defaultBadges = listOf(
            BadgeAchievement(
                badgeId = "badge_first_step",
                title = "First Dance Step",
                description = "Completed your very first dance lesson in Happy Dances!",
                iconEmoji = "🩰",
                category = "milestone"
            ),
            BadgeAchievement(
                badgeId = "badge_lessons_3",
                title = "Rising Star Dancer",
                description = "Marked 3 lessons as completed in your dance journal!",
                iconEmoji = "🌟",
                category = "milestone"
            ),
            BadgeAchievement(
                badgeId = "badge_lessons_5",
                title = "Ballet Master",
                description = "Completed 5 dance lessons across the stages!",
                iconEmoji = "👑",
                category = "milestone"
            ),
            BadgeAchievement(
                badgeId = "badge_streak_3",
                title = "3-Day Dance Hero",
                description = "Practiced dance for 3 consecutive days in a row!",
                iconEmoji = "🔥",
                category = "streak"
            ),
            BadgeAchievement(
                badgeId = "badge_streak_7",
                title = "7-Day Star Ballerina",
                description = "Achieved an incredible full 7-day daily dance streak!",
                iconEmoji = "⭐",
                category = "streak"
            ),
            BadgeAchievement(
                badgeId = "badge_stage_garden",
                title = "Garden Sprite",
                description = "Mastered all creative movement lessons in the Imagination Garden!",
                iconEmoji = "🌸",
                category = "stage"
            ),
            BadgeAchievement(
                badgeId = "badge_stage_castle",
                title = "Castle Royalty",
                description = "Completed all foundation ballet lessons in the Royal Ballroom!",
                iconEmoji = "👑",
                category = "stage"
            ),
            BadgeAchievement(
                badgeId = "badge_stage_moon",
                title = "Cosmic Voyager",
                description = "Completed all advanced technique lessons at Lunar Space Station!",
                iconEmoji = "🚀",
                category = "stage"
            ),
            BadgeAchievement(
                badgeId = "badge_mirror_master",
                title = "Mirror Perfectionist",
                description = "Practiced dancing with the camera mirror view mode activated!",
                iconEmoji = "🪞",
                category = "mastery"
            ),
            BadgeAchievement(
                badgeId = "badge_story_teller",
                title = "Storybook Dancer",
                description = "Completed an interactive multi-chapter Story Mode narrative adventure!",
                iconEmoji = "📖",
                category = "milestone"
            ),
            BadgeAchievement(
                badgeId = "badge_music_maestro",
                title = "Music Maestro",
                description = "Customized background classical melodies in the studio music player!",
                iconEmoji = "🎵",
                category = "mastery"
            ),
            BadgeAchievement(
                badgeId = "badge_sticker_collector",
                title = "Master Passport Holder",
                description = "Collected 5 or more digital stickers in your Passport book!",
                iconEmoji = "✨",
                category = "milestone"
            )
        )
        badgeDao.insertBadges(defaultBadges)

        val defaultMilestones = listOf(
            StudentMilestone(
                milestoneId = "ms_toddler_hop",
                title = "Toddler Creative Hop",
                description = "Complete 3 creative movement story lessons",
                category = "toddler_storytelling",
                starsReward = 5,
                targetCount = 3
            ),
            StudentMilestone(
                milestoneId = "ms_ballet_basics",
                title = "Ballet Foundation Honor",
                description = "Complete 3 foundation technique lessons",
                category = "ballet_basics",
                starsReward = 5,
                targetCount = 3
            ),
            StudentMilestone(
                milestoneId = "ms_academy_virtuoso",
                title = "Academy Virtuoso",
                description = "Complete 3 junior technical virtuoso lessons",
                category = "academy_training",
                starsReward = 10,
                targetCount = 3
            )
        )
        milestoneDao.insertMilestones(defaultMilestones)
    }

    suspend fun updateStudentMilestone(milestoneId: String, currentCount: Int) {
        milestoneDao.updateMilestoneProgress(milestoneId, currentCount)
    }

    suspend fun toggleFavoriteMove(move: FavoriteDanceMove) {
        if (favoriteMoveDao.isMoveFavorite(move.moveId)) {
            favoriteMoveDao.deleteFavoriteMove(move.moveId)
        } else {
            favoriteMoveDao.insertFavoriteMove(move)
        }
    }

    suspend fun isMoveFavorite(moveId: String): Boolean {
        return favoriteMoveDao.isMoveFavorite(moveId)
    }

    suspend fun getProfile(): DancerProfile {
        return profileDao.getProfile() ?: DancerProfile()
    }

    suspend fun updateProfile(profile: DancerProfile) {
        profileDao.insertOrUpdateProfile(profile)
    }

    suspend fun recordLessonCompletion(
        lessonId: String,
        stage: String,
        lessonTitle: String = "",
        sessionMode: String = "academy",
        practiceDurationMinutes: Int = 4,
        accuracyScore: Float = 98f
    ): CompletedResult {
        // 1. Record completed lesson
        lessonDao.insertCompletedLesson(CompletedLesson(lessonId = lessonId, stage = stage))

        // 1b. Record in Progress entity table
        progressDao.insertProgress(
            Progress(
                lessonId = lessonId,
                lessonTitle = lessonTitle.ifEmpty { lessonId.replace("_", " ").replaceFirstChar { it.uppercase() } },
                danceLevel = stage,
                sessionMode = sessionMode,
                isCompleted = true,
                completionStatus = "completed",
                repetitionsCount = 1,
                durationSeconds = practiceDurationMinutes * 60,
                accuracyScore = accuracyScore,
                timestamp = System.currentTimeMillis()
            )
        )

        // 2. Update profile session count & practice minutes
        val profile = getProfile()
        val updatedProfile = profile.copy(
            totalSessionsCount = profile.totalSessionsCount + 1,
            totalPracticeMinutes = profile.totalPracticeMinutes + practiceDurationMinutes
        )
        profileDao.insertOrUpdateProfile(updatedProfile)

        // 3. Update Daily Dance Session & Calculate Daily Streak
        val todayStr = dateFormat.format(Date())
        val existingSession = streakDao.getSessionByDate(todayStr)
        if (existingSession != null) {
            streakDao.insertOrUpdateSession(
                existingSession.copy(
                    sessionCount = existingSession.sessionCount + 1,
                    totalMinutes = existingSession.totalMinutes + practiceDurationMinutes,
                    lastCompletedTimestamp = System.currentTimeMillis()
                )
            )
        } else {
            streakDao.insertOrUpdateSession(
                DailyDanceSession(
                    date = todayStr,
                    sessionCount = 1,
                    totalMinutes = practiceDurationMinutes,
                    lastCompletedTimestamp = System.currentTimeMillis()
                )
            )
        }

        // Streak calculation
        val currentStreakInfo = streakDao.getStreak() ?: DanceStreakInfo()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(calendar.time)

        var newStreak = currentStreakInfo.currentStreak
        val lastActive = currentStreakInfo.lastActiveDate

        if (lastActive == todayStr) {
            // Already counted today
            if (newStreak == 0) newStreak = 1
        } else if (lastActive == yesterdayStr) {
            // Consecutive day
            newStreak += 1
        } else {
            // Streak reset or started
            newStreak = 1
        }

        val bestStreak = maxOf(newStreak, currentStreakInfo.bestStreak)
        val updatedStreakInfo = DanceStreakInfo(
            id = 1,
            currentStreak = newStreak,
            bestStreak = bestStreak,
            lastActiveDate = todayStr
        )
        streakDao.insertOrUpdateStreak(updatedStreakInfo)

        // 4. Check and unlock achievements
        val unlockedBadgesList = mutableListOf<String>()

        // Check total completed lessons count thresholds
        val allCompleted = lessonDao.getAllCompletedLessons().map { it.lessonId }.toSet()
        val totalCompletedCount = allCompleted.size

        if (totalCompletedCount >= 1) {
            badgeDao.unlockBadge("badge_first_step")
            unlockedBadgesList.add("First Dance Step")
        }
        if (totalCompletedCount >= 3) {
            badgeDao.unlockBadge("badge_lessons_3")
            unlockedBadgesList.add("Rising Star Dancer")
        }
        if (totalCompletedCount >= 5) {
            badgeDao.unlockBadge("badge_lessons_5")
            unlockedBadgesList.add("Ballet Master")
        }

        // Streak badges
        if (newStreak >= 3) {
            badgeDao.unlockBadge("badge_streak_3")
            unlockedBadgesList.add("3-Day Dance Hero")
        }
        if (newStreak >= 7) {
            badgeDao.unlockBadge("badge_streak_7")
            unlockedBadgesList.add("7-Day Star Ballerina")
        }

        // Check stage completions
        if (listOf("garden_toes", "garden_mouse", "garden_butterfly").all { it in allCompleted }) {
            badgeDao.unlockBadge("badge_stage_garden")
            unlockedBadgesList.add("Garden Sprite")
        }
        if (listOf("castle_pli", "castle_tendu", "castle_releve").all { it in allCompleted }) {
            badgeDao.unlockBadge("badge_stage_castle")
            unlockedBadgesList.add("Castle Royalty")
        }
        if (listOf("moon_barre", "moon_adagio", "moon_allegro").all { it in allCompleted }) {
            badgeDao.unlockBadge("badge_stage_moon")
            unlockedBadgesList.add("Cosmic Voyager")
        }

        // Check sticker collection badge
        val totalStickers = stickerDao.getAllEarnedStickers().size
        if (totalStickers >= 5) {
            badgeDao.unlockBadge("badge_sticker_collector")
            unlockedBadgesList.add("Master Passport Holder")
        }

        return CompletedResult(
            currentStreak = newStreak,
            isNewDay = lastActive != todayStr,
            newlyUnlockedBadges = unlockedBadgesList
        )
    }

    suspend fun awardSticker(stickerId: String, title: String, description: String, stage: String) {
        stickerDao.insertSticker(
            EarnedSticker(
                stickerId = stickerId,
                title = title,
                description = description,
                stage = stage
            )
        )
    }

    suspend fun recordProgress(progress: Progress): Long {
        return progressDao.insertProgress(progress)
    }

    suspend fun unlockMirrorBadge() {
        badgeDao.unlockBadge("badge_mirror_master")
    }

    suspend fun unlockStoryBadge() {
        badgeDao.unlockBadge("badge_story_teller")
    }

    suspend fun unlockMusicBadge() {
        badgeDao.unlockBadge("badge_music_maestro")
    }

    suspend fun resetProgress() {
        lessonDao.clearCompletedLessons()
        stickerDao.clearStickers()
        streakDao.clearSessions()
        streakDao.clearStreak()
        badgeDao.clearBadges()
        progressDao.clearProgress()
        danceLessonEntityDao.clearLessons()
        profileDao.insertOrUpdateProfile(DancerProfile())
        initializeDefaultBadgesIfNeeded()
    }
}

data class CompletedResult(
    val currentStreak: Int,
    val isNewDay: Boolean,
    val newlyUnlockedBadges: List<String>
)
