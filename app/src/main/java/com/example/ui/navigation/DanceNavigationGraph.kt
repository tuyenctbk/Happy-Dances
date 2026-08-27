package com.example.ui.navigation

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DanceViewModel
import com.example.ui.screens.LessonLibraryScreen
import com.example.ui.screens.StoryModeScreen
import com.example.ui.screens.ToddlerStoryDanceScreen
import com.example.ui.theme.BorderStrokeColors

enum class DanceCurriculumRoute(val hashKey: String, val title: String, val emoji: String) {
    TODDLER_STORY("toddler_story", "Toddler Storytelling", "🌸"),
    BALLET_BASICS("ballet_basics", "Ballet Basics", "🩰"),
    ACADEMY_TRAINING("academy_training", "Academy Training", "🚀")
}

/**
 * HashRouter / State-based navigation graph allowing smooth switching between
 * Toddler Storytelling, Ballet Basics, and Academy Training views.
 */
@Composable
fun DanceNavigationGraph(
    viewModel: DanceViewModel,
    initialRoute: DanceCurriculumRoute = DanceCurriculumRoute.TODDLER_STORY,
    onBackToMap: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeRoute by remember { mutableStateOf(initialRoute) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("dance_navigation_graph")
    ) {
        // HashRouter Navigation Bar Switcher
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp),
            border = BorderStrokeColors(1.dp, Color(0xFFE2E8F0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DanceCurriculumRoute.entries.forEach { route ->
                    val isSelected = activeRoute == route
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) {
                                    when (route) {
                                        DanceCurriculumRoute.TODDLER_STORY -> Color(0xFFFFD1DC)
                                        DanceCurriculumRoute.BALLET_BASICS -> Color(0xFFDBEAFE)
                                        DanceCurriculumRoute.ACADEMY_TRAINING -> Color(0xFFF3E8FF)
                                    }
                                } else Color.Transparent
                            )
                            .clickable {
                                activeRoute = route
                                viewModel.saveLastSessionState(
                                    lessonId = when (route) {
                                        DanceCurriculumRoute.TODDLER_STORY -> "toddler_garden_bunny"
                                        DanceCurriculumRoute.BALLET_BASICS -> "castle_pli"
                                        DanceCurriculumRoute.ACADEMY_TRAINING -> "moon_barre"
                                    },
                                    lessonTitle = route.title,
                                    moduleRoute = route.hashKey
                                )
                                viewModel.synthesizer.playSparkleChime()
                            }
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                            .testTag("nav_route_tab_${route.hashKey}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(route.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = route.title.split(" ").first(),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = if (isSelected) {
                                    when (route) {
                                        DanceCurriculumRoute.TODDLER_STORY -> Color(0xFF914D5D)
                                        DanceCurriculumRoute.BALLET_BASICS -> Color(0xFF1E40AF)
                                        DanceCurriculumRoute.ACADEMY_TRAINING -> Color(0xFF6B21A8)
                                    }
                                } else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }

        // Active View Container
        AnimatedContent(
            targetState = activeRoute,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.weight(1f),
            label = "route_transition"
        ) { route ->
            when (route) {
                DanceCurriculumRoute.TODDLER_STORY -> {
                    ToddlerStoryDanceScreen(
                        viewModel = viewModel,
                        onBack = onBackToMap
                    )
                }
                DanceCurriculumRoute.BALLET_BASICS -> {
                    LessonLibraryScreen(
                        viewModel = viewModel,
                        onSelectLesson = { lesson -> viewModel.selectDanceLesson(lesson) }
                    )
                }
                DanceCurriculumRoute.ACADEMY_TRAINING -> {
                    StoryModeScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
