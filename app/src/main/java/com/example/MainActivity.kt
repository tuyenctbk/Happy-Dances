package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import com.example.ui.screens.LessonLibraryScreen
import com.example.ui.navigation.DanceNavigationGraph
import com.example.ui.components.VoiceStudioAssistantDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DanceViewModel
import com.example.ui.DanceViewModelFactory
import com.example.ui.screens.AvatarScreen
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.res.stringResource
import com.example.ui.screens.IntroLevelSelectionScreen
import com.example.ui.screens.LessonScreen
import com.example.ui.screens.MapScreen
import com.example.ui.screens.ParentPortalScreen
import com.example.ui.screens.PassportScreen
import com.example.ui.screens.StoryModeScreen
import com.example.ui.theme.HappyDancesTheme
import com.example.ui.theme.BalletPink
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.LemonYellow
import kotlinx.coroutines.delay

import com.example.ui.components.dpadFocusable

class MainActivity : ComponentActivity() {

    private val viewModel: DanceViewModel by viewModels {
        DanceViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isNightModeEnabled by viewModel.isNightModeEnabled.collectAsState()
            HappyDancesTheme(darkTheme = isNightModeEnabled) {
                var showSplash by remember { mutableStateOf(true) }


                LaunchedEffect(Unit) {
                    delay(2200) // Show beautiful splash for 2.2 seconds
                    showSplash = false
                }

                Crossfade(
                    targetState = showSplash,
                    animationSpec = tween(durationMillis = 600)
                ) { isSplashActive ->
                    if (isSplashActive) {
                        SplashScreen()
                    } else {
                        MainAppContent()
                    }
                }
            }
        }
    }

    @Composable
    fun SplashScreen() {
        // Soft gradient representing our brand identity
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFF0F3), // Soft pink tint
                Color(0xFFFFD1DC), // Ballet Pink
                Color(0xFFAEC6CF)  // Soft Blue
            )
        )

        val infiniteTransition = rememberInfiniteTransition(label = "splash_anim")
        
        // Gentle pulse scale animation for the center emblem
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )

        // Gentle rotation for the sparkle icons
        val rotationAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rot_angle"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .testTag("splash_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Interactive pulsing outer circle representing ballet steps
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .graphicsLayer(
                            scaleX = pulseScale,
                            scaleY = pulseScale
                        )
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🩰",
                        fontSize = 72.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Sparkling stars around the slippers
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "sparkle",
                        tint = Color(0xFFFFD54F), // LemonYellow
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.TopEnd)
                            .graphicsLayer(rotationZ = rotationAngle)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Happy Dances",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF914D5D), // Deep rich brand pink text
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Discover the Magic of Dance!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B), // Slate-500
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    fun MainAppContent() {
        val currentScreen by viewModel.currentScreen.collectAsState()
        val isFullscreenActivity = currentScreen == "lesson" || currentScreen == "story_mode" || currentScreen == "intro_level"
        val activeLesson by viewModel.activeLesson.collectAsState()
        val activeMode by viewModel.activeMode.collectAsState()
        val isStorytelling = activeMode == "storytelling"
        var showVoiceAssistant by remember { mutableStateOf(false) }

        val showRateAppDialog by viewModel.showRateAppDialog.collectAsState()
        val showShareAppDialog by viewModel.showShareAppDialog.collectAsState()
        val context = LocalContext.current

        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp
        val isWideDisplay = screenWidthDp >= 720

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                if (!isFullscreenActivity) {
                    FloatingActionButton(
                        onClick = { showVoiceAssistant = true },
                        containerColor = if (isStorytelling) Color(0xFFEC4899) else Color(0xFF2563EB),
                        contentColor = Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp),
                        modifier = Modifier
                            .testTag("voice_assistant_fab")
                            .dpadFocusable(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Assistant",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            },
            bottomBar = {
                // Immersive Lesson Studio, Story Mode, and Intro Level hide the Bottom Navigation Bar completely
                AnimatedVisibility(
                    visible = !isFullscreenActivity,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (isWideDisplay) 12.dp else 0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NavigationBar(
                            modifier = Modifier
                                .then(
                                    if (isWideDisplay) {
                                        Modifier
                                            .widthIn(max = 680.dp)
                                            .clip(RoundedCornerShape(24.dp))
                                            .border(
                                                width = 1.dp,
                                                color = if (isStorytelling) Color.White.copy(alpha = 0.5f) else Color(0x331E40AF),
                                                shape = RoundedCornerShape(24.dp)
                                            )
                                    } else {
                                        Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                                            .border(
                                                width = 1.dp,
                                                color = if (isStorytelling) Color.White.copy(alpha = 0.4f) else Color(0x331E40AF),
                                                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                                            )
                                    }
                                )
                                .testTag("main_navigation_bar"),
                            containerColor = if (isStorytelling) Color.White.copy(alpha = 0.95f) else Color(0xFFF8FAFC),
                            tonalElevation = 4.dp
                        ) {
                        NavigationBarItem(
                            selected = currentScreen == "map",
                            onClick = { viewModel.navigateTo("map") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = if (isStorytelling) "Story Map" else "Academy Studio"
                                )
                            },
                            label = {
                                Text(
                                    text = if (isStorytelling) "Story Map" else "Academy",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                )
                            },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E40AF),
                                selectedTextColor = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E40AF),
                                indicatorColor = if (isStorytelling) Color(0xFFFFD1DC) else Color(0xFFDBEAFE),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier
                                .testTag("nav_map_tab")
                                .dpadFocusable(RoundedCornerShape(12.dp))
                        )

                        NavigationBarItem(
                            selected = currentScreen == "library",
                            onClick = { viewModel.navigateTo("library") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.VideoLibrary,
                                    contentDescription = "Studio Dashboard"
                                )
                            },
                            label = {
                                Text(
                                    text = "Dashboard",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                )
                            },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFFF5286),
                                selectedTextColor = Color(0xFFFF5286),
                                indicatorColor = Color(0xFFFFD9E2),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier
                                .testTag("nav_library_tab")
                                .dpadFocusable(RoundedCornerShape(12.dp))
                        )

                        NavigationBarItem(
                            selected = currentScreen == "story_mode_entry",
                            onClick = { viewModel.navigateTo("story_mode_entry") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = if (isStorytelling) "Story Mode" else "Stories & Tales"
                                )
                            },
                            label = {
                                Text(
                                    text = if (isStorytelling) "Story Mode" else "Tales",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                )
                            },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E40AF),
                                selectedTextColor = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E40AF),
                                indicatorColor = if (isStorytelling) Color(0xFFFFD1DC) else Color(0xFFDBEAFE),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier
                                .testTag("nav_story_tab")
                                .dpadFocusable(RoundedCornerShape(12.dp))
                        )

                        NavigationBarItem(
                            selected = currentScreen == "avatar",
                            onClick = { viewModel.navigateTo("avatar") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "My Avatar"
                                )
                            },
                            label = {
                                Text(
                                    text = if (isStorytelling) "Tiny Dancer" else "Avatar",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                )
                            },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E40AF),
                                selectedTextColor = if (isStorytelling) Color(0xFF914D5D) else Color(0xFF1E40AF),
                                indicatorColor = if (isStorytelling) Color(0xFFFFD1DC) else Color(0xFFAEC6CF),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier
                                .testTag("nav_avatar_tab")
                                .dpadFocusable(RoundedCornerShape(12.dp))
                        )

                        NavigationBarItem(
                            selected = currentScreen == "passport",
                            onClick = { viewModel.navigateTo("passport") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "My Passport"
                                )
                            },
                            label = {
                                Text(
                                    text = if (isStorytelling) "Stickers" else "Passport",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                )
                            },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF92400E),
                                selectedTextColor = Color(0xFF92400E),
                                indicatorColor = Color(0xFFFFE082), // LemonYellow
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier
                                .testTag("nav_passport_tab")
                                .dpadFocusable(RoundedCornerShape(12.dp))
                        )

                        NavigationBarItem(
                            selected = currentScreen == "parent",
                            onClick = { viewModel.navigateTo("parent") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Parent Portal"
                                )
                            },
                            label = {
                                Text(
                                    text = "Parents",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                )
                            },
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF334155),
                                selectedTextColor = Color(0xFF334155),
                                indicatorColor = Color(0xFFE2E8F0),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier
                                .testTag("nav_parents_tab")
                                .dpadFocusable(RoundedCornerShape(12.dp))
                        )
                    }
                    }
                }
            }
        ) { innerPadding ->
            val contentModifier = if (isFullscreenActivity) {
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFF131416))
            } else {
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            }

            Box(
                modifier = contentModifier,
                contentAlignment = Alignment.TopCenter
            ) {
                when (currentScreen) {
                    "intro_level" -> IntroLevelSelectionScreen(
                        viewModel = viewModel,
                        onCompleted = { viewModel.navigateTo("map") }
                    )
                    "map" -> MapScreen(
                        viewModel = viewModel,
                        onNavigateToLibrary = { viewModel.navigateTo("library") },
                        onNavigateToLesson = { lesson -> viewModel.selectLesson(lesson) },
                        onNavigateToIntroLevel = { viewModel.navigateTo("intro_level") }
                    )
                    "library" -> LessonLibraryScreen(
                        viewModel = viewModel,
                        onSelectLesson = { lesson -> viewModel.selectLesson(lesson) }
                    )
                    "story_mode", "story_mode_entry", "curriculum_graph" -> DanceNavigationGraph(
                        viewModel = viewModel,
                        onBackToMap = { viewModel.navigateTo("map") }
                    )
                    "avatar" -> AvatarScreen(viewModel)
                    "passport" -> PassportScreen(viewModel)
                    "parent" -> ParentPortalScreen(viewModel)
                    "lesson" -> {
                        activeLesson?.let { lesson ->
                            LessonScreen(viewModel = viewModel, lesson = lesson)
                        } ?: MapScreen(viewModel)
                    }
                }

                if (showVoiceAssistant) {
                    VoiceStudioAssistantDialog(
                        viewModel = viewModel,
                        onDismiss = { showVoiceAssistant = false }
                    )
                }

                if (showRateAppDialog) {
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissRateAppDialog() },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⭐ ", fontSize = 24.sp)
                                Text(
                                    text = stringResource(R.string.rate_app_dialog_title),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.rate_app_dialog_message),
                                fontSize = 14.sp,
                                color = Color(0xFF475569)
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.dismissRateAppDialog()
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                                        context.startActivity(intent)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5286)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.dpadFocusable(RoundedCornerShape(12.dp))
                            ) {
                                Text(stringResource(R.string.rate_app_action_rate), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { viewModel.dismissRateAppDialog() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.dpadFocusable(RoundedCornerShape(12.dp))
                            ) {
                                Text(stringResource(R.string.rate_app_action_later), color = Color(0xFF64748B))
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        containerColor = Color.White,
                        modifier = Modifier.testTag("rate_app_suggestion_dialog")
                    )
                }

                if (showShareAppDialog) {
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissShareAppDialog() },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🚀 ", fontSize = 24.sp)
                                Text(
                                    text = stringResource(R.string.share_app_dialog_title),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.share_app_dialog_message),
                                fontSize = 14.sp,
                                color = Color(0xFF475569)
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.dismissShareAppDialog()
                                    val shareBody = context.getString(R.string.share_app_message_body, context.packageName)
                                    val chooserTitle = context.getString(R.string.share_app_chooser_title)
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareBody)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, chooserTitle)
                                    context.startActivity(shareIntent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.dpadFocusable(RoundedCornerShape(12.dp))
                            ) {
                                Text(stringResource(R.string.share_app_action_share), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { viewModel.dismissShareAppDialog() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.dpadFocusable(RoundedCornerShape(12.dp))
                            ) {
                                Text(stringResource(R.string.share_app_action_later), color = Color(0xFF64748B))
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        containerColor = Color.White,
                        modifier = Modifier.testTag("share_app_suggestion_dialog")
                    )
                }
            }
        }
    }
}
