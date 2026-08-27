package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DancerProfileDao {
    @Query("SELECT * FROM dancer_profile WHERE id = 1 LIMIT 1")
    fun getProfileFlow(): Flow<DancerProfile?>

    @Query("SELECT * FROM dancer_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): DancerProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: DancerProfile)
}

@Dao
interface CompletedLessonDao {
    @Query("SELECT * FROM completed_lessons ORDER BY completedAt DESC")
    fun getCompletedLessonsFlow(): Flow<List<CompletedLesson>>

    @Query("SELECT * FROM completed_lessons")
    suspend fun getAllCompletedLessons(): List<CompletedLesson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedLesson(lesson: CompletedLesson)

    @Query("DELETE FROM completed_lessons")
    suspend fun clearCompletedLessons()
}

@Dao
interface EarnedStickerDao {
    @Query("SELECT * FROM earned_stickers ORDER BY earnedAt DESC")
    fun getEarnedStickersFlow(): Flow<List<EarnedSticker>>

    @Query("SELECT * FROM earned_stickers")
    suspend fun getAllEarnedStickers(): List<EarnedSticker>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSticker(sticker: EarnedSticker)

    @Query("DELETE FROM earned_stickers")
    suspend fun clearStickers()
}

@Dao
interface DanceStreakDao {
    @Query("SELECT * FROM dance_streak_info WHERE id = 1 LIMIT 1")
    fun getStreakFlow(): Flow<DanceStreakInfo?>

    @Query("SELECT * FROM dance_streak_info WHERE id = 1 LIMIT 1")
    suspend fun getStreak(): DanceStreakInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streakInfo: DanceStreakInfo)

    @Query("SELECT * FROM daily_dance_sessions ORDER BY date DESC")
    fun getAllSessionsFlow(): Flow<List<DailyDanceSession>>

    @Query("SELECT * FROM daily_dance_sessions WHERE date = :date LIMIT 1")
    suspend fun getSessionByDate(date: String): DailyDanceSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSession(session: DailyDanceSession)

    @Query("DELETE FROM daily_dance_sessions")
    suspend fun clearSessions()

    @Query("DELETE FROM dance_streak_info")
    suspend fun clearStreak()
}

@Dao
interface BadgeAchievementDao {
    @Query("SELECT * FROM badge_achievements ORDER BY isUnlocked DESC, badgeId ASC")
    fun getAllBadgesFlow(): Flow<List<BadgeAchievement>>

