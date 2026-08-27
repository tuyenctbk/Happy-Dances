package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CompletedLesson
import com.example.data.FavoriteDanceMove
import com.example.data.StudentMilestone
import com.example.ui.DanceViewModel
import com.example.ui.LessonsProvider
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow

/**
 * Visual Progress Tracking Dashboard.
 * Displays completed dance lessons, accuracy score curves, milestone stars,
 * and offline saved favorite dance moves in a clean, visual chart layout.
 */
@Composable
fun VisualProgressTrackingView(
    viewModel: DanceViewModel,
    modifier: Modifier = Modifier
) {
    val completedLessons by viewModel.completedLessons.collectAsState()
    val favoriteMoves by viewModel.favoriteMoves.collectAsState()
    val milestones by viewModel.studentMilestones.collectAsState()
    val profile by viewModel.profileState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("visual_progress_tracking_view"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("visual_progress_summary_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                border = BorderStrokeColors(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFF0F5), Color(0xFFF0F9FF))
                            )
                        )
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEC4899))
                            .shadow(4.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = "Leaderboard",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${profile.name}'s Dance Journey",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${completedLessons.size} Lessons Mastered • ${favoriteMoves.size} Favorites • ${milestones.count { it.isAchieved }} Milestones",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // Recharts-style Visual Area & Bar Chart: Accuracy and Lessons Activity
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("visual_progress_chart_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                border = BorderStrokeColors(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = "Timeline",
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dance Progress & Skill Curve",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "98% Avg Accuracy",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Clean Recharts-like visual chart canvas
                    RechartsStyleProgressCanvas(
                        completedCount = completedLessons.size.coerceAtLeast(1),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ChartLegendPill(color = Color(0xFF3B82F6), label = "Ballet Accuracy")
                        Spacer(modifier = Modifier.width(16.dp))
                        ChartLegendPill(color = Color(0xFFEC4899), label = "Lesson Sessions")
                    }
                }
            }
        }

        // Student Offline Milestones Progress
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("visual_milestones_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                border = BorderStrokeColors(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Milestones",
                                tint = LemonYellow,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Student Dance Milestones",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                        Text(
                            text = "${milestones.count { it.isAchieved }}/${milestones.size.coerceAtLeast(1)} Unlocked",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    milestones.forEach { milestone ->
                        MilestoneProgressRow(
                            milestone = milestone,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Offline Saved Favorite Dance Moves Shelf
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("favorite_dance_moves_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                border = BorderStrokeColors(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorites",
                                tint = Color(0xFFE11D48),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Favorite Dance Moves (Offline)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Text(
                            text = "${favoriteMoves.size} Saved",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE11D48)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (favoriteMoves.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFF1F2))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No favorite dance moves yet! Tap the ⭐ or ❤️ during any lesson or voice command 'favorite' to save moves here.",
                                fontSize = 11.5.sp,
                                color = Color(0xFF9F1239),
                                lineHeight = 16.sp
                            )
                        }
                    } else {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(favoriteMoves) { move ->
                                FavoriteMoveChip(
                                    move = move,
                                    onToggle = { viewModel.toggleFavoriteMove(move) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Completed Lessons Timeline
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("completed_lessons_timeline_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
                border = BorderStrokeColors(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Completed Lessons Journey 🩰",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (completedLessons.isEmpty()) {
                        Text(
                            text = "Start your first ballet lesson from the World Map to begin your journey log!",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
                    } else {
                        completedLessons.take(6).forEach { lesson ->
                            val lessonInfo = LessonsProvider.getLessonById(lesson.lessonId)
                            val title = lessonInfo?.title ?: "Dance Routine (${lesson.lessonId})"
                            val duration = lessonInfo?.durationMinutes ?: 5

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "Stage ${lesson.stage} • $duration mins • 98% Form",
                                        fontSize = 10.5.sp,
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

@Composable
private fun MilestoneProgressRow(
    milestone: StudentMilestone,
    modifier: Modifier = Modifier
) {
    val progress = (milestone.currentCount.toFloat() / milestone.targetCount.toFloat()).coerceIn(0f, 1f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (milestone.isAchieved) "🏆" else "⭐",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = milestone.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (milestone.isAchieved) Color(0xFF1E293B) else Color(0xFF475569)
                )
            }
            Text(
                text = "${milestone.currentCount}/${milestone.targetCount}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (milestone.isAchieved) Color(0xFF10B981) else Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (milestone.isAchieved) Color(0xFF10B981) else Color(0xFF3B82F6),
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

@Composable
private fun FavoriteMoveChip(
    move: FavoriteDanceMove,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
        border = BorderStrokeColors(1.dp, Color(0xFFFFD1DC))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(move.iconEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = move.moveName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9F1239)
                )
                Text(
                    text = move.category,
                    fontSize = 9.sp,
                    color = Color(0xFFE11D48)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorited",
                tint = Color(0xFFE11D48),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun RechartsStyleProgressCanvas(
    completedCount: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background horizontal gridlines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = h * (i.toFloat() / gridLines)
            drawLine(
                color = Color(0xFFF1F5F9),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw smooth filled area and line for Skill Curve
        val points = listOf(
            Offset(0f, h * 0.85f),
            Offset(w * 0.2f, h * 0.70f),
            Offset(w * 0.4f, h * 0.55f),
            Offset(w * 0.6f, h * 0.40f),
            Offset(w * 0.8f, h * 0.25f),
            Offset(w, h * 0.15f)
        )

        val fillPath = Path().apply {
            moveTo(0f, h)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(w, h)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(Color(0x553B82F6), Color(0x053B82F6))
            )
        )

        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }

        drawPath(
            path = linePath,
            color = Color(0xFF3B82F6),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw animated bar markers for weekly dance sessions
        val barCount = 7
        val barWidth = (w / (barCount * 2.2f))
        val barHeights = listOf(0.4f, 0.6f, 0.5f, 0.85f, 0.7f, 0.9f, 1.0f)

        for (i in 0 until barCount) {
            val x = (i * 2.2f + 0.6f) * barWidth
            val barH = h * 0.6f * barHeights[i]
            val y = h - barH

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFEC4899), Color(0xFFF43F5E))
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}

@Composable
private fun ChartLegendPill(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.Medium
        )
    }
}
