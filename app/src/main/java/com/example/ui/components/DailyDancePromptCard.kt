package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.DanceViewModel
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue

@Composable
fun DailyDancePromptCard(
    viewModel: DanceViewModel,
    onStartPractice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profileState.collectAsState()
    val streakInfo by viewModel.streakState.collectAsState()
    val todayChallenge = viewModel.todayDailyChallenge
    val todayRecord by viewModel.todayDailyChallengeRecord.collectAsState()
    val isChallengeCompleted = todayRecord != null

    var showDailyChallengeDialog by remember { mutableStateOf(false) }
    var showPracticeTimerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDailyChallengeDialog) {
        DailyChallengeDialog(
            challenge = todayChallenge,
            viewModel = viewModel,
            isAlreadyCompletedToday = isChallengeCompleted,
            onDismiss = { showDailyChallengeDialog = false }
        )
    }

    if (showPracticeTimerDialog) {
        PracticeTimerDialog(
            initialSeconds = 120,
            title = stringResource(R.string.timer_session_heading),
            subtitle = stringResource(R.string.timer_focus_guide),
            onDismiss = { showPracticeTimerDialog = false },
            onPlayChime = { viewModel.synthesizer.playSparkleChime() }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_dance_prompt_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        border = BorderStrokeColors(1.5.dp, Color(0xFFFFD1DC)),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF0F5), Color.White)
                    )
                )
                .padding(18.dp)
        ) {
            // Header with Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD1DC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🩰", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.daily_prompt_title),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("✨", fontSize = 14.sp)
                        }
                        Text(
                            text = stringResource(R.string.daily_prompt_subtitle),
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Daily Streak Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFF3C4))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🔥 ${streakInfo.currentStreak} Days",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // DAILY CHALLENGE HIGHLIGHT CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDailyChallengeDialog = true }
                    .testTag("daily_challenge_highlight_banner"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChallengeCompleted) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                ),
                border = BorderStrokeColors(
                    1.2.dp,
                    if (isChallengeCompleted) Color(0xFF86EFAC) else Color(0xFFFDE68A)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isChallengeCompleted) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(todayChallenge.emoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.daily_challenge_tag),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isChallengeCompleted) Color(0xFF166534) else Color(0xFFB45309)
                            )
                            if (isChallengeCompleted) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("✅ Done", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                        }
                        Text(
                            text = todayChallenge.moveName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = todayChallenge.description,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1
                        )
                    }

                    Icon(
                        imageVector = if (isChallengeCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                        contentDescription = "Start Challenge",
                        tint = if (isChallengeCompleted) Color(0xFF16A34A) else Color(0xFFD97706),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Focus Timer & Daily Alert Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Focus Practice Timer launcher button
                OutlinedButton(
                    onClick = { showPracticeTimerDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("btn_open_practice_timer"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyBlue),
                    border = BorderStrokeColors(1.dp, SkyBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = SkyBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.timer_button_label), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Daily Dance Challenge popup button
                Button(
                    onClick = { showDailyChallengeDialog = true },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(42.dp)
                        .testTag("btn_open_daily_challenge_modal"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5286))
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Challenge",
                        tint = LemonYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isChallengeCompleted) stringResource(R.string.daily_challenge_review) else stringResource(R.string.daily_challenge_button),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Daily Reminder Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Reminder",
                        tint = Color(0xFFFF5286),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.daily_reminder_label),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = if (profile.dailyReminderEnabled) stringResource(R.string.daily_reminder_active) else stringResource(R.string.daily_reminder_inactive),
                            fontSize = 9.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Switch(
                    checked = profile.dailyReminderEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.updateDailyReminder(16, 0, enabled)
                        val msg = if (enabled) "Daily 4:00 PM reminder enabled! 🔔" else "Reminder turned off."
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFFF5286),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.testTag("daily_reminder_switch")
                )
            }
        }
    }
}
