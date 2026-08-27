package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.data.BadgeAchievement
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue

data class CurriculumTier(
    val id: String,
    val title: String,
    val ageGroup: String,
    val description: String,
    val iconEmoji: String,
    val badgeId: String,
    val badgeName: String,
    val requiredLessonIds: List<String>,
    val containerBg: List<Color>,
    val accentColor: Color
)

val curriculumTiers = listOf(
    CurriculumTier(
        id = "tier_toddler",
        title = "Toddler Creative Movement",
        ageGroup = "Ages 3–5",
        description = "Story-driven rhythm, pointy toes, mouse pliés, and fluttering butterfly jumps.",
        iconEmoji = "🌸",
        badgeId = "badge_stage_garden",
        badgeName = "Garden Sprite Master",
        requiredLessonIds = listOf("garden_toes", "garden_mouse", "garden_butterfly"),
        containerBg = listOf(Color(0xFFFFF0F5), Color(0xFFFCE7F3)),
        accentColor = Color(0xFFDB2777)
    ),
    CurriculumTier(
        id = "tier_foundation",
        title = "Royal Academy Ballet",
        ageGroup = "Ages 6–8",
        description = "Formal 1st & 2nd position pliés, foot alignment tendus, and relevé balance.",
        iconEmoji = "👑",
        badgeId = "badge_stage_castle",
        badgeName = "Castle Royalty Graduate",
        requiredLessonIds = listOf("castle_pli", "castle_tendu", "castle_releve"),
        containerBg = listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE)),
        accentColor = Color(0xFF2563EB)
    ),
    CurriculumTier(
        id = "tier_junior",
        title = "Junior Technical Virtuoso",
        ageGroup = "Ages 9–12",
        description = "Barre conditioning, spot pirouettes, and high-energy grand allegro leaps.",
        iconEmoji = "🚀",
        badgeId = "badge_stage_moon",
        badgeName = "Cosmic Voyager Virtuoso",
        requiredLessonIds = listOf("moon_barre", "moon_adagio", "moon_allegro"),
        containerBg = listOf(Color(0xFFFAF5FF), Color(0xFFF3E8FF)),
        accentColor = Color(0xFF9333EA)
    )
)

/**
 * Visual Dashboard Component displaying curriculum tiers, progress bars,
 * and digital badge rewards unlocked upon completing each tier.
 */
@Composable
fun CurriculumTiersBadgeDashboard(
    completedLessonIds: Set<String>,
    unlockedBadges: List<BadgeAchievement>,
    onPlayChime: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTierForModal by remember { mutableStateOf<CurriculumTier?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("curriculum_tiers_badge_dashboard")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "Tiers",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Curriculum Tiers & Digital Badges",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                }
                Text(
                    text = "Complete all tier lessons to unlock digital honors & certificates!",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tier Cards Grid / Column
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            curriculumTiers.forEach { tier ->
                val completedInTier = tier.requiredLessonIds.count { it in completedLessonIds }
                val totalInTier = tier.requiredLessonIds.size
                val tierProgressRatio = (completedInTier.toFloat() / totalInTier.toFloat()).coerceIn(0f, 1f)
                val isTierUnlocked = completedInTier >= totalInTier || unlockedBadges.any { it.badgeId == tier.badgeId }

                val animProgress by animateFloatAsState(
                    targetValue = tierProgressRatio,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing),
                    label = "tier_progress"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable {
                            onPlayChime()
                            selectedTierForModal = tier
                        }
                        .testTag("curriculum_tier_card_${tier.id}"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp),
                    border = BorderStrokeColors(
                        1.5.dp,
                        if (isTierUnlocked) tier.accentColor.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge Icon Frame with Glow if unlocked
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(tier.containerBg))
                                    .border(
                                        2.dp,
                                        if (isTierUnlocked) tier.accentColor else Color.Transparent,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(tier.iconEmoji, fontSize = 28.sp)

                                if (isTierUnlocked) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Unlocked",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = tier.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(tier.accentColor.copy(alpha = 0.12f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = tier.ageGroup,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tier.accentColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = tier.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 2,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar & Reward Badge Pill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isTierUnlocked) "Tier Honor Unlocked! 🏅" else "Lesson Tier Progress",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isTierUnlocked) tier.accentColor else Color(0xFF475569)
                                    )
                                    Text(
                                        text = "$completedInTier / $totalInTier Lessons",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { animProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = tier.accentColor,
                                    trackColor = tier.accentColor.copy(alpha = 0.15f)
                                )
                            }

                            // Interactive View Badge Action Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isTierUnlocked) tier.accentColor else Color(0xFFF1F5F9)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isTierUnlocked) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = "Badge",
                                            tint = LemonYellow,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "View Badge",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Locked Badge",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog displaying Tier Badge Certificate & Celebration Details
    selectedTierForModal?.let { tier ->
        val completedInTier = tier.requiredLessonIds.count { it in completedLessonIds }
        val isUnlocked = completedInTier >= tier.requiredLessonIds.size || unlockedBadges.any { it.badgeId == tier.badgeId }

        Dialog(
            onDismissRequest = { selectedTierForModal = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .border(BorderStrokeColors(2.dp, tier.accentColor))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Starburst Lottie if unlocked
                    if (isUnlocked) {
                        val lottieComp by rememberLottieComposition(
                            LottieCompositionSpec.JsonString("""
                            {"v":"5.7.4","fr":30,"ip":0,"op":60,"w":200,"h":200,"nm":"StarBurst","layers":[{"ddd":0,"ind":1,"ty":4,"nm":"Star","ks":{"o":{"k":100},"r":{"k":180},"p":{"k":[100,100,0]},"s":{"k":[120,120,100]}},"shapes":[{"ty":"gr","it":[{"ty":"sr","sy":1,"pt":{"k":5},"p":{"k":[0,0]},"r":{"k":0},"ir":{"k":20},"is":{"k":0},"or":{"k":45},"os":{"k":0}},{"ty":"fl","c":{"k":[1,0.82,0.3,1]}}]}]}]}
                            """.trimIndent())
                        )
                        val lottieProgress by animateLottieCompositionAsState(
                            composition = lottieComp,
                            iterations = LottieConstants.IterateForever
                        )

                        LottieAnimation(
                            composition = lottieComp,
                            progress = { lottieProgress },
                            modifier = Modifier.size(100.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(tier.iconEmoji, fontSize = 40.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isUnlocked) "🎉 Official Tier Badge Earned!" else "🔒 Curriculum Tier Badge",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tier.accentColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = tier.badgeName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = tier.description,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) tier.accentColor.copy(alpha = 0.1f) else Color(0xFFF8FAFC)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Required Lessons Milestone",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$completedInTier of ${tier.requiredLessonIds.size} Lessons Completed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) tier.accentColor else Color(0xFF1E293B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedTierForModal = null },
                        colors = ButtonDefaults.buttonColors(containerColor = tier.accentColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (isUnlocked) "Celebrate Tier Honor! 🏆" else "Close",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
