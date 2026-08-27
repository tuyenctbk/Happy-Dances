package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DanceLesson
import com.example.data.DanceStreakInfo
import com.example.data.RecentlyViewedLesson

import com.example.ui.DanceViewModel
import com.example.ui.LessonsProvider
import com.example.ui.components.CelebrationLottieOverlay
import com.example.ui.components.DailyDancePromptCard
import com.example.ui.components.StarProgressTrackerCard
import com.example.ui.components.WeeklyLessonsChart
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import com.example.ui.components.AchievementsOverlayDialog
import com.example.ui.components.dpadFocusable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun MapScreen(
    viewModel: DanceViewModel,
    onNavigateToLibrary: (() -> Unit)? = null,
    onNavigateToLesson: ((DanceLesson) -> Unit)? = null,
    onNavigateToIntroLevel: (() -> Unit)? = null
) {
    val profile by viewModel.profileState.collectAsState()
    val completedLessons by viewModel.completedLessons.collectAsState()
    val streakInfo by viewModel.streakState.collectAsState()
    val dailySessions by viewModel.dailySessions.collectAsState()
    val celebrationEvent by viewModel.celebrationEvent.collectAsState()
    val lastSession by viewModel.lastPlayedSession.collectAsState()
    val unlockedBadges by viewModel.unlockedBadges.collectAsState()
    val milestones by viewModel.studentMilestones.collectAsState()
    val recentlyViewed: List<RecentlyViewedLesson> by viewModel.recentlyViewedLessons.collectAsState()


    var showAchievementsOverlay by remember { mutableStateOf(false) }

    if (showAchievementsOverlay) {
        AchievementsOverlayDialog(
            viewModel = viewModel,
            onDismiss = { showAchievementsOverlay = false }
        )
    }

    // Lottie Celebratory Overlay Trigger
    CelebrationLottieOverlay(
        celebrationData = celebrationEvent,
        onDismiss = { viewModel.dismissCelebrationAndGoHome() }
    )


    val currentStageLessons = LessonsProvider.getLessonsForStage(profile.ageGroup)
    val completedIds = completedLessons.filter { it.stage == profile.ageGroup }.map { it.lessonId }.toSet()

    // Pulsing transition for the active play button & streak flame
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val flamePulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9FB)) // Ballet Canvas background
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("map_screen")
    ) {
        // Welcome Dancer Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular profile avatar container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD1DC))
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌸", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Hi, ${profile.name}!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    val stageLabel = when (profile.ageGroup) {
                        "3-5" -> "IMAGINATION STAGE"
                        "6-8" -> "FOUNDATION STAGE"
                        else -> "TECHNIQUE STAGE"
                    }
                    Text(
                        text = stageLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Stars/badge count container
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f))
                    .border(1.dp, Color(0x4DFFD1DC), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("✨", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${completedLessons.size}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF914D5D)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ----------------- DATASTORE RESUME LAST SESSION CARD -----------------
        if (lastSession.lessonTitle.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(BorderStrokeColors(1.5.dp, Color(0xFF3B82F6)), RoundedCornerShape(22.dp))
                    .testTag("datastore_resume_session_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2563EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "RESUME LAST SESSION ⏯️",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E40AF),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = lastSession.lessonTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.resumeSavedSession() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("resume_session_button")
                    ) {
                        Text("Resume ➔", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // ----------------- VISUAL STAR & BADGE PROGRESS TRACKER -----------------
        StarProgressTrackerCard(
            completedLessonCount = completedLessons.size,
            unlockedBadges = unlockedBadges,
            milestones = milestones,
            onPlayChime = { viewModel.synthesizer.playSparkleChime() },
            onOpenAchievements = { showAchievementsOverlay = true }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ----------------- RECENTLY VIEWED LESSONS SECTION -----------------
        if (recentlyViewed.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(BorderStrokeColors(1.dp, Color(0xFFE2E8F0)), RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .testTag("recently_viewed_section")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Viewed Lessons 🕒",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Last 3 accessed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    recentlyViewed.take(3).forEach { recent ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .dpadFocusable(RoundedCornerShape(16.dp))
                                .clickable { viewModel.selectLessonById(recent.lessonId) }
                                .testTag("recently_viewed_card_${recent.lessonId}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)),
                            border = BorderStrokeColors(1.dp, Color(0xFFFFD1DC))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = recent.emoji, fontSize = 26.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = recent.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF881337),
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFE11D48))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Resume ▶",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }


        // ----------------- MODE & THEME SELECTOR CARD -----------------
        val activeMode by viewModel.activeMode.collectAsState()
        val isStorytelling = activeMode == "storytelling"

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(
                    BorderStrokeColors(
                        1.5.dp,
                        if (isStorytelling) Color(0x66FFD1DC) else Color(0x661E40AF)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .testTag("mode_theme_selector_card"),
            colors = CardDefaults.cardColors(
                containerColor = if (isStorytelling) Color(0xFFFFF0F5) else Color(0xFFF0F4F8)
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isStorytelling) Color(0xFFFFD1DC) else Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isStorytelling) Icons.Default.AutoStories else Icons.Default.School,
                            contentDescription = null,
                            tint = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E40AF),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isStorytelling) "Storytelling Mode" else "Technical Academy",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E293B)
                        )
                        Text(
                            text = if (isStorytelling) "🌸 Toddler Play & Tales" else "🏛️ Junior Ballet & Alignment",
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Switch Mode Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0x33000000), RoundedCornerShape(12.dp))
                            .clickable {
                                val nextMode = if (isStorytelling) "academy" else "storytelling"
                                viewModel.setMode(nextMode)
                                viewModel.synthesizer.playSparkleChime()
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("btn_toggle_mode")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Mode",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isStorytelling) "Academy" else "Stories",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                        }
                    }

                    // Level Picker Shortcut
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isStorytelling) Color(0xFFFFD1DC) else Color(0xFFBFDBFE))
                            .clickable {
                                onNavigateToIntroLevel?.invoke() ?: viewModel.navigateTo("intro_level")
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("btn_open_level_picker")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Level Selector",
                                tint = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E40AF),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.ageGroup} Yrs",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E40AF)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ----------------- DAILY DANCE STREAK WIDGET -----------------
        DailyDanceStreakWidget(
            streakInfo = streakInfo,
            dailySessions = dailySessions.map { it.date }.toSet(),
            flameScale = flamePulse
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ----------------- TIME TO DANCE DAILY PROMPT -----------------
        DailyDancePromptCard(
            viewModel = viewModel,
            onStartPractice = {
                val next = currentStageLessons.firstOrNull { !completedIds.contains(it.id) } ?: currentStageLessons.firstOrNull()
                next?.let { viewModel.selectLesson(it) }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ----------------- WEEKLY LESSONS BAR CHART -----------------
        WeeklyLessonsChart(
            dailySessions = dailySessions
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Active Lesson Hero Card
        val nextLesson = currentStageLessons.firstOrNull { !completedIds.contains(it.id) } ?: currentStageLessons.firstOrNull()
        nextLesson?.let { activeLesson ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("active_lesson_hero_card"),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStrokeColors(4.dp, Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFAEC6CF), Color(0xFF87CEEB))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f))
                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val stageEmoji = when (profile.ageGroup) {
                            "3-5" -> "🌸"
                            "6-8" -> "🏰"
                            else -> "🌙"
                        }
                        Text(stageEmoji, fontSize = 24.sp)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFF9C4))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ACTIVE LESSON",
                                color = Color(0xFF8B7D00),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = activeLesson.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            lineHeight = 28.sp
                        )

                        Text(
                            text = activeLesson.storyTitle,
                            fontSize = 14.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.4f))
                                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                                .padding(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    onNavigateToLesson?.invoke(activeLesson) ?: viewModel.selectLesson(activeLesson)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("start_dancing_hero_button")
                            ) {
                                Text(
                                    text = "▶ Start Dancing",
                                    color = Color(0xFF914D5D),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Entry Choices Grid Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Lesson Library (New Primary Discoverability)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onNavigateToLibrary?.invoke() ?: viewModel.navigateTo("library")
                    }
                    .testTag("quick_action_library"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                border = BorderStrokeColors(1.dp, Color(0xFFFFD1DC)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("📚", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Library",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "All lessons",
                        fontSize = 9.5.sp,
                        color = Color(0xFF914D5D),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Card 2: Story Mode Adventure
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo("story_mode_entry") }
                    .testTag("quick_action_story_mode"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = BorderStrokeColors(1.dp, Color(0x4DFFD1DC)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("📖", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Stories",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Quests",
                        fontSize = 9.5.sp,
                        color = Color(0xFF914D5D),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Card 3: Custom Avatar Choice
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo("avatar") }
                    .testTag("quick_action_avatar"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = BorderStrokeColors(1.dp, Color(0x33AEC6CF)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🩰", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Avatar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Dress up",
                        fontSize = 9.5.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Card 4: Passport Choice
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.navigateTo("passport") }
                    .testTag("quick_action_passport"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = BorderStrokeColors(1.dp, Color(0x3387CEEB)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("⭐", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Passport",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Badges",
                        fontSize = 9.5.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Level Map Regions (Visual Stage Selector cards)
        Text(
            text = "Dancer's Worlds",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val stages = listOf(
                Triple("3-5", "The Garden", Color(0xFFE8F5E9)),
                Triple("6-8", "The Castle", Color(0xFFFFFDE7)),
                Triple("9-12", "The Moon", Color(0xFFE8EAF6))
            )

            stages.forEach { (age, title, bg) ->
                val isActive = profile.ageGroup == age
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.changeAgeGroup(age) }
                        .testTag("world_region_$age"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) bg else bg.copy(alpha = 0.5f)
                    ),
                    border = if (isActive) BorderStrokeColors(2.dp, Color(0xFFFFD1DC)) else BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.6f)),
                    elevation = CardDefaults.cardElevation(if (isActive) 3.dp else 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Ages $age",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD1DC))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Active", color = Color(0xFF914D5D), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Active Region Progress Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("stage_progress_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
            border = BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val completionFraction = if (currentStageLessons.isNotEmpty()) {
                    completedIds.size.toFloat() / currentStageLessons.size.toFloat()
                } else 0f

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Stage Completion",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${completedIds.size}/${currentStageLessons.size} Done",
                        fontSize = 12.sp,
                        color = BalletPink,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { completionFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = BalletPink,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lesson Path Nodes
        Text(
            text = "Your Dance Path",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            currentStageLessons.forEachIndexed { index, lesson ->
                val isCompleted = completedIds.contains(lesson.id)
                val nodeColor = if (isCompleted) Color(0xFFAEC6CF) else Color(0xFFFFD1DC)
                val nodeIconColor = if (isCompleted) Color(0xFF1E293B) else Color(0xFF914D5D)
                val scale = if (!isCompleted) pulseScale else 1.0f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.7f))
                        .border(
                            width = 1.dp,
                            color = if (isCompleted) Color(0x33AEC6CF) else Color(0x4DFFD1DC),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable { viewModel.selectLesson(lesson) }
                        .padding(16.dp)
                        .testTag("lesson_node_${lesson.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .offset(scale.dp)
                            .clip(CircleShape)
                            .background(nodeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = nodeIconColor,
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Lesson",
                                tint = nodeIconColor,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lesson ${index + 1}: ${lesson.title}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = lesson.storyTitle,
                            fontSize = 12.sp,
                            color = Color(0xFF914D5D),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Launch",
                        tint = Color(0xFF64748B)
                    )
                }

                if (index < currentStageLessons.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(28.dp)
                            .background(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Zero barriers disclaimer callout
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SkyBlue.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = "Free platform",
                    tint = SkyBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Happy Dances is 100% free with no locked features. All lesson stages are completely open for play!",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ----------------- DAILY DANCE STREAK COMPONENT -----------------
@Composable
fun DailyDanceStreakWidget(
    streakInfo: DanceStreakInfo,
    dailySessions: Set<String>,
    flameScale: Float
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val todayStr = dateFormat.format(Date())
    val isPracticedToday = dailySessions.contains(todayStr) || streakInfo.lastActiveDate == todayStr

    // Compute last 7 days dates
    val past7Days = remember(dailySessions, todayStr) {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -6)
        for (i in 0 until 7) {
            val dateStr = dateFormat.format(cal.time)
            val dayName = dayNameFormat.format(cal.time).take(1).uppercase()
            list.add(Pair(dateStr, dayName))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_dance_streak_widget"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.85f)
        ),
        border = BorderStrokeColors(
            width = 2.dp,
            color = if (streakInfo.currentStreak > 0) Color(0xFFFFD1DC) else Color(0x33AEC6CF)
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .graphicsLayer {
                                scaleX = flameScale
                                scaleY = flameScale
                            }
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFCC80))
                                )
                            )
                            .border(2.dp, Color(0xFFFFB74D), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${streakInfo.currentStreak} Day Streak",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF914D5D)
                            )
                            if (streakInfo.currentStreak >= 3) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("⭐", fontSize = 14.sp)
                            }
                        }
                        Text(
                            text = if (isPracticedToday) "🎉 Today's dance completed!" else "🩰 Practice today to keep your streak!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isPracticedToday) Color(0xFF2E7D32) else Color(0xFF64748B)
                        )
                    }
                }

                // Best streak badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF8E1))
                        .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏆 Best: ${streakInfo.bestStreak}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57F17)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7-day mini progress dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                past7Days.forEach { (dateStr, dayLabel) ->
                    val isCompleted = dailySessions.contains(dateStr) || (dateStr == todayStr && isPracticedToday)
                    val isToday = dateStr == todayStr

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = dayLabel,
                            fontSize = 10.sp,
                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isToday) Color(0xFF914D5D) else Color(0xFF64748B)
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> Color(0xFFFFD1DC)
                                        isToday -> Color(0xFFFFF3E0)
                                        else -> Color(0xFFF1F5F9)
                                    }
                                )
                                .border(
                                    width = if (isToday) 2.dp else 1.dp,
                                    color = when {
                                        isCompleted -> Color(0xFF914D5D)
                                        isToday -> Color(0xFFFFB74D)
                                        else -> Color(0xFFE2E8F0)
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Practiced",
                                    tint = Color(0xFF914D5D),
                                    modifier = Modifier.size(16.dp)
                                )
                            } else if (isToday) {
                                Text("🩰", fontSize = 12.sp)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFCBD5E1))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
