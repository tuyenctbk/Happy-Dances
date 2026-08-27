package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.data.DanceLesson
import com.example.ui.DanceViewModel
import com.example.ui.components.CelebrationLottieOverlay
import com.example.ui.components.LocalMusicPlayerModal
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue

data class ToddlerNarrativeStep(
    val stepTitle: String,
    val storyCharacter: String, // Emoji
    val storyPrompt: String,
    val danceAction: String,
    val lottieAnimType: String, // "starburst", "butterfly", "sparkles"
    val rewardStickerName: String
)

val toddlerLessonsList = listOf(
    DanceLesson(
        id = "toddler_garden_bunny",
        title = "Bunny Hop & Pointy Toes",
        ageCategory = "Ages 3–5",
        description = "Twinkle the Garden Bunny teaches cute bunny hops and pointed ballet toes!",
        thumbnailUrl = "https://images.unsplash.com/photo-1518834107812-67b0b7c58434?w=600&auto=format&fit=crop&q=80",
        stage = "3-5",
        storyTitle = "Twinkle Bunny's Garden Hop",
        storyContent = "Welcome to the Secret Flower Garden! Twinkle the Bunny needs your help reaching the highest carrot tops. Let's point our ballet toes and stretch tall!",
        actionGuide = "Point one foot out in front like a bunny whisker! Stretch your arches and gently hop on the balls of your feet.",
        visualPrompt = "Tap the screen to make Twinkle do a happy spin!",
        stickerTitle = "Bunny Hopper 🐰",
        stickerDescription = "Awarded for mastering pointed toes and light springy hops in the garden.",
        durationMinutes = 3,
        emoji = "🐰",
        musicTrack = "chopin_waltz",
        difficulty = "Toddler"
    ),
    DanceLesson(
        id = "toddler_butterfly_sway",
        title = "Butterfly Wing Flutter & Sway",
        ageCategory = "Ages 3–5",
        description = "Flutter your arms like magical butterfly wings and sway softly with the melody.",
        thumbnailUrl = "https://images.unsplash.com/photo-1508807526345-15e9b5f4eaff?w=600&auto=format&fit=crop&q=80",
        stage = "3-5",
        storyTitle = "The Butterfly Blossom Ball",
        storyContent = "The flower garden is waking up! Soft pink butterflies are flying from blossom to blossom. Put on your butterfly wings!",
        actionGuide = "Open your arms wide like soft butterfly wings. Float side to side on tip-toes, making gentle circles in the air.",
        visualPrompt = "Watch the Lottie butterfly flutter as you move your arms!",
        stickerTitle = "Garden Butterfly 🦋",
        stickerDescription = "Awarded for graceful arm movement (Port de bras) and light balance.",
        durationMinutes = 3,
        emoji = "🦋",
        musicTrack = "swan_lake",
        difficulty = "Toddler"
    ),
    DanceLesson(
        id = "toddler_mouse_plie",
        title = "Quiet Mouse Plié & Tip-Toe",
        ageCategory = "Ages 3–5",
        description = "Tip-toe quietly through the sleeping palace with soft knee bends (pliés).",
        thumbnailUrl = "https://images.unsplash.com/photo-1524594152303-9fd13543fe6e?w=600&auto=format&fit=crop&q=80",
        stage = "3-5",
        storyTitle = "Shh! The Mouse Palace Adventure",
        storyContent = "The Royal Castle is sleeping! Milo the Tiny Mouse must sneak past the sleeping dragon to fetch the golden cheese. Shh... keep your feet quiet!",
        actionGuide = "Bend your knees gently (Plié) and stay low to the floor like a quiet mouse, then tip-toe quietly on high relevé!",
        visualPrompt = "Keep your knees springy and quiet!",
        stickerTitle = "Quiet Mouse 🐭",
        stickerDescription = "Awarded for silent landing and soft plié knee bends.",
        durationMinutes = 4,
        emoji = "🐭",
        musicTrack = "bach_minuet",
        difficulty = "Toddler"
    )
)

