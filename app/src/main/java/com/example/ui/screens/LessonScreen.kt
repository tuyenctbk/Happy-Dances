package com.example.ui.screens

import android.Manifest
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.ui.CelebrationData
import com.example.ui.DanceViewModel
import com.example.ui.Lesson
import com.example.ui.components.CameraPermissionHelper
import com.example.ui.components.LocalMusicPlayerModal
import com.example.ui.components.VisualPracticeTimer
import com.example.ui.components.PracticeTimerDialog
import com.example.ui.components.AmbientMusicBar
import com.example.ui.components.DanceStarCertificateDialog
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.random.Random

@Composable
fun LessonScreen(
    viewModel: DanceViewModel,
    lesson: Lesson
) {
    var isPracticing by remember { mutableStateOf(false) }
    val celebrationState by viewModel.celebrationEvent.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = isPracticing,
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                }
            },
            label = "lesson_state_transition"
        ) { practicing ->
            if (practicing) {
                StudioPracticeView(
                    viewModel = viewModel,
                    lesson = lesson
                )
            } else {
                StorybookIntroView(
                    viewModel = viewModel,
                    lesson = lesson,
                    onStartPractice = { isPracticing = true },
                    onExit = { viewModel.exitLesson() }
                )
            }
        }

        // Global celebration overlay if milestone or completion happens
        celebrationState?.let { celebration ->
            MilestoneCelebrationOverlay(
                celebration = celebration,
                onDismiss = {
                    if (celebration.isFullLessonCompletion) {
                        viewModel.dismissCelebrationAndGoHome()
                    } else {
                        viewModel.triggerMilestoneCelebration("", "") // clear
                    }
                },
                onContinuePracticing = {
                    viewModel.dismissCelebrationAndGoHome()
                }
            )
        }
    }
}

