package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BadgeAchievement
import com.example.ui.DanceViewModel
import com.example.ui.components.CurriculumTiersBadgeDashboard
import com.example.ui.components.WeeklyDanceTimeChart
import com.example.ui.components.VisualProgressTrackingView
import com.example.ui.components.DanceStarCertificateCard
import com.example.ui.components.DanceStarCertificateDialog
import com.example.ui.components.CertificateTheme
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue
import androidx.compose.material.icons.filled.WorkspacePremium

@Composable
fun PassportScreen(viewModel: DanceViewModel) {
    val profile by viewModel.profileState.collectAsState()
    val earnedStickers by viewModel.earnedStickers.collectAsState()
    val unlockedBadges by viewModel.unlockedBadges.collectAsState()
    val completedLessonIds by viewModel.completedLessonIds.collectAsState()
    val streakInfo by viewModel.streakState.collectAsState()
    val dailySessions by viewModel.dailySessions.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) } // 0: Journey, 1: Certificates, 2: Stickers, 3: Badges, 4: Weekly Practice Chart
    var showCertificateDialog by remember { mutableStateOf(false) }
    var activeCertificateTitle by remember { mutableStateOf("Grand Ballet Star Award 🌸") }
    var activeCertificateDesc by remember { mutableStateOf("Completed all stage movement foundations with exceptional joy, musicality, and balance!") }
    var activeCertificateTheme by remember { mutableStateOf(CertificateTheme.ROYAL_GOLD) }

    if (showCertificateDialog) {
        DanceStarCertificateDialog(
            dancerName = profile.name,
            milestoneTitle = activeCertificateTitle,
            milestoneDescription = activeCertificateDesc,
            onDismiss = { showCertificateDialog = false },
            onPlayChime = { viewModel.synthesizer.playSparkleChime() }
        )
    }

    // Set of all static stickers matching our lessons & story modes
    val allStickers = listOf(
        StickerDef("stk_garden_toes", "Rainbow Painter", "Earned by painting beautiful flowers with pointed toes.", "3-5", Color(0xFFFF4081)),
        StickerDef("stk_garden_mouse", "Quiet Little Mouse", "Earned by hiding small and straight in Pliés.", "3-5", Color(0xFFAB47BC)),
        StickerDef("stk_garden_butterfly", "Sky Butterfly", "Awarded for soft springy jumps and fluttering wings.", "3-5", Color(0xFF29B6F6)),
        StickerDef("stk_castle_pli", "Royal Courtier", "Mastered proper turnout in first & second positions.", "6-8", Color(0xFFFFB300)),
        StickerDef("stk_castle_tendu", "Mirror Polisher", "Achieved flawless straight ankle extension (Tendu).", "6-8", Color(0xFF26A69A)),
        StickerDef("stk_castle_releve", "Crystal Balancer", "Balanced perfectly on the balls of your feet in Relevé.", "6-8", Color(0xFF26C6DA)),
        StickerDef("stk_moon_barre", "Gravity Defier", "Completed intense Space Barre combinations.", "9-12", Color(0xFF5C6BC0)),
        StickerDef("stk_moon_adagio", "Cosmic Orbit", "Demonstrated slow-motion control and poise in Extensions.", "9-12", Color(0xFF78909C)),
        StickerDef("stk_moon_allegro", "Lunar Leaper", "Leaped high and memorized Grand Allegro combinations.", "9-12", Color(0xFF8D6E63)),
        StickerDef("stk_story_swan_princess_rescue", "Swan Lake Hero", "Completed the 4-chapter Enchanted Swan Lake quest.", "Story", Color(0xFFEC407A)),
        StickerDef("stk_story_secret_garden_bloom", "Master Bloomer", "Restored rainbow colors to the garden kingdom.", "Story", Color(0xFF66BB6A)),
        StickerDef("stk_story_cosmic_constellation_voyage", "Galactic Pathfinder", "Completed zero-gravity dance balance sequences.", "Story", Color(0xFF7E57C2))
    )

    // Complete badge definitions
    val allBadges = listOf(
        BadgeDef("badge_first_step", "First Dance Step 🩰", "Complete your very first dance lesson in any studio.", "🩰"),
        BadgeDef("badge_lessons_3", "Rising Star Dancer 🌟", "Marked 3 lessons as completed in your dance journal.", "🌟"),
        BadgeDef("badge_lessons_5", "Ballet Master 👑", "Completed 5 dance lessons across the stages.", "👑"),
        BadgeDef("badge_streak_3", "3-Day Dance Hero 🔥", "Maintain a 3-day consecutive dance streak.", "🔥"),
        BadgeDef("badge_streak_7", "7-Day Star Ballerina ⭐", "Achieve an amazing 7-day daily dance streak.", "⭐"),
        BadgeDef("badge_stage_garden", "Garden Sprite 🌸", "Complete all creative movement lessons in the Garden.", "🌸"),
        BadgeDef("badge_stage_castle", "Castle Royalty 👑", "Master all ballet postures in the Castle stage.", "👑"),
        BadgeDef("badge_stage_moon", "Cosmic Voyager 🚀", "Conquer the advanced Junior Academy on the Moon.", "🚀"),
        BadgeDef("badge_mirror_master", "Mirror Perfectionist 🪞", "Practiced dancing with camera mirror mode enabled.", "🪞"),
        BadgeDef("badge_story_teller", "Storybook Dancer 📖", "Completed an interactive Story Mode adventure tale.", "📖"),
        BadgeDef("badge_music_maestro", "Music Maestro 🎵", "Customized background classical melodies in studio.", "🎵"),
        BadgeDef("badge_sticker_collector", "Master Passport ✨", "Collected 5 or more digital stickers in your Passport.", "✨")
    )

    val earnedStickerIds = earnedStickers.map { it.stickerId }.toSet()
    val earnedBadgeIds = unlockedBadges.map { it.badgeId }.toSet()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9FB)) // Frosted Cream base
            .padding(top = 20.dp, start = 16.dp, end = 16.dp)
            .testTag("passport_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${profile.name}'s Passport & Stats",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Track your consistency, collect stickers, and unlock dance badges!",
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Progress counter card with Share
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
            border = BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Stars",
                        tint = LemonYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Achievements Overview",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "${earnedStickerIds.size} Stickers • ${earnedBadgeIds.size} Badges • 🔥 ${streakInfo.currentStreak} Streak",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Button(
                    onClick = {
                        val shareText = "🌟 My little dancer ${profile.name} is learning ballet on Happy Dances! " +
                                "We have earned ${earnedStickerIds.size} stickers and ${earnedBadgeIds.size} achievement badges with a ${streakInfo.currentStreak}-day dance streak! 🩰"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share My Achievements"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BalletPink),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("share_passport_button")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF914D5D), modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", color = Color(0xFF914D5D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 5 Tabs: Progress Journey, Certificates, Stickers, Badges, Weekly Practice Chart
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White.copy(alpha = 0.6f),
            contentColor = Color(0xFF914D5D),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0x33FFD1DC), RoundedCornerShape(16.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Journey 📈",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "Certificates 📜",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        "Stickers (${earnedStickerIds.size})",
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = {
                    Text(
                        "Badges (${earnedBadgeIds.size})",
                        fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                text = {
                    Text(
                        "Weekly 📊",
                        fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                // Visual Progress Journey with Recharts-style Analytics & Favorite Moves
                VisualProgressTrackingView(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
            1 -> {
                // Printable & Shareable Digital Certificates Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("passport_certificates_tab"),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = BorderStrokeColors(1.2.dp, LemonYellow),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("👑", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Official Milestone Certificates",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF78350F)
                                        )
                                        Text(
                                            text = "Earn, customize, print, or share your digital diplomas!",
                                            fontSize = 11.sp,
                                            color = Color(0xFF92400E)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Milestone Certificates Catalog
                    item {
                        val milestoneCertificates = listOf(
                            Triple("Grand Ballet Star Award 🌸", "Completed foundation ballet techniques and posture mastery with poise.", "Certified Star Laureate 🩰"),
                            Triple("7-Day Star Ballerina ⭐", "Maintained seven consecutive days of joyful dance practice routines.", "Consistency Champion 🔥"),
                            Triple("Swan Lake Story Laureate 🦢", "Completed the enchanted story quests with musicality and grace.", "Storybook Hero 📖"),
                            Triple("Creative Movement Prodigy 🌈", "Mastered toddler balance, toe painting, and imaginative gestures.", "Creative Maestro ✨"),
                            Triple("Junior Academy Virtuoso 🚀", "Successfully completed Junior Academy Barre combinations and Adagio.", "Virtuoso Dancer 👑")
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            milestoneCertificates.forEach { (title, desc, level) ->
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStrokeColors(1.dp, Color(0xFFE2E8F0)),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.WorkspacePremium,
                                                    contentDescription = "Certificate",
                                                    tint = LemonYellow,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = title,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF1E293B)
                                                    )
                                                    Text(
                                                        text = level,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = SkyBlue
                                                    )
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    activeCertificateTitle = title
                                                    activeCertificateDesc = desc
                                                    showCertificateDialog = true
                                                    viewModel.synthesizer.playSparkleChime()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = BalletPink),
                                                shape = RoundedCornerShape(12.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.testTag("open_certificate_${title.take(10)}")
                                            ) {
                                                Text(
                                                    text = "View & Print 📜",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF914D5D)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = desc,
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Live Certificate Preview Sample
                    item {
                        Text(
                            text = "Live Certificate Preview",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        DanceStarCertificateCard(
                            dancerName = profile.name,
                            milestoneTitle = activeCertificateTitle,
                            milestoneDescription = activeCertificateDesc,
                            theme = activeCertificateTheme
                        )
                    }
                }
            }
            2 -> {
                // Sticker collection grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("passport_stickers_grid"),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allStickers) { sticker ->
                        val isEarned = earnedStickerIds.contains(sticker.id)
                        StickerCard(sticker = sticker, isEarned = isEarned)
                    }
                }
            }
            3 -> {
                // Curriculum Tiers Badge Dashboard & Milestones
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("passport_badges_column"),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        CurriculumTiersBadgeDashboard(
                            completedLessonIds = completedLessonIds,
                            unlockedBadges = unlockedBadges,
                            onPlayChime = { viewModel.synthesizer.playSparkleChime() }
                        )
                    }

                    item {
                        Text(
                            text = "Individual Achievements & Badges",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .testTag("passport_badges_grid"),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(allBadges) { badge ->
                                val isEarned = earnedBadgeIds.contains(badge.id)
                                BadgeCard(badge = badge, isEarned = isEarned)
                            }
                        }
                    }
                }
            }
            4 -> {
                // Weekly Dance Time Visualization Component
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("passport_weekly_chart_view"),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        WeeklyDanceTimeChart(dailySessions = dailySessions)
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                            border = BorderStrokeColors(1.dp, Color(0xFFF1F5F9))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🌟 Healthy Habit Insights",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• Consistent short daily practice (3-5 minutes) produces better motor memory than single long sessions.\n• The camera mirror encourages posture symmetry and coordination self-correction.\n• Celebrate dance streaks together with your child!",
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class StickerDef(
    val id: String,
    val title: String,
    val description: String,
    val stage: String,
    val color: Color
)

data class BadgeDef(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String
)

@Composable
fun StickerCard(sticker: StickerDef, isEarned: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEarned) 1f else 0.65f)
            .testTag("sticker_card_${sticker.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEarned) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.45f)
        ),
        border = BorderStrokeColors(
            width = 1.dp,
            color = if (isEarned) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(if (isEarned) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isEarned) sticker.color.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.2f))
                    .border(
                        width = 2.5.dp,
                        color = if (isEarned) sticker.color else Color.LightGray.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isEarned) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Earned",
                        tint = sticker.color,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sticker.title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isEarned) Color(0xFF1E293B) else Color(0xFF64748B),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = "Stage: ${sticker.stage}",
                fontSize = 10.sp,
                color = if (isEarned) Color(0xFF914D5D) else Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isEarned) sticker.description else "Locked. Complete this on the World Map or Story Mode!",
                fontSize = 10.sp,
                color = if (isEarned) Color(0xFF334155) else Color(0xFF64748B).copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                minLines = 3
            )
        }
    }
}

@Composable
fun BadgeCard(badge: BadgeDef, isEarned: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEarned) 1f else 0.65f)
            .testTag("badge_card_${badge.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEarned) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.45f)
        ),
        border = BorderStrokeColors(
            width = 1.dp,
            color = if (isEarned) Color(0xFFFFD1DC) else Color.White.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(if (isEarned) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isEarned) Color(0xFFFFF9C4) else Color.LightGray.copy(alpha = 0.2f))
                    .border(
                        width = 2.5.dp,
                        color = if (isEarned) Color(0xFFFFD54F) else Color.LightGray.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isEarned) {
                    Text(badge.emoji, fontSize = 26.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isEarned) Color(0xFF1E293B) else Color(0xFF64748B),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = badge.description,
                fontSize = 10.sp,
                color = if (isEarned) Color(0xFF334155) else Color(0xFF64748B).copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                minLines = 3
            )
        }
    }
}
