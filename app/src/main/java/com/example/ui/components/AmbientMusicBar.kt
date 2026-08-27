package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AudioSynthesizer
import com.example.ui.MusicTrackCatalog
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue

/**
 * Child-friendly Ambient Background Music Player Bar.
 * Provides looping ambient classical music with simple play/pause toggle and track switching.
 */
@Composable
fun AmbientMusicBar(
    synthesizer: AudioSynthesizer,
    onTrackSelected: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true
) {
    val currentTrackId by synthesizer.currentTrackId.collectAsState()
    val isPlaying by synthesizer.isPlaying.collectAsState()
    var showTrackSelector by remember { mutableStateOf(false) }

    val activeTrack = remember(currentTrackId) {
        MusicTrackCatalog.getTrackById(currentTrackId)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "music_pulse")
    val noteRotation by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "note_rot"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ambient_music_player_bar"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E1E24).copy(alpha = 0.92f) else Color.White.copy(alpha = 0.95f)
        ),
        border = BorderStrokeColors(
            1.5.dp,
            if (isPlaying) LemonYellow.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Play / Pause Master Button
                IconButton(
                    onClick = { synthesizer.togglePlayback() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) Color(0xFFEF4444) else SkyBlue)
                        .testTag("ambient_music_play_pause_toggle")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) stringResource(R.string.audio_pause) else stringResource(R.string.audio_play),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Track Information & Looping Badge
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTrackSelector = !showTrackSelector }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(activeTrack.emoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activeTrack.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = activeTrack.composer,
                            fontSize = 11.sp,
                            color = if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color(0xFF64748B),
                            maxLines = 1
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(LemonYellow.copy(alpha = 0.25f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = "Looping",
                                    tint = LemonYellow,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (isPlaying) stringResource(R.string.audio_looping_active) else stringResource(R.string.audio_loop_ready),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) LemonYellow else Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Music equalizing indicator or Track list expander
                IconButton(
                    onClick = { showTrackSelector = !showTrackSelector },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) Color.White.copy(alpha = 0.12f) else BalletPink.copy(alpha = 0.5f))
                        .testTag("ambient_music_expand_selector")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                        contentDescription = "Select Ambient Track",
                        tint = if (isPlaying) LemonYellow else (if (isDarkTheme) Color.White else Color(0xFF914D5D)),
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(if (isPlaying) noteRotation else 0f)
                    )
                }
            }

            // Quick track selection row
            AnimatedVisibility(
                visible = showTrackSelector,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = stringResource(R.string.audio_choose_melody),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(MusicTrackCatalog.tracks) { track ->
                            val isSelected = track.id == currentTrackId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) BalletPink else (if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color(0xFFF1F5F9))
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) LemonYellow else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onTrackSelected(track.id)
                                        synthesizer.setTrackAndPlay(track.id)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("ambient_track_${track.id}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(track.emoji, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = track.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF914D5D) else (if (isDarkTheme) Color.White else Color(0xFF1E293B))
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
