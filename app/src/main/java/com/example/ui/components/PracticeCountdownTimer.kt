package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue
import kotlinx.coroutines.delay

/**
 * Visual countdown timer designed specifically for children's dance exercises.
 * Helps kids maintain focus during individual practice routines, stretches, and balances.
 */
@Composable
fun VisualPracticeTimer(
    initialSeconds: Int = 60,
    onTimerFinished: () -> Unit = {},
    onPlayChime: () -> Unit = {},
    modifier: Modifier = Modifier,
    sizeDp: Dp = 160.dp,
    showControls: Boolean = true,
    showPresets: Boolean = true
) {
    var totalSeconds by remember { mutableIntStateOf(initialSeconds) }
    var secondsRemaining by remember { mutableIntStateOf(initialSeconds) }
    var isRunning by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    // Pulse animation when timer is ticking
    val infiniteTransition = rememberInfiniteTransition(label = "timer_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRunning) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Countdown tick effect
    LaunchedEffect(isRunning, secondsRemaining) {
        if (isRunning && secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
            if (secondsRemaining == 0) {
                isRunning = false
                isCompleted = true
                onPlayChime()
                onTimerFinished()
            }
        }
    }

    val progress = if (totalSeconds > 0) {
        (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier.testTag("visual_practice_timer"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Circular Graphic Timer Canvas
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(sizeDp)
                .scale(pulseScale)
        ) {
            Canvas(modifier = Modifier.size(sizeDp)) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val centerOffset = Offset(size.width / 2f, size.height / 2f)

                // Background track circle
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f),
                    radius = radius,
                    center = centerOffset,
                    style = Stroke(width = strokeWidth)
                )

                // Progress Arc
                val startAngle = -90f
                val sweepAngle = 360f * progress

                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            LemonYellow,
                            BalletPink,
                            SkyBlue,
                            LemonYellow
                        )
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(radius * 2, radius * 2),
                    topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)
                )
            }

            // Inner Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isCompleted) {
                    Text("🌟", fontSize = (sizeDp.value * 0.22f).sp)
                    Text(
                        text = stringResource(R.string.timer_completed_star),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LemonYellow,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.HourglassBottom else Icons.Default.HourglassTop,
                        contentDescription = "Timer icon",
                        tint = if (isRunning) LemonYellow else Color.White,
                        modifier = Modifier.size((sizeDp.value * 0.16f).dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formattedTime,
                        fontSize = (sizeDp.value * 0.22f).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (isRunning) stringResource(R.string.timer_focus_dancing) else stringResource(R.string.timer_tap_to_start),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isRunning) LemonYellow else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        if (showControls) {
            Spacer(modifier = Modifier.height(14.dp))

            // Main Control Buttons (Play/Pause, Reset, +30s)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                IconButton(
                    onClick = {
                        isRunning = false
                        isCompleted = false
                        secondsRemaining = totalSeconds
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("timer_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.timer_action_reset),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Play / Pause Toggle Button
                Button(
                    onClick = {
                        if (isCompleted) {
                            isCompleted = false
                            secondsRemaining = totalSeconds
                            isRunning = true
                        } else {
                            isRunning = !isRunning
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) Color(0xFFEF4444) else SkyBlue
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("timer_toggle_play_button")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) stringResource(R.string.audio_pause) else stringResource(R.string.audio_play),
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRunning) stringResource(R.string.audio_pause) else stringResource(R.string.timer_start_session),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Quick +30s Add Button
                IconButton(
                    onClick = {
                        secondsRemaining += 30
                        totalSeconds += 30
                        isCompleted = false
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("timer_add_30s_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("+30s", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Preset Duration Selector Chips (30s, 1m, 2m, 3m, 5m)
        if (showPresets) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    30 to "30s",
                    60 to "1 min",
                    120 to "2 min",
                    180 to "3 min",
                    300 to "5 min"
                ).forEach { (secs, label) ->
                    val isSelected = totalSeconds == secs
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) BalletPink else Color.White.copy(alpha = 0.15f)
                            )
                            .clickable {
                                isRunning = false
                                isCompleted = false
                                totalSeconds = secs
                                secondsRemaining = secs
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("timer_preset_${secs}s")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFF914D5D) else Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Full-Screen / Modal Dialog for Dance Practice Countdown Timer.
 */
@Composable
fun PracticeTimerDialog(
    initialSeconds: Int = 120,
    title: String = "Dance Practice Focus Timer 🩰",
    subtitle: String = "Stay focused, point your toes, and keep dancing until the star sparkles!",
    onDismiss: () -> Unit,
    onPlayChime: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
            border = BorderStrokeColors(2.dp, LemonYellow.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("practice_timer_modal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⏳", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Text("✕", color = Color.White, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                VisualPracticeTimer(
                    initialSeconds = initialSeconds,
                    onPlayChime = onPlayChime,
                    sizeDp = 170.dp,
                    showControls = true,
                    showPresets = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = BalletPink),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = stringResource(R.string.close_action),
                        color = Color(0xFF914D5D),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