val toddlerNarrativeSteps = mapOf(
    "toddler_garden_bunny" to listOf(
        ToddlerNarrativeStep(
            stepTitle = "Step 1: Point Your Bunny Whisker Toes",
            storyCharacter = "🐰",
            storyPrompt = "Twinkle Bunny stretches her toes in the morning sunshine! Can you point your toes out straight?",
            danceAction = "Stretch your right toe straight forward! Keep your knee straight and point down to the floor.",
            lottieAnimType = "sparkles",
            rewardStickerName = "Pointy Toe Badge 🩰"
        ),
        ToddlerNarrativeStep(
            stepTitle = "Step 2: Gentle Bunny Spring Hops",
            storyCharacter = "🥕",
            storyPrompt = "Twinkle sees a carrot high on a flower petal! Let's do 3 gentle, soft hops on the balls of our feet!",
            danceAction = "Bend your knees softly and pop up: Hop, Hop, Hop! Land quietly on your toes.",
            lottieAnimType = "starburst",
            rewardStickerName = "Bunny Hopper 🐰"
        )
    ),
    "toddler_butterfly_sway" to listOf(
        ToddlerNarrativeStep(
            stepTitle = "Step 1: Open Your Butterfly Wings",
            storyCharacter = "🦋",
            storyPrompt = "Flutter Fairy spreads her colorful wings! Open your arms wide and hold them round and soft.",
            danceAction = "Raise your arms out to the sides in a round circle (1st & 2nd position carriage).",
            lottieAnimType = "butterfly",
            rewardStickerName = "Fairy Wings 🌸"
        ),
        ToddlerNarrativeStep(
            stepTitle = "Step 2: Float Side to Side on Tip-Toes",
            storyCharacter = "🌸",
            storyPrompt = "A gentle spring breeze blows through the garden! Sway side to side while staying high on tip-toes.",
            danceAction = "Rise up on the balls of your feet and sway softly from side to side in a rhythm!",
            lottieAnimType = "starburst",
            rewardStickerName = "Blossom Sway 🌺"
        )
    ),
    "toddler_mouse_plie" to listOf(
        ToddlerNarrativeStep(
            stepTitle = "Step 1: Bend Low in Soft Mouse Plié",
            storyCharacter = "🐭",
            storyPrompt = "Milo Mouse sneaks through the quiet hallway! Bend your knees softly so nobody hears your feet.",
            danceAction = "Feet together, bend both knees outward softly while keeping your back straight and tall!",
            lottieAnimType = "sparkles",
            rewardStickerName = "Quiet Plié 🩰"
        ),
        ToddlerNarrativeStep(
            stepTitle = "Step 2: High Tip-Toe Mouse Walk",
            storyCharacter = "🧀",
            storyPrompt = "Milo reaches the golden cheese! Rise as high as you can on tip-toes and march quietly!",
            danceAction = "Rise on tip-toes and take 5 tiny, silent mouse steps forward!",
            lottieAnimType = "starburst",
            rewardStickerName = "Golden Cheese Hero 🧀"
        )
    )
)

/**
 * Screen UI that displays interactive, narrative-driven dance lessons
 * specifically designed for toddlers, featuring integrated Lottie animations.
 */