    @Query("SELECT * FROM badge_achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedBadgesFlow(): Flow<List<BadgeAchievement>>

    @Query("SELECT * FROM badge_achievements")
    suspend fun getAllBadges(): List<BadgeAchievement>

    @Query("SELECT * FROM badge_achievements WHERE badgeId = :badgeId LIMIT 1")
    suspend fun getBadgeById(badgeId: String): BadgeAchievement?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadges(badges: List<BadgeAchievement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBadge(badge: BadgeAchievement)

    @Query("UPDATE badge_achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE badgeId = :badgeId AND isUnlocked = 0")
    suspend fun unlockBadge(badgeId: String, unlockedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM badge_achievements")
    suspend fun clearBadges()
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress ORDER BY timestamp DESC")
    fun getAllProgressFlow(): Flow<List<Progress>>

    @Query("SELECT * FROM progress WHERE lessonId = :lessonId ORDER BY timestamp DESC")
    fun getProgressForLessonFlow(lessonId: String): Flow<List<Progress>>

    @Query("SELECT * FROM progress WHERE danceLevel = :danceLevel ORDER BY timestamp DESC")
    fun getProgressForLevelFlow(danceLevel: String): Flow<List<Progress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: Progress): Long

    @Query("SELECT * FROM progress")
    suspend fun getAllProgress(): List<Progress>

    @Query("SELECT COUNT(*) FROM progress WHERE isCompleted = 1")
    suspend fun getCompletedCount(): Int

    @Query("DELETE FROM progress")
    suspend fun clearProgress()
}

@Dao
interface StudentMilestoneDao {
    @Query("SELECT * FROM student_milestones ORDER BY milestoneId ASC")
    fun getAllMilestonesFlow(): Flow<List<StudentMilestone>>

    @Query("SELECT * FROM student_milestones WHERE category = :category")
    fun getMilestonesByCategoryFlow(category: String): Flow<List<StudentMilestone>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMilestones(milestones: List<StudentMilestone>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMilestone(milestone: StudentMilestone)

    @Query("UPDATE student_milestones SET currentCount = :currentCount, isAchieved = CASE WHEN :currentCount >= targetCount THEN 1 ELSE isAchieved END, achievedAt = CASE WHEN :currentCount >= targetCount AND isAchieved = 0 THEN :now ELSE achievedAt END WHERE milestoneId = :milestoneId")
    suspend fun updateMilestoneProgress(milestoneId: String, currentCount: Int, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM student_milestones")
    suspend fun clearMilestones()
}

@Dao
interface FavoriteDanceMoveDao {
    @Query("SELECT * FROM favorite_dance_moves ORDER BY timestamp DESC")
    fun getAllFavoriteMovesFlow(): Flow<List<FavoriteDanceMove>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMove(move: FavoriteDanceMove)

    @Query("DELETE FROM favorite_dance_moves WHERE moveId = :moveId")
    suspend fun deleteFavoriteMove(moveId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_dance_moves WHERE moveId = :moveId)")
    suspend fun isMoveFavorite(moveId: String): Boolean
}

@Dao
interface DailyChallengeDao {
    @Query("SELECT * FROM daily_challenges ORDER BY date DESC")
    fun getAllCompletedChallengesFlow(): Flow<List<DailyChallengeRecord>>

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    fun getChallengeByDateFlow(date: String): Flow<DailyChallengeRecord?>

    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    suspend fun getChallengeByDate(date: String): DailyChallengeRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallengeRecord(record: DailyChallengeRecord)

    @Query("SELECT COUNT(*) FROM daily_challenges")
    suspend fun getCompletedChallengesCount(): Int
}

@Dao
interface DanceLessonEntityDao {
    @Query("SELECT * FROM dance_lesson_entities ORDER BY timestamp DESC")
    fun getAllLessonsFlow(): Flow<List<DanceLessonEntity>>

    @Query("SELECT * FROM dance_lesson_entities WHERE id = :id LIMIT 1")
    suspend fun getLessonById(id: String): DanceLessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLesson(lesson: DanceLessonEntity)

    @Query("DELETE FROM dance_lesson_entities")
    suspend fun clearLessons()
}

@Dao
interface FavoriteDanceLessonDao {
    @Query("SELECT * FROM favorite_dance_lessons ORDER BY addedAt DESC")
    fun getAllFavoriteLessonsFlow(): Flow<List<FavoriteDanceLesson>>

    @Query("SELECT * FROM favorite_dance_lessons WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getFavoriteLessonById(lessonId: String): FavoriteDanceLesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLesson(favorite: FavoriteDanceLesson)

    @Query("DELETE FROM favorite_dance_lessons WHERE lessonId = :lessonId")
    suspend fun deleteFavoriteLesson(lessonId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_dance_lessons WHERE lessonId = :lessonId)")
    fun isLessonFavoriteFlow(lessonId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_dance_lessons WHERE lessonId = :lessonId)")
    suspend fun isLessonFavorite(lessonId: String): Boolean

    @Query("DELETE FROM favorite_dance_lessons")
    suspend fun clearFavorites()
}

@Dao
interface RecentlyViewedLessonDao {
    @Query("SELECT * FROM recently_viewed_lessons ORDER BY viewedAt DESC LIMIT 3")
    fun getRecentlyViewedLessonsFlow(): Flow<List<RecentlyViewedLesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyViewed(lesson: RecentlyViewedLesson)

    @Query("DELETE FROM recently_viewed_lessons WHERE lessonId = :lessonId")
    suspend fun deleteRecentlyViewed(lessonId: String)

    @Query("DELETE FROM recently_viewed_lessons")
    suspend fun clearRecentlyViewed()
}




