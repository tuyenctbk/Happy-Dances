package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DancerProfile::class,
        CompletedLesson::class,
        EarnedSticker::class,
        DailyDanceSession::class,
        DanceStreakInfo::class,
        BadgeAchievement::class,
        Progress::class,
        StudentMilestone::class,
        FavoriteDanceMove::class,
        DailyChallengeRecord::class,
        DanceLessonEntity::class,
        FavoriteDanceLesson::class,
        RecentlyViewedLesson::class
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dancerProfileDao(): DancerProfileDao
    abstract fun completedLessonDao(): CompletedLessonDao
    abstract fun earnedStickerDao(): EarnedStickerDao
    abstract fun danceStreakDao(): DanceStreakDao
    abstract fun badgeAchievementDao(): BadgeAchievementDao
    abstract fun progressDao(): ProgressDao
    abstract fun studentMilestoneDao(): StudentMilestoneDao
    abstract fun favoriteDanceMoveDao(): FavoriteDanceMoveDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun danceLessonEntityDao(): DanceLessonEntityDao
    abstract fun favoriteDanceLessonDao(): FavoriteDanceLessonDao
    abstract fun recentlyViewedLessonDao(): RecentlyViewedLessonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "happy_dances_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
