package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.example.ui.components.dpadFocusable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DanceViewModel
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue

data class DanceLevelOption(
    val levelKey: String,
    val title: String,
    val subtitle: String,
    val ageBadge: String,
    val emoji: String,
    val icon: ImageVector,
    val themeModeName: String,
    val description: String,
    val features: List<String>,
    val primaryColor: Color,
    val gradientColors: List<Color>,
    val defaultMode: String
)

@Composable
fun IntroLevelSelectionScreen(
    viewModel: DanceViewModel,
    modifier: Modifier = Modifier,
    onCompleted: (() -> Unit)? = null
) {
    val profile by viewModel.profileState.collectAsState()
    var selectedLevel by remember(profile.ageGroup) {
        mutableStateOf(if (profile.ageGroup.isNotEmpty()) profile.ageGroup else "3-5")
    }

    val levels = listOf(
        DanceLevelOption(
            levelKey = "3-5",
            title = "Toddler Play",
            subtitle = "Imagination Garden",
            ageBadge = "Ages 3–5",
            emoji = "🌸",
            icon = Icons.Default.AutoStories,
            themeModeName = "Storytelling Mode",
            description = "Playful, imaginative creative dance with animated storybook chapters, finger toe-painting, and gentle fairy tunes.",
            features = listOf(
                "📖 Storybook Chapter Quests",
                "🎨 Magic Floor Toe-Painting",
                "🦋 Butterfly & Mouse Animal Steps",
                "✨ Whimsical Pastel Theme"
            ),
            primaryColor = Color(0xFF914D5D),
            gradientColors = listOf(Color(0xFFFFF0F5), Color(0xFFFFD1DC), Color(0xFFFFF9C4)),
            defaultMode = "storytelling"
        ),
        DanceLevelOption(
            levelKey = "6-8",
            title = "Foundation Ballet",
            subtitle = "Royal Ballroom",
            ageBadge = "Ages 6–8",
            emoji = "👑",
            icon = Icons.Default.Spa,
            themeModeName = "Classical Foundations",
            description = "Step-by-step classical ballet fundamentals with alignment foot placement guides, turnouts, and graceful port de bras.",
            features = listOf(
                "🩰 1st & 2nd Turnout Placement Guide",
                "🏛️ Classical Port de Bras Stances",
                "⭐ Relevé & Tendu Foot Mastery",
                "🎵 Swan Lake Orchestral Melodies"
            ),
            primaryColor = Color(0xFF1E40AF),
            gradientColors = listOf(Color(0xFFF0F9FF), Color(0xFFAEC6CF), Color(0xFFFFE4E6)),
            defaultMode = "academy"
        ),
        DanceLevelOption(
            levelKey = "9-12",
            title = "Junior Ballet",
            subtitle = "Technical Academy",
            ageBadge = "Ages 9–12",
            emoji = "🚀",
            icon = Icons.Default.School,
            themeModeName = "Technical Academy",
            description = "Structured classical technique, adagio extensions, allegro leaps, and live camera mirror posture tracker.",
            features = listOf(
                "🪞 Live Camera Mirror Posture Tracker",
                "⚡ Grand Allegro & Adagio Combinations",
                "⏱️ Studio Tempo BPM & Accuracy Metrics",
                "🌌 Midnight Academy Pro Theme"
            ),
            primaryColor = Color(0xFF0F172A),
            gradientColors = listOf(Color(0xFFF8FAFC), Color(0xFFCBD5E1), Color(0xFFE2E8F0)),
            defaultMode = "academy"
        )
    )

    val infiniteTransition = rememberInfiniteTransition(label = "intro_sparkle")
    val sparkleAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF5F7),
                        Color(0xFFF8FAFC),
                        Color(0xFFEDF2F7)
                    )
                )
            )
            .testTag("intro_level_selection_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header Icon & Title
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(BalletPink.copy(alpha = 0.8f))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🩰", fontSize = 34.sp)
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = LemonYellow,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .graphicsLayer(rotationZ = sparkleAngle)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Welcome to Happy Dances",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pick your dancer's age & experience to personalize lessons, coaching, and studio themes.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Cards for each level
            levels.forEach { level ->
                val isSelected = selectedLevel == level.levelKey

                val cardBorderColor by animateColorAsState(
                    targetValue = if (isSelected) level.primaryColor else Color.White.copy(alpha = 0.7f),
                    animationSpec = tween(300),
                    label = "border_color"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            selectedLevel = level.levelKey
                            viewModel.synthesizer.playSparkleChime()
                        }
                        .border(
                            BorderStrokeColors(
                                if (isSelected) 2.5.dp else 1.dp,
                                cardBorderColor
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("level_card_${level.levelKey}")
                        .dpadFocusable(RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f)
                    ),
                    elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(level.gradientColors)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(level.emoji, fontSize = 24.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = level.title,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isSelected) level.primaryColor.copy(alpha = 0.12f)
                                                    else Color(0xFFF1F5F9)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = level.ageBadge,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) level.primaryColor else Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    Text(
                                        text = level.subtitle + " • " + level.themeModeName,
                                        fontSize = 12.sp,
                                        color = if (isSelected) level.primaryColor else Color(0xFF64748B),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = level.primaryColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = level.description,
                            fontSize = 12.5.sp,
                            color = Color(0xFF475569),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Features List
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            level.features.forEach { feature ->
                                Text(
                                    text = feature,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF334155),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm Button
            Button(
                onClick = {
                    viewModel.selectLevelAndMode(selectedLevel)
                    onCompleted?.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .testTag("btn_confirm_level_selection")
                    .dpadFocusable(RoundedCornerShape(18.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (selectedLevel) {
                        "3-5" -> BalletPink
                        "6-8" -> Color(0xFF2563EB)
                        else -> Color(0xFF0F172A)
                    }
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = LemonYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter Studio Experience ✨",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedLevel == "3-5") Color(0xFF914D5D) else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