@Composable
fun ToddlerStoryDanceScreen(
    viewModel: DanceViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedLessonIndex by remember { mutableIntStateOf(0) }
    val currentLesson = toddlerLessonsList[selectedLessonIndex]
    val steps = toddlerNarrativeSteps[currentLesson.id] ?: emptyList()
    var currentStepIndex by remember { mutableIntStateOf(0) }

    val celebrationEvent by viewModel.celebrationEvent.collectAsState()
    var showMusicPlayer by remember { mutableStateOf(false) }
    var interactiveSparklesTrigger by remember { mutableStateOf(0) }

    // Character bounce loop animation for toddler delight
    val infiniteTransition = rememberInfiniteTransition(label = "toddler_char_bounce")
    val characterBounce by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "char_bounce"
    )

    // Lottie Composition Specs
    val starburstLottieComp by rememberLottieComposition(
        LottieCompositionSpec.JsonString("""
        {"v":"5.7.4","fr":30,"ip":0,"op":60,"w":200,"h":200,"nm":"StarBurst","layers":[{"ddd":0,"ind":1,"ty":4,"nm":"Star","ks":{"o":{"k":100},"r":{"k":180},"p":{"k":[100,100,0]},"s":{"k":[120,120,100]}},"shapes":[{"ty":"gr","it":[{"ty":"sr","sy":1,"pt":{"k":5},"p":{"k":[0,0]},"r":{"k":0},"ir":{"k":20},"is":{"k":0},"or":{"k":45},"os":{"k":0}},{"ty":"fl","c":{"k":[1,0.82,0.3,1]}}]}]}]}
        """.trimIndent())
    )
    val lottieProgress by animateLottieCompositionAsState(
        composition = starburstLottieComp,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF0F5), // Lavender blush
                        Color(0xFFFFF9E6), // Soft Buttercup
                        Color(0xFFE0F2FE)  // Pale Sky
                    )
                )
            )
            .testTag("toddler_story_dance_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .shadow(2.dp, CircleShape)
                        .testTag("btn_toddler_back")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF914D5D)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌸", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Toddler Story Dance Studio",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                    }
                    Text(
                        text = "Interactive narrative movement for ages 3–5",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                IconButton(
                    onClick = { showMusicPlayer = !showMusicPlayer },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Music Player",
                        tint = Color(0xFF914D5D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lesson Selection Carousel Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(toddlerLessonsList) { index, lesson ->
                    val isSelected = index == selectedLessonIndex
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedLessonIndex = index
                                currentStepIndex = 0
                                viewModel.synthesizer.playSparkleChime()
                            }
                            .testTag("toddler_lesson_tab_$index"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFFFD1DC) else Color.White
                        ),
                        border = BorderStrokeColors(
                            1.dp,
                            if (isSelected) Color(0xFF914D5D) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lesson.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = lesson.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) Color(0xFF914D5D) else Color(0xFF334155)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Interactive Toddler Storybook Card
            val activeStep = steps.getOrNull(currentStepIndex) ?: steps.firstOrNull()

            activeStep?.let { step ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
                    elevation = CardDefaults.cardElevation(6.dp),
                    border = BorderStrokeColors(1.dp, Color(0xFFFFD1DC))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Character & Lottie Animation Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFFF0F5), Color(0xFFFEF3C7))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Lottie Starburst Effect Background
                            LottieAnimation(
                                composition = starburstLottieComp,
                                progress = { lottieProgress },
                                modifier = Modifier.size(140.dp)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .shadow(3.dp, CircleShape)
                                        .offset(y = characterBounce.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(step.storyCharacter, fontSize = 36.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = step.stepTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Toddler Narrative Story Text
                        Text(
                            text = step.storyPrompt,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dance Movement Prompt Box
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x19FFD1DC)),
                            border = BorderStrokeColors(1.dp, Color(0x66FFD1DC))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Dance",
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Dance Action:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF914D5D)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = step.danceAction,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B),
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Step Progress Dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            steps.forEachIndexed { idx, _ ->
                                Box(
                                    modifier = Modifier
                                        .size(if (idx == currentStepIndex) 12.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (idx == currentStepIndex) Color(0xFF914D5D) else Color(0xFFCBD5E1)
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Call-and-Response Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    interactiveSparklesTrigger++
                                    viewModel.synthesizer.playSparkleChime()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("btn_toddler_practice_sound")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Sound",
                                    tint = Color(0xFF334155),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Chime ✨", color = Color(0xFF334155), fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.synthesizer.playCelebrationFanfare()
                                    if (currentStepIndex + 1 < steps.size) {
                                        currentStepIndex++
                                    } else {
                                        // Complete lesson in Room database
                                        viewModel.completeActiveLesson()
                                        viewModel.triggerMilestoneCelebration(
                                            milestoneTitle = "Toddler Story Mastery! 🏆",
                                            milestoneSubtext = "You finished ${currentLesson.title}! Awarded ${step.rewardStickerName}"
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1.4f)
                                    .height(50.dp)
                                    .testTag("btn_toddler_next_step")
                            ) {
                                Text(
                                    text = if (currentStepIndex + 1 < steps.size) "Next Step ➡️" else "Finish Story! 🌟",
                                    color = Color(0xFF914D5D),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Expandable Local Music Player
            if (showMusicPlayer) {
                Spacer(modifier = Modifier.height(12.dp))
                LocalMusicPlayerModal(
                    synthesizer = viewModel.synthesizer,
                    onTrackSelected = { track -> viewModel.selectMusicTrack(track) }
                )
            }
        }

        // Celebratory Lottie Overlay
        CelebrationLottieOverlay(
            celebrationData = celebrationEvent,
            onDismiss = { viewModel.dismissCelebrationAndGoHome() }
        )
    }
}
