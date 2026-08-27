package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dancer_profile")
data class DancerProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Tiny Dancer",
    val ageGroup: String = "3-5", // "3-5", "6-8", "9-12"
    val avatarOutfit: String = "tutu_pink", // "tutu_pink", "ballet_blue", "royal_gold"
    val avatarHair: String = "bun_brown", // "bun_brown", "braids_black", "curls_blonde"
    val avatarBackground: String = "garden", // "garden", "castle", "moon"
    val totalSessionsCount: Int = 0,
    val totalPracticeMinutes: Int = 0,
    val dailyReminderHour: Int = 16, // 4:00 PM default
    val dailyReminderMinute: Int = 0,
    val dailyReminderEnabled: Boolean = true,
    val selectedMusicTrack: String = "chopin_waltz"
)

@Entity(tableName = "completed_lessons")
data class CompletedLesson(
    @PrimaryKey val lessonId: String,
    val stage: String,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "earned_stickers")
data class EarnedSticker(
    @PrimaryKey val stickerId: String,
    val title: String,
    val description: String,
    val stage: String,
    val earnedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_dance_sessions")
data class DailyDanceSession(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val sessionCount: Int = 1,
    val totalMinutes: Int = 5,
    val lastCompletedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "dance_streak_info")
data class DanceStreakInfo(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastActiveDate: String = "" // YYYY-MM-DD
)

@Entity(tableName = "badge_achievements")
data class BadgeAchievement(
    @PrimaryKey val badgeId: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val category: String, // "streak", "milestone", "stage", "mastery"
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

@Entity(tableName = "progress")
data class Progress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: String,
    val lessonTitle: String,
    val danceLevel: String, // "3-5" (Toddler), "6-8" (Foundation), "9-12" (Junior)
    val sessionMode: String, // "storytelling", "academy"
    val isCompleted: Boolean = true,
    val completionStatus: String = "completed", // "completed", "partial"
    val repetitionsCount: Int = 1,
    val durationSeconds: Int = 180,
    val accuracyScore: Float = 98f,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "student_milestones")
data class StudentMilestone(
    @PrimaryKey val milestoneId: String,
    val title: String,
    val description: String,
    val category: String, // "toddler_storytelling", "ballet_basics", "academy_training"
    val starsReward: Int = 5,
    val targetCount: Int = 3,
    val currentCount: Int = 0,
    val isAchieved: Boolean = false,
    val achievedAt: Long? = null
)

@Entity(tableName = "favorite_dance_moves")
data class FavoriteDanceMove(
    @PrimaryKey val moveId: String,
    val moveName: String,
    val category: String, // e.g. "Pliés & Relevés", "Jumps & Leap", "Story Poses"
    val description: String = "",
    val iconEmoji: String = "🩰",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_challenges")
data class DailyChallengeRecord(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val challengeId: String,
    val title: String,
    val moveName: String,
    val description: String,
    val starsAwarded: Int = 5,
    val completedAt: Long = System.currentTimeMillis()
)

data class DailyDanceChallenge(
    val id: String,
    val title: String,
    val moveName: String,
    val description: String,
    val stepInstructions: List<String>,
    val emoji: String,
    val stage: String,
    val durationSeconds: Int = 60,
    val musicTrack: String = "nutcracker_flutes",
    val coachTip: String
)

/**
 * DanceLesson model for the Lesson Library and Player.
 * Includes title, ageCategory, description, and thumbnailUrl.
 */
data class DanceLesson(
    val id: String,
    val title: String,
    val ageCategory: String, // e.g. "Ages 3–5", "Ages 6–8", "Ages 9–12"
    val description: String,
    val thumbnailUrl: String,
    val stage: String = "3-5", // "3-5", "6-8", "9-12"
    val storyTitle: String = "",
    val storyContent: String = "",
    val actionGuide: String = "",
    val visualPrompt: String = "",
    val stickerTitle: String = "",
    val stickerDescription: String = "",
    val durationMinutes: Int = 4,
    val emoji: String = "🩰",
    val musicTrack: String = "chopin_waltz",
    val difficulty: String = "Beginner"
)

@Entity(tableName = "dance_lesson_entities")
data class DanceLessonEntity(
    @PrimaryKey val id: String,
    val lessonName: String,
    val category: String,
    val completionStatus: String, // "completed", "in_progress", "not_started"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_dance_lessons")
data class FavoriteDanceLesson(
    @PrimaryKey val lessonId: String,
    val title: String,
    val ageCategory: String,
    val difficulty: String,
    val thumbnailUrl: String,
    val durationMinutes: Int = 4,
    val emoji: String = "🩰",
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_viewed_lessons")
data class RecentlyViewedLesson(
    @PrimaryKey val lessonId: String,
    val title: String,
    val ageCategory: String,
    val stage: String,
    val thumbnailUrl: String,
    val durationMinutes: Int = 4,
    val emoji: String = "🩰",
    val viewedAt: Long = System.currentTimeMillis()
)


