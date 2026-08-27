package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DanceViewModel
import com.example.ui.theme.BalletPink
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue

@Composable
fun AvatarScreen(viewModel: DanceViewModel) {
    val profile by viewModel.profileState.collectAsState()

    // Gentle levitation animation for the avatar preview
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = { t -> (t * t * (3 - 2 * t)) }), // SmootheaseInOut
            repeatMode = RepeatMode.Reverse
        ),
        label = "levitate"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9FB)) // Frosted Cream base
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("avatar_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dancer Avatar Creator",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Practice lessons to unlock outfits and stage designs!",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Avatar Interactive Canvas Card
        Card(
            modifier = Modifier
                .size(280.dp)
                .offset(y = floatOffset.dp)
                .border(4.dp, Color.White, CircleShape) // Sleek white border for glassmorphic contrast
                .testTag("avatar_preview_card"),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Vector Canvas drawing our customized character
                AvatarCanvas(
                    outfit = profile.avatarOutfit,
                    hair = profile.avatarHair,
                    stageBackground = profile.avatarBackground,
                    modifier = Modifier.fillMaxSize()
                )

                // Twinkling Overlay Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BalletPink)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Stars",
                            tint = LemonYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = profile.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Customization Options
        // 1. OUTFIT SELECTION
        CustomizerSectionHeader(icon = Icons.Default.Palette, title = "Select Dance Outfit")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val outfits = listOf(
                Triple("tutu_pink", "Ballet Pink Tutu", BalletPink),
                Triple("ballet_blue", "Ocean Blue Bodysuit", SkyBlue),
                Triple("royal_gold", "Royal Gold Stars", LemonYellow)
            )

            outfits.forEach { (key, name, color) ->
                val isSelected = profile.avatarOutfit == key
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            viewModel.updateAvatar(
                                outfit = key,
                                hair = profile.avatarHair,
                                background = profile.avatarBackground
                            )
                        }
                        .testTag("outfit_$key")
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF914D5D) else Color.White.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = name.split(" ")[1], // Just show short noun
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. HAIRSTYLE SELECTION
        CustomizerSectionHeader(icon = Icons.Default.Face, title = "Choose Hair Design")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val hairstyles = listOf(
                Triple("bun_brown", "Ballet Bun", Color(0xFF5D4037)),
                Triple("braids_black", "Double Braids", Color(0xFF212121)),
                Triple("curls_blonde", "Sunny Curls", Color(0xFFFFD54F))
            )

            hairstyles.forEach { (key, name, color) ->
                val isSelected = profile.avatarHair == key
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            viewModel.updateAvatar(
                                outfit = profile.avatarOutfit,
                                hair = key,
                                background = profile.avatarBackground
                            )
                        }
                        .testTag("hair_$key")
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF914D5D) else Color.White.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = name.split(" ")[1],
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. BACKGROUND SELECTION
        CustomizerSectionHeader(icon = Icons.Default.Landscape, title = "Change Stage Scene")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val backdrops = listOf(
                Triple("garden", "Magic Garden", Color(0xFFAEC6CF)),
                Triple("castle", "Royal Palace", Color(0xFFFFE082)),
                Triple("moon", "Lunar Station", Color(0xFF9FA8DA))
            )

            backdrops.forEach { (key, name, color) ->
                val isSelected = profile.avatarBackground == key
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            viewModel.updateAvatar(
                                outfit = profile.avatarOutfit,
                                hair = profile.avatarHair,
                                background = key
                            )
                        }
                        .testTag("background_$key")
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color(0xFF914D5D) else Color.White.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = name.split(" ")[1],
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Go Back Home button
        Button(
            onClick = { viewModel.navigateTo("map") },
            colors = ButtonDefaults.buttonColors(containerColor = BalletPink),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(52.dp)
                .testTag("avatar_back_button")
        ) {
            Text("Looks Perfect!", color = Color(0xFF914D5D), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun CustomizerSectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF914D5D), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
fun AvatarCanvas(
    outfit: String,
    hair: String,
    stageBackground: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f
        val centerY = h / 2f

        // Draw Stage Background Backdrop
        val bgBrush = when (stageBackground) {
            "garden" -> Brush.radialGradient(
                colors = listOf(Color(0xFFE8F5E9), Color(0xFFA5D6A7)),
                center = Offset(centerX, centerY),
                radius = w / 1.5f
            )
            "castle" -> Brush.radialGradient(
                colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFD54F)),
                center = Offset(centerX, centerY),
                radius = w / 1.5f
            )
            else -> Brush.radialGradient( // Moon
                colors = listOf(Color(0xFFE8EAF6), Color(0xFF7986CB)),
                center = Offset(centerX, centerY),
                radius = w / 1.5f
            )
        }

        drawCircle(brush = bgBrush, radius = w / 2f, center = Offset(centerX, centerY))

        // Draw stage floor spotlights
        drawOval(
            color = Color.White.copy(alpha = 0.3f),
            topLeft = Offset(centerX - w / 3f, centerY + h / 5f),
            size = Size(w * 2 / 3f, h / 4f)
        )

        // Draw Dancer body & neck
        // Skin tone
        val skinColor = Color(0xFFFFD1A9)
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - 15f, centerY + 30f),
            size = Size(30f, 40f)
        )

        // Draw head
        val headRadius = 45f
        drawCircle(
            color = skinColor,
            radius = headRadius,
            center = Offset(centerX, centerY - 10f)
        )

        // Draw Face Details (Cute sleeping/smiling eyes and mouth)
        // Eyes
        drawArc(
            color = Color(0xFF3E2723),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - 22f, centerY - 15f),
            size = Size(14f, 10f),
            style = Stroke(width = 3f)
        )
        drawArc(
            color = Color(0xFF3E2723),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX + 8f, centerY - 15f),
            size = Size(14f, 10f),
            style = Stroke(width = 3f)
        )
        // Rosy cheeks
        drawCircle(
            color = Color(0xFFFF8A80).copy(alpha = 0.6f),
            radius = 8f,
            center = Offset(centerX - 24f, centerY)
        )
        drawCircle(
            color = Color(0xFFFF8A80).copy(alpha = 0.6f),
            radius = 8f,
            center = Offset(centerX + 24f, centerY)
        )
        // Smiling mouth
        drawArc(
            color = Color(0xFFC2185B),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - 6f, centerY - 2f),
            size = Size(12f, 12f),
            style = Stroke(width = 3.5f)
        )

        // Draw Hair Selection
        val hairColor = when (hair) {
            "bun_brown" -> Color(0xFF5D4037)
            "braids_black" -> Color(0xFF212121)
            else -> Color(0xFFFDD835) // Blonde
        }

        if (hair == "bun_brown") {
            // Draw top knot bun
            drawCircle(
                color = hairColor,
                radius = 24f,
                center = Offset(centerX, centerY - 65f)
            )
            // Cute head hairband
            drawArc(
                color = BalletPink,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX - headRadius, centerY - 28f),
                size = Size(headRadius * 2, headRadius * 2),
                style = Stroke(width = 6f)
            )
            // Hair cap
            drawArc(
                color = hairColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - headRadius, centerY - 24f),
                size = Size(headRadius * 2, headRadius * 2)
            )
        } else if (hair == "braids_black") {
            // Hair cap
            drawArc(
                color = hairColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - headRadius, centerY - 24f),
                size = Size(headRadius * 2, headRadius * 2)
            )
            // Left Braid
            drawCircle(color = hairColor, radius = 12f, center = Offset(centerX - 42f, centerY + 15f))
            drawCircle(color = hairColor, radius = 10f, center = Offset(centerX - 45f, centerY + 30f))
            drawCircle(color = BalletPink, radius = 6f, center = Offset(centerX - 46f, centerY + 40f)) // hairbow
            // Right Braid
            drawCircle(color = hairColor, radius = 12f, center = Offset(centerX + 42f, centerY + 15f))
            drawCircle(color = hairColor, radius = 10f, center = Offset(centerX + 45f, centerY + 30f))
            drawCircle(color = BalletPink, radius = 6f, center = Offset(centerX + 46f, centerY + 40f)) // hairbow
        } else { // Blonde curls
            // Hair cap
            drawArc(
                color = hairColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - headRadius, centerY - 24f),
                size = Size(headRadius * 2, headRadius * 2)
            )
            // Fluffy curls around the head
            drawCircle(color = hairColor, radius = 16f, center = Offset(centerX - 40f, centerY - 20f))
            drawCircle(color = hairColor, radius = 16f, center = Offset(centerX + 40f, centerY - 20f))
            drawCircle(color = hairColor, radius = 18f, center = Offset(centerX - 44f, centerY - 2f))
            drawCircle(color = hairColor, radius = 18f, center = Offset(centerX + 44f, centerY - 2f))
            drawCircle(color = hairColor, radius = 15f, center = Offset(centerX - 42f, centerY + 15f))
            drawCircle(color = hairColor, radius = 15f, center = Offset(centerX + 42f, centerY + 15f))
        }

        // Draw Outfit selection
        val outfitColor = when (outfit) {
            "tutu_pink" -> BalletPink
            "ballet_blue" -> SkyBlue
            else -> Color(0xFFFFD54F) // Gold
        }

        // Bodysuit torso
        val torsoPath = Path().apply {
            moveTo(centerX - 22f, centerY + 60f)
            lineTo(centerX + 22f, centerY + 60f)
            lineTo(centerX + 18f, centerY + 120f)
            lineTo(centerX - 18f, centerY + 120f)
            close()
        }
        drawPath(path = torsoPath, color = outfitColor)

        // Arms (raised gracefully in classical 5th position or 1st position)
        if (outfit == "tutu_pink" || outfit == "royal_gold") {
            // Elegant curved arms overhead
            drawArc(
                color = skinColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(centerX - 50f, centerY + 10f),
                size = Size(100f, 80f),
                style = Stroke(width = 12f)
            )
        } else {
            // Athletic arms out to sides
            drawLine(
                color = skinColor,
                start = Offset(centerX - 20f, centerY + 65f),
                end = Offset(centerX - 70f, centerY + 45f),
                strokeWidth = 12f
            )
            drawLine(
                color = skinColor,
                start = Offset(centerX + 20f, centerY + 65f),
                end = Offset(centerX + 70f, centerY + 45f),
                strokeWidth = 12f
            )
        }

        // Leg 1 and 2 (Leg standing gracefully, pointing)
        drawLine(
            color = skinColor,
            start = Offset(centerX - 10f, centerY + 115f),
            end = Offset(centerX - 12f, centerY + 180f),
            strokeWidth = 12f
        )
        drawLine(
            color = skinColor,
            start = Offset(centerX + 10f, centerY + 115f),
            end = Offset(centerX + 28f, centerY + 175f),
            strokeWidth = 12f
        )
        // Cute shoes (Pointed slippers!)
        drawCircle(color = outfitColor, radius = 7f, center = Offset(centerX - 12f, centerY + 183f))
        drawCircle(color = outfitColor, radius = 7f, center = Offset(centerX + 30f, centerY + 178f))

        // Draw tutu skirt if tutu outfit is selected
        if (outfit == "tutu_pink") {
            // Layered fluffy tulle tutu skirt
            drawOval(
                color = BalletPink.copy(alpha = 0.8f),
                topLeft = Offset(centerX - 55f, centerY + 105f),
                size = Size(110f, 24f)
            )
            drawOval(
                color = Color.White.copy(alpha = 0.9f),
                topLeft = Offset(centerX - 45f, centerY + 103f),
                size = Size(90f, 20f)
            )
        } else if (outfit == "royal_gold") {
            // Glittering golden tunic stars
            drawCircle(color = Color.White, radius = 2.5f, center = Offset(centerX - 8f, centerY + 75f))
            drawCircle(color = Color.White, radius = 3.5f, center = Offset(centerX + 6f, centerY + 90f))
            drawCircle(color = Color.White, radius = 2f, center = Offset(centerX - 5f, centerY + 105f))
        }
    }
}
