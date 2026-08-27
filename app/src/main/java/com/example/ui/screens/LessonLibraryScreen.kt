package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.components.dpadFocusable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DanceLesson
import com.example.data.DanceLessonEntity
import com.example.ui.DanceViewModel
import com.example.ui.components.CelebrationLottieOverlay
import com.example.ui.theme.BalletPink
import com.example.ui.theme.CheerfulMint
import com.example.ui.theme.CheerfulPrimaryPink
import com.example.ui.theme.CheerfulSecondaryBlue
import com.example.ui.theme.CheerfulTertiaryYellow
import com.example.ui.theme.CreamBackground
import com.example.ui.theme.DeeperPinkText
import com.example.ui.theme.FrostedGlassWhite
import com.example.ui.theme.GlassBorderPink
import com.example.ui.theme.GlassBorderWhite
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SoftPink
import com.example.ui.theme.TextCharcoal
import com.example.ui.theme.TextGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LessonLibraryScreen(
    viewModel: DanceViewModel,
    onSelectLesson: (DanceLesson) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedLessonIds by viewModel.completedLessonIds.collectAsState()
    val celebrationEvent by viewModel.celebrationEvent.collectAsState()
    val lessonEntities by viewModel.allDanceLessonEntities.collectAsState()
    val favoriteLessonIds by viewModel.favoriteLessonIds.collectAsState()
    val isSpeaking by viewModel.isTtsSpeaking.collectAsState()

    // Lottie Celebration Dialog Trigger
    CelebrationLottieOverlay(
        celebrationData = celebrationEvent,
        onDismiss = { viewModel.dismissCelebrationAndGoHome() }
    )
    val progressRecords by viewModel.progressRecords.collectAsState()
    val allLessons = viewModel.allDanceLessons

    var searchQuery by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("All") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    val difficultyLevels = listOf("All", "Toddler", "Ballet Basics", "Academy", "Favorites")
    val statusFilters = listOf("All", "Completed", "In Progress", "Not Started")

    val filteredLessons = allLessons.filter { lesson ->
        val matchesSearch = searchQuery.isBlank() ||
                lesson.title.contains(searchQuery, ignoreCase = true) ||
                lesson.description.contains(searchQuery, ignoreCase = true) ||
                lesson.storyTitle.contains(searchQuery, ignoreCase = true)

        val matchesDifficulty = when (selectedDifficulty) {
            "Toddler" -> lesson.stage == "3-5" || lesson.ageCategory == "Ages 3–5"
            "Ballet Basics" -> lesson.stage == "6-8" || lesson.ageCategory == "Ages 6–8"
            "Academy" -> lesson.stage == "9-12" || lesson.ageCategory == "Ages 9–12"
            "Favorites" -> favoriteLessonIds.contains(lesson.id)
            else -> true
        }

        val entity = lessonEntities.find { it.id == lesson.id }
        val dbStatus = entity?.completionStatus ?: "Not Started"

        val matchesStatus = when (selectedStatusFilter) {
            "Completed" -> dbStatus == "Completed"
            "In Progress" -> dbStatus == "In Progress"
            "Not Started" -> dbStatus == "Not Started"
            else -> true
        }

        matchesSearch && matchesDifficulty && matchesStatus
    }

    val totalCompletedCount = completedLessonIds.size
    val totalCount = allLessons.size
    val progressPercentage = if (totalCount > 0) totalCompletedCount.toFloat() / totalCount else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CreamBackground,
                        SoftPink.copy(alpha = 0.5f),
                        CreamBackground
                    )
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 900.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header & Title
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Dance Lesson Library",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextCharcoal
                            )
                            Text(
                                text = "Discover playful ballet & movement journeys",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = CheerfulPrimaryPink.copy(alpha = 0.15f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🩰", fontSize = 24.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Overview Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = FrostedGlassWhite
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GlassBorderWhite, GlassBorderPink))),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("progress_overview_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = CheerfulTertiaryYellow.copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = "⭐", fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Your Dance Journey Progress",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = TextCharcoal
                                        )
                                        Text(
                                            text = "$totalCompletedCount of $totalCount lessons completed",
                                            fontSize = 12.sp,
                                            color = TextGray
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (totalCompletedCount > 0) CheerfulMint.copy(alpha = 0.2f) else SoftPink,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(
                                        text = "${(progressPercentage * 100).toInt()}%",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = if (totalCompletedCount > 0) Color(0xFF047857) else DeeperPinkText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { progressPercentage },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = CheerfulPrimaryPink,
                                trackColor = SoftPink
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search TextField
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lesson_search_field"),
                        placeholder = { Text("Search lessons, stories, or ballet steps...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = CheerfulPrimaryPink
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = TextGray
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FrostedGlassWhite,
                            unfocusedContainerColor = FrostedGlassWhite,
                            focusedBorderColor = CheerfulPrimaryPink,
                            unfocusedBorderColor = GlassBorderPink
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Difficulty Category Filter Chips
                    Text(
                        text = "Difficulty & Categories",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextCharcoal,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        difficultyLevels.forEach { level ->
                            val isSelected = selectedDifficulty == level
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedDifficulty = level },
                                label = {
                                    Text(
                                        text = when (level) {
                                            "All" -> "🌟 All Dances"
                                            "Toddler" -> "👶 Toddler (Ages 3–5)"
                                            "Ballet Basics" -> "🩰 Ballet Basics (Ages 6–8)"
                                            "Academy" -> "🚀 Academy (Ages 9–12)"
                                            "Favorites" -> "❤️ Favorites"
                                            else -> level
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (level == "Favorites") Color(0xFFE11D48) else CheerfulPrimaryPink,
                                    selectedLabelColor = Color.White,
                                    containerColor = FrostedGlassWhite,
                                    labelColor = TextCharcoal
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = GlassBorderPink,
                                    selectedBorderColor = if (level == "Favorites") Color(0xFFE11D48) else CheerfulPrimaryPink
                                ),
                                modifier = Modifier.testTag("filter_level_${level.replace(" ", "_")}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Filter Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Status:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = TextGray
                        )
                        statusFilters.forEach { status ->
                            val isSelected = selectedStatusFilter == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedStatusFilter = status },
                                label = {
                                    Text(
                                        text = when (status) {
                                            "Completed" -> "✓ Completed"
                                            "In Progress" -> "🩰 In Progress"
                                            "Not Started" -> "🎯 New"
                                            else -> "All Status"
                                        },
                                        fontSize = 11.sp
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CheerfulSecondaryBlue,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.6f),
                                    labelColor = TextCharcoal
                                ),
                                modifier = Modifier.testTag("filter_status_$status")
                            )
                        }
                    }
                }
            }

            // Results count
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${filteredLessons.size} Lessons Available",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextCharcoal
                    )
                    if (searchQuery.isNotEmpty() || selectedDifficulty != "All" || selectedStatusFilter != "All") {
                        Text(
                            text = "Reset Filters",
                            color = CheerfulPrimaryPink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    searchQuery = ""
                                    selectedDifficulty = "All"
                                    selectedStatusFilter = "All"
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Empty State
            if (filteredLessons.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = FrostedGlassWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = if (selectedDifficulty == "Favorites") "❤️" else "🔍", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (selectedDifficulty == "Favorites") "No favorite dances saved yet" else "No lessons found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextCharcoal
                            )
                            Text(
                                text = if (selectedDifficulty == "Favorites") "Tap the heart icon on any lesson to save it to your favorites!" else "Try clearing your search query or selecting a different category.",
                                fontSize = 13.sp,
                                color = TextGray,
                                modifier = Modifier.padding(top = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedDifficulty = "All"
                                    selectedStatusFilter = "All"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CheerfulPrimaryPink),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Show All Lessons")
                            }
                        }
                    }
                }
            }

            // Lessons List
            items(filteredLessons, key = { it.id }) { lesson ->
                val entity = lessonEntities.find { it.id == lesson.id }
                val dbStatus = entity?.completionStatus ?: "Not Started"
                val isFav = favoriteLessonIds.contains(lesson.id)

                DanceLessonCard(
                    lesson = lesson,
                    dbStatus = dbStatus,
                    isFavorite = isFav,
                    onToggleFavorite = { viewModel.toggleFavoriteLesson(lesson) },
                    onSpeakPreview = {
                        if (isSpeaking) {
                            viewModel.stopVoiceInstruction()
                        } else {
                            viewModel.speakVoiceInstruction("${lesson.title}. ${lesson.description}", force = true)
                        }
                    },
                    onPlay = { onSelectLesson(lesson) },
                    onToggleComplete = {
                        if (dbStatus == "Completed") {
                            // Already completed
                        } else {
                            viewModel.markDanceLessonCompleted(lesson)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DanceLessonCard(
    lesson: DanceLesson,
    dbStatus: String,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onSpeakPreview: () -> Unit = {},
    onPlay: () -> Unit,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = dbStatus == "Completed"
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SoftPink.copy(alpha = 0.4f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                if (isCompleted) listOf(CheerfulMint.copy(alpha = 0.7f), GlassBorderWhite)
                else listOf(GlassBorderWhite, GlassBorderPink)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay)
            .testTag("lesson_card_${lesson.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Thumbnail image with fallback emoji
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    when (lesson.stage) {
                                        "3-5" -> BalletPink
                                        "6-8" -> SkyBlue
                                        else -> LemonYellow
                                    },
                                    SoftPink
                                )
                            )
                        )
                ) {
                    AsyncImage(
                        model = lesson.thumbnailUrl,
                        contentDescription = lesson.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Emoji pill overlay
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .padding(6.dp)
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = lesson.emoji, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Details Column
                Column(modifier = Modifier.weight(1f)) {
                    // Category & Duration Badges + Favorite Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (lesson.stage) {
                                    "3-5" -> CheerfulPrimaryPink.copy(alpha = 0.15f)
                                    "6-8" -> CheerfulSecondaryBlue.copy(alpha = 0.15f)
                                    else -> CheerfulTertiaryYellow.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = when (lesson.stage) {
                                        "3-5" -> "Toddler (3–5)"
                                        "6-8" -> "Ballet Basics (6–8)"
                                        else -> "Academy (9–12)"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (lesson.stage) {
                                        "3-5" -> CheerfulPrimaryPink
                                        "6-8" -> Color(0xFF0369A1)
                                        else -> Color(0xFFB45309)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.8f)
                            ) {
                                Text(
                                    text = "⏱️ ${lesson.durationMinutes} min",
                                    fontSize = 10.sp,
                                    color = TextGray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Favorite heart toggle
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("lesson_fav_button_${lesson.id}")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Toggle Favorite",
                                tint = if (isFavorite) Color(0xFFE11D48) else TextGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextCharcoal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = lesson.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Action Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Completion Status Badge from Room DB
                when (dbStatus) {
                    "Completed" -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CheerfulMint.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CheerfulMint, CheerfulMint)))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Completed",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }
                    "In Progress" -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDBEAFE),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF3B82F6))))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🩰 In Progress",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E40AF)
                                )
                            }
                        }
                    }
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.7f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🎯 Ready to Dance",
                                    fontSize = 11.sp,
                                    color = TextGray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Actions: Voice Preview, Mark complete & Play
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Voice Preview button
                    IconButton(
                        onClick = onSpeakPreview,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("preview_voice_${lesson.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Hear lesson guide",
                            tint = Color(0xFF914D5D),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (!isCompleted) {
                        IconButton(
                            onClick = onToggleComplete,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("mark_done_${lesson.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Mark lesson as completed",
                                tint = TextGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onPlay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CheerfulPrimaryPink
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("play_button_${lesson.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCompleted) "Replay" else "Start Dance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * DanceLibraryScreen is an accessible entry point providing difficulty-based category navigation
 * (Toddler, Ballet Basics, Academy) and Room-backed Favorites using Coil image loading.
 */
@Composable
fun DanceLibraryScreen(
    viewModel: DanceViewModel,
    onSelectLesson: (DanceLesson) -> Unit,
    modifier: Modifier = Modifier
) {
    LessonLibraryScreen(viewModel = viewModel, onSelectLesson = onSelectLesson, modifier = modifier)
}

@Composable
fun DanceLibrary(
    viewModel: DanceViewModel,
    onSelectLesson: (DanceLesson) -> Unit,
    modifier: Modifier = Modifier
) {
    LessonLibraryScreen(viewModel = viewModel, onSelectLesson = onSelectLesson, modifier = modifier)
}
