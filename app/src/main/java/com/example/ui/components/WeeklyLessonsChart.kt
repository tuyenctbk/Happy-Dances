package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyDanceSession
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyLessonBarPoint(
    val dayLabel: String,       // e.g. "Mon", "Tue"
    val dateStr: String,        // e.g. "2026-08-24"
    val completedCount: Int,    // lessons completed on this day
    val isToday: Boolean = false
)

@Composable
fun WeeklyLessonsChart(
    dailySessions: List<DailyDanceSession>,
    modifier: Modifier = Modifier
) {
    val weekPoints = remember(dailySessions) {
        getLessonsPerDayThisWeek(dailySessions)
    }

    var selectedDay by remember {
        mutableStateOf(weekPoints.find { it.isToday } ?: weekPoints.lastOrNull())
    }

    val totalWeeklyCompleted = remember(weekPoints) {
        weekPoints.sumOf { it.completedCount }
    }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    val maxCount = remember(weekPoints) {
        val maxVal = weekPoints.maxOfOrNull { it.completedCount } ?: 1
        maxOf(maxVal, 4) // fixed scale height for clean proportion
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_lessons_chart_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        border = BorderStrokeColors(1.dp, Color(0xFFFFD1DC)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFE4EC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Weekly Chart",
                            tint = Color(0xFFFF5286),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Weekly Dance Progress",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Lessons completed per day",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Total summary badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF0F5))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Check",
                            tint = Color(0xFFFF5286),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalWeeklyCompleted Lessons",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF914D5D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected day info bar
            selectedDay?.let { day ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 ${day.dayLabel} (${day.dateStr}):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = if (day.completedCount > 0) "🎉 ${day.completedCount} lessons completed!" else "🩰 Ready to dance today!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (day.completedCount > 0) Color(0xFFFF5286) else Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bar Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 24f
                    val topPadding = 20f
                    val chartHeight = h - bottomPadding - topPadding

                    val count = weekPoints.size
                    if (count == 0) return@Canvas

                    val barSpacing = w / count
                    val barWidth = (barSpacing * 0.48f).coerceIn(16f, 32f)

                    // Baseline
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(0f, topPadding + chartHeight),
                        end = Offset(w, topPadding + chartHeight),
                        strokeWidth = 1.5f
                    )

                    weekPoints.forEachIndexed { index, point ->
                        val centerX = (index * barSpacing) + (barSpacing / 2f)
                        val ratio = (point.completedCount.toFloat() / maxCount).coerceIn(0.06f, 1f)
                        val animatedBarHeight = chartHeight * ratio * animProgress.value
                        val barTopY = topPadding + (chartHeight - animatedBarHeight)

                        val isSelected = selectedDay?.dateStr == point.dateStr

                        val barBrush = if (isSelected || point.isToday) {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFF80AB), Color(0xFFFF4081)),
                                startY = barTopY,
                                endY = topPadding + chartHeight
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF87CEEB), Color(0xFF4FC3F7)),
                                startY = barTopY,
                                endY = topPadding + chartHeight
                            )
                        }

                        // Pillar shadow track
                        drawRoundRect(
                            color = Color(0xFFF1F5F9),
                            topLeft = Offset(centerX - barWidth / 2f, topPadding),
                            size = Size(barWidth, chartHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )

                        // Data bar
                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(centerX - barWidth / 2f, barTopY),
                            size = Size(barWidth, animatedBarHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )

                        // Star on top of bar if completed
                        if (point.completedCount > 0) {
                            drawCircle(
                                color = Color(0xFFFFD54F),
                                radius = 4f,
                                center = Offset(centerX, barTopY + 4f)
                            )
                        }
                    }
                }

                // Interactive tap area for each day bar
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weekPoints.forEach { point ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { selectedDay = point },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = point.dayLabel,
                                fontSize = 10.sp,
                                fontWeight = if (selectedDay?.dateStr == point.dateStr) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (selectedDay?.dateStr == point.dateStr) Color(0xFFFF5286) else Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getLessonsPerDayThisWeek(dailySessions: List<DailyDanceSession>): List<DailyLessonBarPoint> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayLabelFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val sessionsMap = dailySessions.associateBy({ it.date }, { it })

    val todayStr = dateFormat.format(Date())
    val cal = Calendar.getInstance()
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val list = mutableListOf<DailyLessonBarPoint>()
    for (i in 0 until 7) {
        val dateStr = dateFormat.format(cal.time)
        val dayLabel = dayLabelFormat.format(cal.time)
        val session = sessionsMap[dateStr]
        val completed = session?.sessionCount ?: 0

        list.add(
            DailyLessonBarPoint(
                dayLabel = dayLabel,
                dateStr = dateStr,
                completedCount = completed,
                isToday = dateStr == todayStr
            )
        )
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }

    // Default mock distribution if newly installed so the chart looks vibrant
    if (dailySessions.isEmpty()) {
        return list.mapIndexed { idx, point ->
            val mockCount = when (idx) {
                0 -> 1
                1 -> 2
                2 -> 1
                3 -> 0
                4 -> 2
                5 -> 1
                else -> 0
            }
            point.copy(completedCount = mockCount)
        }
    }

    return list
}
