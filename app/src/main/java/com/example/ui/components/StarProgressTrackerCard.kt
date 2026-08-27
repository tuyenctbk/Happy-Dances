package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BadgeAchievement
import com.example.data.StudentMilestone
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow

/**
 * Visual Progress Tracking component displaying badges, stars, and level milestones
 * earned as students complete dance lessons.
 */
@Composable
fun StarProgressTrackerCard(
    completedLessonCount: Int,
    unlockedBadges: List<BadgeAchievement>,
    milestones: List<StudentMilestone>,
    onPlayChime: () -> Unit = {},
    onOpenAchievements: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {

    val totalStarsEarned = (completedLessonCount * 3) + unlockedBadges.size * 5
    val maxStarsTarget = 50
    val starProgressRatio = (totalStarsEarned.toFloat() / maxStarsTarget.toFloat()).coerceIn(0f, 1f)

    val infiniteTransition = rememberInfiniteTransition(label = "star_pulse")
    val starScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .clickable { onPlayChime() }
            .testTag("star_progress_tracker_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStrokeColors(1.5.dp, Color(0xFFFDE68A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFFDF0), Color(0xFFFFFFFF))
                    )
                )
                .padding(20.dp)
        ) {
            // Header Row with Glowing Star Emblem
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .scale(starScale)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(LemonYellow, Color(0xFFF59E0B))
                            )
                        )
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Star Dance Master",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(LemonYellow.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⭐ ", fontSize = 11.sp)
                                Text(
                                    text = "$totalStarsEarned Stars",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Earn stars by practicing dance steps & reaching milestones!",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Star Progress Indicator Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Star Goal Progress",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "$totalStarsEarned / $maxStarsTarget ⭐",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { starProgressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFFF59E0B),
                    trackColor = Color(0xFFFEF3C7)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Category Stat Pills: Toddler Story, Ballet Basics, Academy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CategoryStarPill(
                    emoji = "🌸",
                    title = "Toddler",
                    subtext = "${completedLessonCount.coerceAtMost(3)}/3 Lessons",
                    color = Color(0xFFEC4899),
                    bgColor = Color(0xFFFCE7F3),
                    modifier = Modifier.weight(1f)
                )

                CategoryStarPill(
                    emoji = "👑",
                    title = "Ballet",
                    subtext = "${unlockedBadges.size} Badges",
                    color = Color(0xFF2563EB),
                    bgColor = Color(0xFFDBEAFE),
                    modifier = Modifier.weight(1f)
                )

                CategoryStarPill(
                    emoji = "🚀",
                    title = "Academy",
                    subtext = "${milestones.count { it.isAchieved }}/${milestones.size.coerceAtLeast(1)} Honors",
                    color = Color(0xFF9333EA),
                    bgColor = Color(0xFFF3E8FF),
                    modifier = Modifier.weight(1f)
                )
            }

            if (onOpenAchievements != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .dpadFocusable(RoundedCornerShape(14.dp))
                        .clickable { onOpenAchievements() }
                        .testTag("open_achievements_overlay_btn"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStrokeColors(1.dp, Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Achievements",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Digital Badges & Achievements 🏆",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun CategoryStarPill(
    emoji: String,
    title: String,
    subtext: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = Color(0xFF475569)
            )
        }
    }
}