@Composable
fun StorybookIntroView(
    viewModel: DanceViewModel,
    lesson: Lesson,
    onStartPractice: () -> Unit,
    onExit: () -> Unit
) {
    val favoriteIds by viewModel.favoriteLessonIds.collectAsState()
    val isFav = favoriteIds.contains(lesson.id)
    val isSpeaking by viewModel.isTtsSpeaking.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0F5), // Lavender blush
                        Color(0xFFFFF9E6), // Warm buttercup
                        Color(0xFFE8F4F8)  // Pale sky
                    )
                )
            )
            .padding(20.dp)
            .testTag("storybook_intro_view")
    ) {
        // Floating Top Header Controls
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.stopVoiceInstruction()
                    onExit()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
                    .shadow(2.dp, CircleShape)
                    .testTag("close_storybook_intro")
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Storybook", tint = Color(0xFF914D5D))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Voice Guide Button (TTS)
                IconButton(
                    onClick = {
                        if (isSpeaking) {
                            viewModel.stopVoiceInstruction()
                        } else {
                            viewModel.speakVoiceInstruction("${lesson.title}. ${lesson.storyTitle}. ${lesson.storyContent}. ${lesson.actionGuide}", force = true)
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSpeaking) Color(0xFFFFD1DC) else Color.White.copy(alpha = 0.85f))
                        .shadow(2.dp, CircleShape)
                        .testTag("storybook_voice_guide_button")
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Listen to Story",
                        tint = if (isSpeaking) Color(0xFF914D5D) else Color(0xFF64748B)
                    )
                }

                // Favorite Toggle Button (Room DB)
                IconButton(
                    onClick = { viewModel.toggleFavoriteLesson(lesson) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isFav) Color(0xFFFFE4E6) else Color.White.copy(alpha = 0.85f))
                        .shadow(2.dp, CircleShape)
                        .testTag("storybook_favorite_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFav) "Remove Favorite" else "Add Favorite",
                        tint = if (isFav) Color(0xFFE11D48) else Color(0xFF64748B)
                    )
                }
            }
        }

        // Center Story Presentation Card
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 28.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(6.dp),
            border = BorderStrokeColors(1.dp, Color(0xFFF3E5F5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Story Badge
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0x19914D5D)),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (lesson.stage) {
                        "3-5" -> Icons.Default.Palette
                        "6-8" -> Icons.Default.SelfImprovement
                        else -> Icons.Default.DirectionsWalk
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Lesson motif",
                        tint = Color(0xFF914D5D),
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = lesson.storyTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                val stageLabel = when (lesson.stage) {
                    "3-5" -> "Creative Storytelling • Ages 3-5"
                    "6-8" -> "Ballet Fundamentals • Ages 6-8"
                    else -> "Junior Technical Academy • Ages 9-12"
                }
                Text(
                    text = stageLabel,
                    fontSize = 12.sp,
                    color = Color(0xFF914D5D),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Narrative text
                Text(
                    text = lesson.storyContent,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Instruction Cue Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x1987CEEB)),
                    border = BorderStrokeColors(1.dp, Color(0x3387CEEB)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Guide",
                            tint = Color(0xFF87CEEB),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = lesson.actionGuide,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily Warm-Up Stretch UI Nudge
                var showWarmUpDialog by remember { mutableStateOf(false) }
                var warmUpCompleted by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (warmUpCompleted) Color(0xE6E6F4EA) else Color(0xFFFFF0F5)
                    ),
                    border = BorderStrokeColors(
                        1.dp,
                        if (warmUpCompleted) Color(0xFF34A853) else Color(0xFFFFD1DC)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (warmUpCompleted) "✅" else "🦋",
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (warmUpCompleted) "Stretch Warm-Up Done!" else "Stretch & Sparkle Warm-Up",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (warmUpCompleted) Color(0xFF137333) else Color(0xFF914D5D)
                                    )
                                    Text(
                                        text = if (warmUpCompleted) "Your muscles are beautifully ready! ✨" else "Perform a gentle 2-minute warm-up first!",
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                            }

                            if (!warmUpCompleted) {
                                Button(
                                    onClick = { showWarmUpDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5286)),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Stretch 🦋", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (showWarmUpDialog) {
                    WarmUpStretchingDialog(
                        onDismiss = { showWarmUpDialog = false },
                        onComplete = {
                            warmUpCompleted = true
                            showWarmUpDialog = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onStartPractice,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_practice_button")
                ) {
                    Text("Step into the Studio! 🩰", color = Color(0xFF914D5D), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StudioPracticeView(viewModel: DanceViewModel, lesson: Lesson) {
    val favoriteIds by viewModel.favoriteLessonIds.collectAsState()
    val isFav = favoriteIds.contains(lesson.id)
    val isSpeaking by viewModel.isTtsSpeaking.collectAsState()

    val coachTips = remember {
        listOf(
            "Keep your back straight like a castle tower! 🏰",
            "Point those toes gracefully! ✨",
            "Heels together, bend those knees softly! 🩰",
            "Great balance! Feel the rhythm in the music! 🎵"
        )
    }
    var currentCoachTip by remember { mutableStateOf(coachTips.random()) }

    LaunchedEffect(lesson.id) {
        viewModel.speakVoiceInstruction("${lesson.title}. ${lesson.actionGuide}")
    }

    var cameraSparklesActive by remember { mutableStateOf(false) }
    var isCameraMirrored by remember { mutableStateOf(true) } // Enabled by default for mirror experience
    val userPaintPoints = remember { mutableStateListOf<Offset>() }
    var showMusicModal by remember { mutableStateOf(false) }
    var showPracticeTimer by remember { mutableStateOf(false) }

    if (showPracticeTimer) {
        PracticeTimerDialog(
            initialSeconds = 120,
            title = "${lesson.title} Practice Focus ⏳",
            subtitle = "Keep your posture tall and graceful until the dance star sparkles!",
            onDismiss = { showPracticeTimer = false },
            onPlayChime = { viewModel.synthesizer.playSparkleChime() }
        )
    }

    // Classical Melody playing state indicator animation
    val infiniteTransition = rememberInfiniteTransition(label = "melody_pulse")
    val noteRot by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "music_note_rot"
    )

    val sparklePulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1D21)) // Dark immersive studio theater background
            .testTag("studio_practice_view")
    ) {
        // ----------------- BACKGROUND VIEWPORT ACCORDING TO STAGE -----------------
        if (lesson.stage == "3-5") {
            // Creative Painting Floor Canvas (Ages 3-5)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            userPaintPoints.add(change.position)
                            if (userPaintPoints.size == 25) {
                                viewModel.triggerMilestoneCelebration("Rainbow Movement Milestone! 🌸", "You gracefully pointed your toes and painted the magical garden!")
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw a lovely floral backdrop
                    drawCircle(Color(0xFFFF80AB).copy(alpha = 0.1f), radius = size.width / 3f, center = Offset(size.width / 2f, size.height / 3f))
                    drawCircle(Color(0xFF80D8FF).copy(alpha = 0.1f), radius = size.width / 4f, center = Offset(size.width / 4f, size.height * 2 / 3f))

                    // Draw painted points as glowing rainbow flowers
                    userPaintPoints.forEach { point ->
                        drawCircle(color = BalletPink, radius = 8f, center = point)
                        drawCircle(color = LemonYellow, radius = 4f, center = point)
                    }
                }

                if (userPaintPoints.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "🩰 Drag your finger on the floor to paint beautiful colors with your toes!",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else if (lesson.stage == "6-8") {
            // Footprints Placement Board (Ages 6-8)
            var activeStance by remember { mutableStateOf(1) } // 1st or 2nd Position

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val centerX = w / 2f
                    val centerY = h / 2f

                    // Draw alignment guide lines
                    drawLine(
                        color = Color.White.copy(alpha = 0.15f),
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, h),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.15f),
                        start = Offset(0f, centerY + h / 5f),
                        end = Offset(w, centerY + h / 5f),
                        strokeWidth = 4f
                    )

                    // Draw balance target circle
                    drawCircle(
                        color = SkyBlue.copy(alpha = 0.12f),
                        radius = 160f,
                        center = Offset(centerX, centerY - h / 12f)
                    )
                }

                // Footprint graphics cards
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (activeStance == 1) "1st Position Turnout" else "2nd Position Open",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Step your feet into the alignments!",
                        color = BalletPink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                        // Left foot
                        Box(
                            modifier = Modifier
                                .size(80.dp, 120.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .border(
                                    width = 3.dp,
                                    color = if (activeStance == 1) BalletPink else SkyBlue,
                                    shape = RoundedCornerShape(40.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "L",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }

                        // Right foot
                        Box(
                            modifier = Modifier
                                .size(80.dp, 120.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .border(
                                    width = 3.dp,
                                    color = if (activeStance == 1) BalletPink else SkyBlue,
                                    shape = RoundedCornerShape(40.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "R",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = {
                                activeStance = 1
                                currentCoachTip = "Great first position! Press down into the floor!"
                                cameraSparklesActive = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeStance == 1) BalletPink else Color.Gray.copy(alpha = 0.2f)
                            )
                        ) {
                            Text("1st Position")
                        }

                        Button(
                            onClick = {
                                activeStance = 2
                                currentCoachTip = "Nice turnout extension! Keep those hips square!"
                                cameraSparklesActive = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeStance == 2) BalletPink else Color.Gray.copy(alpha = 0.2f)
                            )
                        ) {
                            Text("2nd Position")
                        }
                    }
                }
            }
        } else {
            // Camera Live Mirror / Posture skeleton (Ages 9-12) with Accompanist CameraPermissionHelper
            CameraPermissionHelper(
                modifier = Modifier.fillMaxSize(),
                onVisualModeFallback = {
                    currentCoachTip = "Visual Guide Mode active! Follow the rhythm & posture alignments! 🌟"
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(
                        isMirrored = isCameraMirrored,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Render simulated calibration skeleton lines
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { scaleX = if (isCameraMirrored) -1f else 1f }
                    ) {
                        val w = size.width
                        val h = size.height

                        // Dotted posture guideline
                        drawLine(
                            color = Color.Green.copy(alpha = 0.4f),
                            start = Offset(w / 2f, h / 8f),
                            end = Offset(w / 2f, h * 7 / 8f),
                            strokeWidth = 4f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        // Head circle
                        drawCircle(
                            color = Color.Green.copy(alpha = 0.3f),
                            radius = 50f,
                            center = Offset(w / 2f, h / 4f),
                            style = Stroke(width = 4f)
                        )

                        // Shoulder guideline
                        drawLine(
                            color = Color.Green.copy(alpha = 0.3f),
                            start = Offset(w / 3f, h * 3 / 8f),
                            end = Offset(w * 2 / 3f, h * 3 / 8f),
                            strokeWidth = 6f
                        )

                        // Hips guideline
                        drawLine(
                            color = Color.Green.copy(alpha = 0.3f),
                            start = Offset(w / 3f, h * 5 / 8f),
                            end = Offset(w * 2 / 3f, h * 5 / 8f),
                            strokeWidth = 6f
                        )
                    }

                    // On-screen calibration status flag & Mirror mode badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Green)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Pose Tracker: 98% aligned",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Mirror status badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCameraMirrored) Color(0xFF914D5D).copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.6f))
                                .clickable {
                                    isCameraMirrored = !isCameraMirrored
                                    viewModel.onMirrorToggled(isCameraMirrored)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Flip,
                                    contentDescription = "Mirror mode",
                                    tint = LemonYellow,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (isCameraMirrored) "🪞 Mirror Mode ON" else "🪞 Mirror OFF",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // ----------------- SPARKLES CELEBRATION PARTICLES -----------------
        if (cameraSparklesActive) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val relativeOffsets = listOf(
                    Offset(0.15f, 0.22f), Offset(0.82f, 0.15f), Offset(0.35f, 0.48f),
                    Offset(0.64f, 0.35f), Offset(0.22f, 0.72f), Offset(0.78f, 0.65f),
                    Offset(0.50f, 0.18f), Offset(0.45f, 0.85f), Offset(0.88f, 0.48f),
                    Offset(0.12f, 0.52f), Offset(0.30f, 0.30f), Offset(0.70f, 0.80f)
                )

                relativeOffsets.forEachIndexed { idx, rel ->
                    val px = w * rel.x
                    val py = h * rel.y
                    val phaseOffset = if (idx % 2 == 0) sparklePulse else (1.6f - sparklePulse)
                    val baseRadius = (15f + (idx * 1.5f)) * phaseOffset

                    drawCircle(
                        color = LemonYellow.copy(alpha = 0.4f * phaseOffset.coerceIn(0f, 1f)),
                        radius = baseRadius * 1.8f,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = LemonYellow.copy(alpha = 0.85f),
                        radius = baseRadius,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = baseRadius * 0.45f,
                        center = Offset(px, py)
                    )
                }
            }
        }

        // ----------------- TOP BAR HUD OVERLAYS -----------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.exitLesson() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("exit_lesson_button")
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Exit Studio", tint = Color.White)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Favorite Button (Room Database)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isFav) Color(0xFFE11D48).copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.5f))
                        .clickable { viewModel.toggleFavoriteLesson(lesson) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("toggle_favorite_lesson_hud")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Lesson",
                            tint = if (isFav) Color.White else LemonYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (isFav) "❤️ Saved" else "🤍 Fav",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Voice Guide Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSpeaking) Color(0xFF914D5D).copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            if (isSpeaking) {
                                viewModel.stopVoiceInstruction()
                            } else {
                                viewModel.speakVoiceInstruction("${lesson.title}. $currentCoachTip", force = true)
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("toggle_voice_guide_hud")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Voice Guide",
                            tint = if (isSpeaking) LemonYellow else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (isSpeaking) "🔊 Voice ON" else "🔈 Voice",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Focus Practice Timer Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { showPracticeTimer = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("toggle_practice_timer_hud")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Focus Timer",
                            tint = SkyBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "⏳ Timer",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mirror View Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCameraMirrored) Color(0xFF914D5D).copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            isCameraMirrored = !isCameraMirrored
                            viewModel.onMirrorToggled(isCameraMirrored)
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("toggle_mirror_mode")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = "Toggle Mirror View",
                            tint = if (isCameraMirrored) LemonYellow else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (isCameraMirrored) "🪞 Mirror: ON" else "🪞 Mirror: OFF",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Music Player Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { showMusicModal = !showMusicModal }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("toggle_music_player")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music player",
                            tint = LemonYellow,
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(noteRot)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "🎼 Music Tracks",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ----------------- EXPANDABLE LOCAL MUSIC SELECTOR MODAL -----------------
        if (showMusicModal) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { showMusicModal = false }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.clickable(enabled = false) {}) {
                    LocalMusicPlayerModal(
                        synthesizer = viewModel.synthesizer,
                        onTrackSelected = { trackId ->
                            viewModel.selectMusicTrack(trackId)
                        }
                    )
                }
            }
        }

        // ----------------- BOTTOM HUD PANELS -----------------
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Coach Speech bubble
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BalletPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👩‍🏫", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = currentCoachTip,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    // Speak tip aloud (TTS)
                    IconButton(
                        onClick = {
                            if (isSpeaking) {
                                viewModel.stopVoiceInstruction()
                            } else {
                                viewModel.speakVoiceInstruction(currentCoachTip, force = true)
                            }
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("coach_bubble_speak_tts")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Read tip aloud",
                            tint = if (isSpeaking) LemonYellow else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Refresh/Change tip
                    IconButton(
                        onClick = {
                            currentCoachTip = coachTips.filter { it != currentCoachTip }.random()
                            cameraSparklesActive = true
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Sparkle",
                            tint = LemonYellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Double Action Bar: Clear/Celebration & Finish
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (lesson.stage == "3-5" && userPaintPoints.isNotEmpty()) {
                    Button(
                        onClick = { userPaintPoints.clear() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clear Paint", color = Color.White, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = {
                            cameraSparklesActive = true
                            currentCoachTip = "Woohoo! Sparkle dance party active! 🌟"
                            viewModel.triggerMilestoneCelebration("Dance Movement Sparkle! ✨", "You are balancing and moving with wonderful grace!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Sparkle", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cheer Sparkles", color = Color.White, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = { viewModel.completeActiveLesson() },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("complete_lesson_action")
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Complete", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Finish Lesson! 🌟", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                cameraSparklesActive = false
            }
        }
    }
}

@Composable
fun CameraPreview(
    isMirrored: Boolean,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Camera binding failed", e)
                }
            }, executor)
            previewView
        },
        modifier = modifier.graphicsLayer {
            scaleX = if (isMirrored) -1f else 1f
        }
    )
}

// ----------------- MILESTONE & COMPLETION CELEBRATION OVERLAY -----------------
@Composable
fun MilestoneCelebrationOverlay(
    celebration: CelebrationData,
    onDismiss: () -> Unit,
    onContinuePracticing: () -> Unit
) {
    var showCertificateModal by remember { mutableStateOf(false) }

    if (showCertificateModal) {
        DanceStarCertificateDialog(
            dancerName = "Star Dancer",
            milestoneTitle = celebration.title,
            milestoneDescription = celebration.subtitle,
            achievementLevel = if (celebration.isFullLessonCompletion) "Lesson Graduate 🏆" else "Movement Star ⭐",
            onDismiss = { showCertificateModal = false }
        )
    }

    // Confetti particles state
    val confettiParticles = remember {
        List(40) {
            ConfettiParticle(
                xNorm = Random.nextFloat(),
                speed = Random.nextFloat() * 400f + 250f,
                size = Random.nextFloat() * 12f + 8f,
                color = listOf(
                    Color(0xFFFFD1DC), // Ballet Pink
                    Color(0xFFFFD54F), // Lemon Yellow
                    Color(0xFF87CEEB), // Sky Blue
                    Color(0xFFA5D6A7), // Mint Green
                    Color(0xFFCE93D8)  // Lavender
                ).random(),
                rotationSpeed = Random.nextFloat() * 360f - 180f
            )
        }
    }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3500, easing = LinearEasing)
        )
    }

    val trophyScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 200f),
        label = "trophy_spring"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "celebration_halo")
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .testTag("celebration_overlay_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Confetti Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            confettiParticles.forEach { particle ->
                val px = particle.xNorm * w
                val py = (particle.speed * animProgress.value * 2f) % (h + 100f) - 50f
                drawCircle(
                    color = particle.color,
                    radius = particle.size,
                    center = Offset(px, py)
                )
            }
        }

        // Celebration dialog card
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .scale(trophyScale),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(16.dp),
            border = BorderStrokeColors(3.dp, Color(0xFFFFD54F))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy / Halo graphic with Lottie celebratory starburst
                Box(contentAlignment = Alignment.Center) {
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
                        modifier = Modifier.size(120.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(100.dp * haloPulse)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(LemonYellow.copy(alpha = 0.45f), Color.Transparent)
                                )
                            )
                    )
                    Text(
                        text = if (celebration.isFullLessonCompletion) "🏆" else "🌟",
                        fontSize = 54.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = celebration.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = celebration.subtitle,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )

                // Sticker award badge
                if (celebration.stickerTitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStrokeColors(1.dp, Color(0xFFFFD54F)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✨", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Passport Sticker Earned!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                                Text(
                                    text = celebration.stickerTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }

                if (celebration.badgeUnlockedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = celebration.badgeUnlockedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // View & Print Certificate button
                Button(
                    onClick = { showCertificateModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF3C7)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStrokeColors(1.dp, Color(0xFFFBBF24)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("celebration_view_certificate_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Certificate",
                        tint = Color(0xFFB45309),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "View 'Dance Star' Certificate 📜",
                        color = Color(0xFF78350F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onContinuePracticing,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("celebration_continue_button")
                ) {
                    Text(
                        text = if (celebration.isFullLessonCompletion) "Back to Studio Map 🗺️" else "Keep Dancing! 🩰",
                        color = Color(0xFF914D5D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WarmUpStretchingDialog(
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(120) } // 2 minutes
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft--
        }
    }

    val progress = timeLeft.toFloat() / 120f
    val currentStretchText = when {
        timeLeft > 90 -> "🦋 Butterfly Wings: Sit tall, touch your feet together, and gently flutter your knees to warm up those hips!"
        timeLeft > 60 -> "🌟 Reach for the Stars: Sit or stand tall, reach your fingers way up to the magical ballet clouds!"
        timeLeft > 30 -> "🩰 Slippery Slippers: Reach forward gently to slide your hands down to tickle your toes!"
        else -> "🦢 Soft Swan Breaths: Stretch your swan wings wide. Inhale deep, exhale slow, and let your body melt like snow!"
    }
    
    val currentEmoji = when {
        timeLeft > 90 -> "🦋"
        timeLeft > 60 -> "🌟"
        timeLeft > 30 -> "🩰"
        else -> "🦢"
    }

    val currentTitle = when {
        timeLeft > 90 -> "Butterfly Flutter"
        timeLeft > 60 -> "Reach to the Stars"
        timeLeft > 30 -> "Toe Tickle Stretch"
        else -> "Swan Wing Breaths"
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("warmup_stretching_dialog"),
            border = BorderStrokeColors(2.dp, BalletPink)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🩰 Stretch & Sparkle Warm-Up",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF914D5D),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Get ready to dance safely! 🌟",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Radial timer progress
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(130.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color(0xFFFFF0F5),
                            style = Stroke(width = 8.dp.toPx())
                        )
                        drawArc(
                            color = Color(0xFFFF5286),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentEmoji,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val minutes = timeLeft / 60
                        val seconds = timeLeft % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title of active stretch
                Text(
                    text = currentTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5286),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Text detailing stretch instructions
                Text(
                    text = currentStretchText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.height(72.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Controls row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { isRunning = !isRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRunning) Color(0xFFFFF0F5) else Color(0xFFFFD1DC)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isRunning) "Pause ⏸️" else "Start ▶️",
                            color = Color(0xFF914D5D),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            onComplete()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5286)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (timeLeft == 0) "All Done! 🎉" else "Done stretch 🩰",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

data class ConfettiParticle(
    val xNorm: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float
)
