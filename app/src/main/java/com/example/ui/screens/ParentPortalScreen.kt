package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.components.dpadFocusable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DanceViewModel
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue
import kotlin.random.Random

@Composable
fun ParentPortalScreen(viewModel: DanceViewModel) {
    val profile by viewModel.profileState.collectAsState()
    val completedLessons by viewModel.completedLessons.collectAsState()
    val earnedStickers by viewModel.earnedStickers.collectAsState()
    val isMusicEnabled by viewModel.isMusicEnabled.collectAsState()
    val isVoiceGuideEnabled by viewModel.isVoiceGuideEnabled.collectAsState()
    val isSpeaking by viewModel.isTtsSpeaking.collectAsState()

    var isUnlocked by remember { mutableStateOf(false) }
    var num1 by remember { mutableStateOf(Random.nextInt(5, 12)) }
    var num2 by remember { mutableStateOf(Random.nextInt(4, 9)) }
    var mathAnswer by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var editingName by remember { mutableStateOf(profile.name) }
    var reminderEnabled by remember(profile.dailyReminderEnabled) { mutableStateOf(profile.dailyReminderEnabled) }
    var reminderHour by remember(profile.dailyReminderHour) { mutableStateOf(profile.dailyReminderHour) }
    var reminderMinute by remember(profile.dailyReminderMinute) { mutableStateOf(profile.dailyReminderMinute) }

    if (!isUnlocked) {
        // Gated access challenge
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF9FB)) // Frosted Cream base
                .padding(24.dp)
                .testTag("parent_gate_screen"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security gate",
                        tint = Color(0xFF914D5D),
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Parental Verification",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "To access settings, please solve this simple puzzle to prove you are a grown-up!",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "What is $num1 + $num2?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF914D5D),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = mathAnswer,
                        onValueChange = {
                            mathAnswer = it
                            errorMessage = ""
                        },
                        label = { Text("Answer") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gate_answer_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD1DC),
                            focusedLabelColor = Color(0xFF914D5D)
                        ),
                        singleLine = true
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { viewModel.navigateTo("map") },
                            modifier = Modifier.testTag("gate_cancel_button")
                        ) {
                            Text("Go Back", color = Color(0xFF64748B))
                        }

                        Button(
                            onClick = {
                                val correctSum = num1 + num2
                                if (mathAnswer.trim().toIntOrNull() == correctSum) {
                                    isUnlocked = true
                                } else {
                                    errorMessage = "Oops! That's not quite right. Try again!"
                                    num1 = Random.nextInt(5, 12)
                                    num2 = Random.nextInt(4, 9)
                                    mathAnswer = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("gate_submit_button")
                        ) {
                            Text("Verify", color = Color(0xFF914D5D), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    } else {
        // Unlocked settings panel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF9FB)), // Frosted Cream base
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
                    .testTag("parent_portal_screen")
            ) {
            Text(
                text = "Parent & Teacher Portal",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )

            Text(
                text = "Configure curriculum adaptation, reminders, and learning progress.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Audio & Voice Accessibility (DataStore Settings)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_settings_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Audio Settings",
                            tint = Color(0xFF914D5D),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Audio & Voice Preferences",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Customize music playback and toddler-friendly voice-guided dance step instructions.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Background Music Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Music",
                                tint = LemonYellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Classical Background Music",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = if (isMusicEnabled) "Synthesized melodies enabled" else "Muted / Off",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Switch(
                            checked = isMusicEnabled,
                            onCheckedChange = { viewModel.setBackgroundMusicEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF914D5D)
                            ),
                            modifier = Modifier.testTag("toggle_background_music_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Voice Guide TTS Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Voice Guide",
                                tint = SkyBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Voice-Guided Instructions",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = if (isVoiceGuideEnabled) "Speaks ballet steps for toddlers" else "Silent instructions",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Switch(
                            checked = isVoiceGuideEnabled,
                            onCheckedChange = { viewModel.setVoiceGuideEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF914D5D)
                            ),
                            modifier = Modifier.testTag("toggle_voice_guide_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Global Day / Night Mode Theme Toggle
                    val isNightModeEnabled by viewModel.isNightModeEnabled.collectAsState()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isNightModeEnabled) "🌙" else "☀️",
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Day / Night Studio Theme",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = if (isNightModeEnabled) "Cozy Evening Twilight Canvas" else "Bright Daylight Dance Studio",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Switch(
                            checked = isNightModeEnabled,
                            onCheckedChange = { viewModel.setNightModeEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF6366F1)
                            ),
                            modifier = Modifier.testTag("toggle_night_mode_switch")
                        )
                    }


                    if (isVoiceGuideEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (isSpeaking) {
                                    viewModel.stopVoiceInstruction()
                                } else {
                                    viewModel.speakVoiceInstruction("Welcome little dancer! Point your toes and let's leap like a graceful swan!", force = true)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_voice_guide_button")
                        ) {
                            Text(
                                text = if (isSpeaking) "🛑 Stop Voice Preview" else "🔊 Test Voice Guide Coach",
                                color = Color(0xFF914D5D),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Curriculum stage selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Curriculum",
                            tint = Color(0xFF87CEEB), // SkyBlue
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Curriculum Stage of Growth",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Happy Dances adapts to the child's development. Selecting an age group alters instruction, metaphors, visual helpers, and technical focus.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val stages = listOf(
                        Triple("3-5", "The Imagination Stage", "Ages 3–5 • Uses creative metaphors like 'hide like a mouse' (Plié) and toe painting."),
                        Triple("6-8", "The Foundation Stage", "Ages 6–8 • Formal ballet terminologies with helpful footprint placement guides."),
                        Triple("9-12", "The Technique Stage", "Ages 9–12 • Builds strength & memory with Space Barre, Adagio extensions, and pose mirrors.")
                    )

                    stages.forEach { (age, title, desc) ->
                        val isSelected = profile.ageGroup == age
                        val bgTint = if (isSelected) Color(0x26FFD1DC) else Color.Transparent

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgTint)
                                .clickable { viewModel.changeAgeGroup(age) }
                                .padding(10.dp)
                                .testTag("stage_card_$age")
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.Star else Icons.Default.ChildCare,
                                    contentDescription = "Selected",
                                    tint = if (isSelected) Color(0xFF914D5D) else Color(0xFF64748B),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color(0xFF914D5D) else Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Daily Dance Reminder Nudges
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Reminder",
                                tint = Color(0xFF914D5D),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Daily Dance Nudge",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Friendly reminder to practice & streak",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = {
                                reminderEnabled = it
                                viewModel.updateDailyReminder(reminderHour, reminderMinute, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF914D5D)
                            ),
                            modifier = Modifier.testTag("daily_reminder_switch")
                        )
                    }

                    if (reminderEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Choose Daily Reminder Time:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val reminderTimeOptions = listOf(
                            Pair(10, 0) to "Morning (10:00 AM)",
                            Pair(16, 0) to "After School (4:00 PM)",
                            Pair(18, 30) to "Before Dinner (6:30 PM)"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            reminderTimeOptions.forEach { (time, label) ->
                                val (h, m) = time
                                val isSelected = reminderHour == h && reminderMinute == m

                                Button(
                                    onClick = {
                                        reminderHour = h
                                        reminderMinute = m
                                        viewModel.updateDailyReminder(h, m, true)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) Color(0xFFFFD1DC) else Color(0xFFF1F5F9)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${if (h > 12) h - 12 else h}:${if (m == 0) "00" else m} ${if (h >= 12) "PM" else "AM"}",
                                        color = if (isSelected) Color(0xFF914D5D) else Color(0xFF475569),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Test Nudge Trigger Button
                        Button(
                            onClick = { viewModel.triggerTestNotification() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("test_notification_button")
                        ) {
                            Text(
                                "🔔 Send Test Dance Nudge Notification",
                                color = Color(0xFF334155),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Personalization
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ChildCare,
                            contentDescription = "Profile",
                            tint = LemonYellow,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Dancer Profile",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editingName,
                        onValueChange = {
                            editingName = it
                            viewModel.updateDancerName(it)
                        },
                        label = { Text("Dancer's Nickname") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dancer_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD1DC),
                            focusedLabelColor = Color(0xFF914D5D)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 4: Room Database Session Completion Records
            val progressList by viewModel.progressRecords.collectAsState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("room_progress_history_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                border = BorderStrokeColors(1.dp, Color(0x33AEC6CF)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Progress Records",
                                tint = Color(0xFF1E40AF),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Local Room Progress Records",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDBEAFE))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${progressList.size} Sessions",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Real-time records saved locally via Room database tracking lesson completions, posture accuracy, stage, and duration.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (progressList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No dance sessions recorded yet. Start dancing to build your log!",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            progressList.takeLast(5).reversed().forEach { record ->
                                val dateStr = remember(record.timestamp) {
                                    java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
                                        .format(java.util.Date(record.timestamp))
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = record.lessonTitle,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = "Stage ${record.danceLevel} • ${record.sessionMode.replaceFirstChar { it.uppercase() }} • $dateStr",
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFDCFCE7))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${record.accuracyScore.toInt()}% Acc",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF15803D)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.navigateTo("intro_level") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Dance Level Selection Screen", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 5: Safety & Privacy Commitments
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = BorderStrokeColors(1.dp, Color.White.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Safety Pledge",
                            tint = Color(0xFF914D5D),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Public Good Safety Pledge",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val safetyPledges = listOf(
                        "🔒 100% Offline & Private: No personal data or camera telemetry ever leaves this device.",
                        "🚫 100% Free & No Ads: Happy Dances has no monetization, tracking, or network calls.",
                        "📚 Pedagogical Integrity: Curriculum crafted to inspire natural motor coordination, posture alignment, and ballet memory."
                    )

                    safetyPledges.forEach { pledge ->
                        Text(
                            text = pledge,
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.resetAllProgress() },
                    modifier = Modifier.testTag("reset_progress_button")
                ) {
                    Text("Reset All Progress", color = Color.Red, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.navigateTo("map") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("back_to_studio_button")
                ) {
                    Text("Back to Studio", color = Color(0xFF914D5D), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
        }
    }
}
