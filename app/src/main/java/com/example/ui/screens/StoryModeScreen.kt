package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.components.dpadFocusable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DanceViewModel
import com.example.ui.StoryAdventure
import com.example.ui.StoryChapter
import com.example.ui.StoryModeProvider
import com.example.ui.components.LocalMusicPlayerModal
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue

@Composable
fun StoryModeScreen(viewModel: DanceViewModel) {
    val activeStory by viewModel.activeStory.collectAsState()
    val chapterIdx by viewModel.storyChapterIndex.collectAsState()
    val celebrationState by viewModel.celebrationEvent.collectAsState()
    var isToddlerModeActive by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF0F5), // Lavender blush
                        Color(0xFFFFF9E6), // Buttercup warm
                        Color(0xFFE0F2FE)  // Soft Sky
                    )
                )
            )
            .testTag("story_mode_screen")
    ) {
        if (isToddlerModeActive) {
            ToddlerStoryDanceScreen(
                viewModel = viewModel,
                onBack = { isToddlerModeActive = false }
            )
        } else if (activeStory == null) {
            // Story Quest Adventure Selection Carousel
            StoryAdventureSelectionView(
                onSelectAdventure = { story ->
                    viewModel.startStoryAdventure(story)
                },
                onLaunchToddlerMode = { isToddlerModeActive = true },
                onBack = { viewModel.navigateTo("map") }
            )
        } else {
            // Active Narrative Chapter View
            ActiveStoryChapterView(
                story = activeStory!!,
                currentChapterIndex = chapterIdx,
                viewModel = viewModel
            )
        }

        // Global celebration overlay
        celebrationState?.let { celebration ->
            MilestoneCelebrationOverlay(
                celebration = celebration,
                onDismiss = {
                    if (celebration.isFullLessonCompletion) {
                        viewModel.dismissCelebrationAndGoHome()
                    } else {
                        viewModel.dismissCelebrationAndGoHome()
                    }
                },
                onContinuePracticing = {
                    if (celebration.isFullLessonCompletion) {
                        viewModel.dismissCelebrationAndGoHome()
                    } else {
                        // Dismiss transient chapter step celebration
                        viewModel.triggerMilestoneCelebration("", "")
                    }
                }
            )
        }
    }
}

@Composable
fun StoryAdventureSelectionView(
    onSelectAdventure: (StoryAdventure) -> Unit,
    onLaunchToddlerMode: () -> Unit = {},
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 800.dp)
                .padding(16.dp)
        ) {
        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .shadow(2.dp, CircleShape)
                        .testTag("story_mode_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF914D5D)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "📖 Story Mode Adventures",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Dance step-by-step through magical narrative tales!",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                // Feature Banner for Toddler Interactive Narrative Studio
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onLaunchToddlerMode() }
                        .testTag("btn_launch_toddler_story_studio"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(5.dp),
                    border = BorderStrokeColors(2.dp, Color(0xFFFFD1DC))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFF0F5), Color(0xFFFFF9E6))
                                )
                            )
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .shadow(2.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌸", fontSize = 32.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Toddler Story Dance Studio",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF914D5D)
                                )
                            }
                            Text(
                                text = "Ages 3–5 • Interactive Lottie animations, bunny hops & butterfly sways!",
                                fontSize = 11.sp,
                                color = Color(0xFF475569),
                                lineHeight = 15.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Open",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            items(StoryModeProvider.stories) { adventure ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectAdventure(adventure) }
                        .testTag("story_adventure_card_${adventure.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStrokeColors(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x26FFD1DC)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(adventure.bannerEmoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = adventure.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Hero: ${adventure.heroName} • ${adventure.chapters.size} Chapters",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF914D5D)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = adventure.subtitle,
                            fontSize = 13.sp,
                            color = Color(0xFF475569),
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Chapter pill preview & play button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                adventure.chapters.forEachIndexed { idx, _ ->
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF1F5F9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${idx + 1}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = { onSelectAdventure(adventure) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(
                                    text = "Enter Tale 🩰",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF914D5D)
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

@Composable
fun ActiveStoryChapterView(
    story: StoryAdventure,
    currentChapterIndex: Int,
    viewModel: DanceViewModel
) {
    val chapter = story.chapters[currentChapterIndex]
    val totalChapters = story.chapters.size
    val progress = (currentChapterIndex + 1).toFloat() / totalChapters.toFloat()

    var isPerformingMovement by remember { mutableStateOf(false) }
    var userDanceCount by remember { mutableStateOf(0) }
    var showMusicModal by remember { mutableStateOf(false) }

    // Visual pulse for narrative immersion
    val infiniteTransition = rememberInfiniteTransition(label = "story_pulse")
    val characterBounce by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "char_bounce"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 800.dp)
                .padding(16.dp)
        ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { viewModel.exitStory() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .shadow(2.dp, CircleShape)
                    .testTag("exit_story_button")
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Exit", tint = Color(0xFF914D5D))
            }

            // Chapter Progress Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "${story.title} • Chapter ${chapter.chapterNumber} of $totalChapters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF914D5D),
                    trackColor = Color(0x33914D5D)
                )
            }

            // Music Modal Toggle
            IconButton(
                onClick = { showMusicModal = !showMusicModal },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .shadow(2.dp, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.MusicNote, contentDescription = "Music", tint = Color(0xFF914D5D))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Narrative Storybook Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(6.dp),
            border = BorderStrokeColors(1.dp, Color(0xFFF1F5F9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Character Emoji Motif with dynamic bounce
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0x26FFD1DC))
                        .offset(y = characterBounce.coerceIn(-4f, 4f).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(chapter.characterEmoji, fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = chapter.chapterTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rich Story Narrative Body
                Text(
                    text = chapter.narrativeText,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dance Movement Quest Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x19FFD1DC)),
                    border = BorderStrokeColors(1.dp, Color(0x66FFD1DC))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🩰", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Movement Quest: ${chapter.danceActionPrompt}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF914D5D)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = chapter.movementInstruction,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "💡 Clue: ${chapter.visualClue}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD97706)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Interactive Dance Performance Tap Button (or Complete Chapter)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            userDanceCount++
                            viewModel.synthesizer.playSparkleChime()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("practice_dance_repetition_button")
                    ) {
                        Text(
                            text = if (userDanceCount == 0) "Try Step ✨" else "Rep: $userDanceCount 🌟",
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            userDanceCount = 0
                            viewModel.completeStoryChapter()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp)
                            .testTag("complete_chapter_button")
                    ) {
                        Text(
                            text = if (currentChapterIndex + 1 == totalChapters) "Finish Story Tale! 🏆" else "Complete Step ➡️",
                            color = Color(0xFF914D5D),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Expandable Local Music Player overlay if requested
        if (showMusicModal) {
            Spacer(modifier = Modifier.height(12.dp))
            LocalMusicPlayerModal(
                synthesizer = viewModel.synthesizer,
                onTrackSelected = { trackId ->
                    viewModel.selectMusicTrack(trackId)
                }
            )
        }
    }
    }
}
