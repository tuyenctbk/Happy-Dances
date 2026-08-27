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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyDanceSession
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeeklyTimePoint(
    val weekLabel: String,
    val totalMinutes: Int,
    val sessionCount: Int,
    val startDateStr: String,
    val isCurrentWeek: Boolean = false
)

@Composable
fun WeeklyDanceTimeChart(
    dailySessions: List<DailyDanceSession>,
    modifier: Modifier = Modifier
) {
    val weeklyData = remember(dailySessions) {
        calculateWeeklyBuckets(dailySessions)
    }

    var selectedWeek by remember { mutableStateOf<WeeklyTimePoint?>(weeklyData.lastOrNull()) }

    // Chart enter animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }

    val maxMinutes = remember(weeklyData) {
        val calculatedMax = weeklyData.maxOfOrNull { it.totalMinutes } ?: 15
        maxOf(calculatedMax, 25) // minimum scale height for clean visual proportion
    }

    val totalPracticedMinutes = remember(weeklyData) {
        weeklyData.sumOf { it.totalMinutes }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_dance_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
        border = BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header with stats summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x26FFD1DC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Chart",
                            tint = Color(0xFF914D5D),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Weekly Dance Time",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Week-by-week practice consistency",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Total summary badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⏱️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${totalPracticedMinutes}m Total",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active week detail pill
            selectedWeek?.let { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0x19FFD1DC), Color(0x1987CEEB))))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 ${week.weekLabel} (${week.startDateStr}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "✨ ${week.totalMinutes} mins • ${week.sessionCount} sessions",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF914D5D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // D3 / Recharts-style Smooth Visualizer Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 28f
                    val topPadding = 20f
                    val chartHeight = h - bottomPadding - topPadding

                    val count = weeklyData.size
                    if (count == 0) return@Canvas

                    val barSpacing = w / count
                    val barWidth = (barSpacing * 0.52f).coerceIn(18f, 44f)

                    // Draw subtle horizontal grid lines (D3 style)
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = topPadding + chartHeight * (i.toFloat() / gridLines)
                        drawLine(
                            color = Color(0xFFE2E8F0).copy(alpha = 0.8f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Build area path for continuous trend fill (Recharts Area style)
                    val areaPath = Path()
                    val linePath = Path()
                    val points = mutableListOf<Offset>()

                    weeklyData.forEachIndexed { index, point ->
                        val centerX = (index * barSpacing) + (barSpacing / 2f)
                        val barRatio = (point.totalMinutes.toFloat() / maxMinutes).coerceIn(0.04f, 1.0f)
                        val animatedHeight = chartHeight * barRatio * animProgress.value
                        val barTopY = topPadding + (chartHeight - animatedHeight)
                        val pointOffset = Offset(centerX, barTopY)
                        points.add(pointOffset)

                        // Render gradient bar with rounded caps
                        val isSelected = selectedWeek?.weekLabel == point.weekLabel
                        val barBrush = if (isSelected) {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFF80AB), Color(0xFFFF4081)),
                                startY = barTopY,
                                endY = topPadding + chartHeight
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF87CEEB), Color(0xFF5C6BC0)),
                                startY = barTopY,
                                endY = topPadding + chartHeight
                            )
                        }

                        // Bar background pillar
                        drawRoundRect(
                            color = Color(0xFFF1F5F9).copy(alpha = 0.6f),
                            topLeft = Offset(centerX - barWidth / 2f, topPadding),
                            size = Size(barWidth, chartHeight),
                            cornerRadius = CornerRadius(10f, 10f)
                        )

                        // Active animated data bar
                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(centerX - barWidth / 2f, barTopY),
                            size = Size(barWidth, animatedHeight),
                            cornerRadius = CornerRadius(10f, 10f)
                        )

                        // Draw top cap glow if positive
                        if (point.totalMinutes > 0) {
                            drawCircle(
                                color = if (isSelected) Color(0xFFFFD54F) else Color.White,
                                radius = 4f,
                                center = Offset(centerX, barTopY + 5f)
                            )
                        }
                    }

                    // Draw smooth spline connection line across the tops
                    if (points.isNotEmpty()) {
                        linePath.moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val cx = (p0.x + p1.x) / 2f
                            linePath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }

                        drawPath(
                            path = linePath,
                            color = Color(0xFF914D5D).copy(alpha = 0.4f * animProgress.value),
                            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                        )
                    }
                }

                // Interactive tap overlay row for each week
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weeklyData.forEach { week ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { selectedWeek = week },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = week.weekLabel,
                                fontSize = 10.sp,
                                fontWeight = if (selectedWeek?.weekLabel == week.weekLabel) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedWeek?.weekLabel == week.weekLabel) Color(0xFF914D5D) else Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Positive consistency tip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Trend",
                    tint = Color(0xFF10B981), // Emerald
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val streakTrendText = if (totalPracticedMinutes > 0) {
                    "Awesome routine! Even 3 minutes of daily dancing builds flexibility, balance, and memory!"
                } else {
                    "Start your first session today to watch your weekly consistency graph rise!"
                }
                Text(
                    text = streakTrendText,
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

private fun calculateWeeklyBuckets(sessions: List<DailyDanceSession>): List<WeeklyTimePoint> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val shortDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val sessionsMap = sessions.associateBy({ it.date }, { it })

    val result = mutableListOf<WeeklyTimePoint>()
    val cal = Calendar.getInstance()

    // Align to Monday of current week
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    // Generate last 5 weeks (W1 = 4 weeks ago, W5 = current week)
    val weekStarts = mutableListOf<Calendar>()
    for (i in 4 downTo 0) {
        val weekCal = cal.clone() as Calendar
        weekCal.add(Calendar.WEEK_OF_YEAR, -i)
        weekStarts.add(weekCal)
    }

    weekStarts.forEachIndexed { index, startOfWeekCal ->
        var totalMinutes = 0
        var totalSessions = 0

        val tempDay = startOfWeekCal.clone() as Calendar
        val startDateStr = shortDateFormat.format(tempDay.time)

        for (day in 0 until 7) {
            val dateStr = dateFormat.format(tempDay.time)
            sessionsMap[dateStr]?.let { session ->
                totalMinutes += if (session.totalMinutes > 0) session.totalMinutes else (session.sessionCount * 4)
                totalSessions += session.sessionCount
            }
            tempDay.add(Calendar.DAY_OF_YEAR, 1)
        }

        // If newly installed and empty, provide friendly starter points for aesthetics
        val displayMinutes = if (sessions.isEmpty()) {
            when (index) {
                0 -> 8
                1 -> 12
                2 -> 15
                3 -> 18
                else -> 5
            }
        } else {
            totalMinutes
        }

        val displaySessions = if (sessions.isEmpty()) {
            when (index) {
                0 -> 2
                1 -> 3
                2 -> 4
                3 -> 4
                else -> 1
            }
        } else {
            totalSessions
        }

        val label = if (index == 4) "This Wk" else "Wk ${index + 1}"
        result.add(
            WeeklyTimePoint(
                weekLabel = label,
                totalMinutes = displayMinutes,
                sessionCount = displaySessions,
                startDateStr = startDateStr,
                isCurrentWeek = index == 4
            )
        )
    }

    return result
}
