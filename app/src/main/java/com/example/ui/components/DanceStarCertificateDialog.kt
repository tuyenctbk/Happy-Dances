package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.BalletPink
import com.example.ui.theme.BorderStrokeColors
import com.example.ui.theme.LemonYellow
import com.example.ui.theme.SkyBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CertificateTheme(
    val label: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val bgStart: Color,
    val bgEnd: Color,
    val ribbonColor: Color,
    val textColor: Color
) {
    ROYAL_GOLD(
        label = "Royal Gold 👑",
        primaryColor = Color(0xFFD97706),
        secondaryColor = Color(0xFFFBBF24),
        bgStart = Color(0xFFFFFBEB),
        bgEnd = Color(0xFFFEF3C7),
        ribbonColor = Color(0xFFB45309),
        textColor = Color(0xFF78350F)
    ),
    BALLET_ROSE(
        label = "Ballet Rose 🌸",
        primaryColor = Color(0xFFDB2777),
        secondaryColor = Color(0xFFF472B6),
        bgStart = Color(0xFFFDF2F8),
        bgEnd = Color(0xFFFCE7F3),
        ribbonColor = Color(0xFFBE185D),
        textColor = Color(0xFF831843)
    ),
    SWAN_SAPPHIRE(
        label = "Swan Sapphire 🦢",
        primaryColor = Color(0xFF2563EB),
        secondaryColor = Color(0xFF60A5FA),
        bgStart = Color(0xFFEFF6FF),
        bgEnd = Color(0xFFDBEAFE),
        ribbonColor = Color(0xFF1D4ED8),
        textColor = Color(0xFF1E3A8A)
    ),
    EMERALD_STAR(
        label = "Emerald Star 🌟",
        primaryColor = Color(0xFF059669),
        secondaryColor = Color(0xFF34D399),
        bgStart = Color(0xFFECFDF5),
        bgEnd = Color(0xFFD1FAE5),
        ribbonColor = Color(0xFF047857),
        textColor = Color(0xFF064E3B)
    )
}

/**
 * Printable & Shareable Digital 'Dance Star' Certificate.
 */
@Composable
fun DanceStarCertificateCard(
    dancerName: String,
    milestoneTitle: String,
    milestoneDescription: String = "For outstanding grace, balance, and joyous dedication in the Happy Dances Ballet Studio.",
    achievementLevel: String = "Certified Dance Star 🌟",
    issuedDate: String = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date()),
    theme: CertificateTheme = CertificateTheme.ROYAL_GOLD,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("dance_star_certificate_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(theme.bgStart, theme.bgEnd)
                    )
                )
                .padding(18.dp)
        ) {
            // Ornamental Border Canvas
            Canvas(modifier = Modifier.matchParentSize()) {
                val strokeW = 4.dp.toPx()
                val innerOffset = 8.dp.toPx()
                val w = size.width
                val h = size.height

                // Outer border
                drawRoundRect(
                    color = theme.primaryColor.copy(alpha = 0.8f),
                    size = Size(w - innerOffset * 2, h - innerOffset * 2),
                    topLeft = Offset(innerOffset, innerOffset),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                    style = Stroke(width = strokeW)
                )

                // Inner thin gold guideline
                drawRoundRect(
                    color = theme.secondaryColor.copy(alpha = 0.4f),
                    size = Size(w - innerOffset * 3.5f, h - innerOffset * 3.5f),
                    topLeft = Offset(innerOffset * 1.75f, innerOffset * 1.75f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Corner decorative flourish circles
                val cornerRadius = 14f
                val cornerInset = innerOffset + 12f
                drawCircle(theme.secondaryColor, cornerRadius, Offset(cornerInset, cornerInset))
                drawCircle(theme.secondaryColor, cornerRadius, Offset(w - cornerInset, cornerInset))
                drawCircle(theme.secondaryColor, cornerRadius, Offset(cornerInset, h - cornerInset))
                drawCircle(theme.secondaryColor, cornerRadius, Offset(w - cornerInset, h - cornerInset))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Crest & Crown
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("✨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(theme.secondaryColor, theme.primaryColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("✨", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Academy Branding
                Text(
                    text = "HAPPY DANCES BALLET ACADEMY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = theme.ribbonColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Diploma Title
                Text(
                    text = "OFFICIAL DANCE STAR CERTIFICATE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.textColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // "This is proudly awarded to"
                Text(
                    text = "This certificate of dance mastery is proudly awarded to",
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dancer's Name Box with Calligraphy style
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                        .border(1.5.dp, theme.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dancerName.ifEmpty { "Star Dancer" },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primaryColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Achievement Milestone Title
                Text(
                    text = "For Successfully Achieving",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Text(
                    text = milestoneTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Milestone description
                Text(
                    text = milestoneDescription,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Seals & Signature Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Date & Level
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "DATE ISSUED",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = issuedDate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = achievementLevel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.primaryColor
                        )
                    }

                    // Gold Official Seal
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(theme.secondaryColor, theme.primaryColor)
                                )
                            )
                            .shadow(3.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐", fontSize = 16.sp)
                            Text(
                                text = "OFFICIAL\nSEAL",
                                fontSize = 6.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 8.sp
                            )
                        }
                    }

                    // Master Choreographer Signature
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Madame Étoile",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            color = theme.primaryColor
                        )
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(1.dp)
                                .background(theme.primaryColor.copy(alpha = 0.6f))
                        )
                        Text(
                            text = "MASTER CHOREOGRAPHER",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Dialog for viewing, printing, and sharing the Dance Star Certificate.
 */
@Composable
fun DanceStarCertificateDialog(
    dancerName: String,
    milestoneTitle: String = "Grand Ballet Star Award 🌸",
    milestoneDescription: String = "Completed all stage movement foundations with exceptional joy, musicality, and balance!",
    achievementLevel: String = "Ballet Star Laureate 🩰",
    onDismiss: () -> Unit,
    onPlayChime: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTheme by remember { mutableStateOf(CertificateTheme.ROYAL_GOLD) }
    val todayDate = remember {
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("dance_star_certificate_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📜", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.certificate_dialog_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close_action),
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Theme selection chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CertificateTheme.values().forEach { themeOption ->
                        val isSelected = selectedTheme == themeOption
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) themeOption.primaryColor else Color.White.copy(alpha = 0.1f)
                                )
                                .clickable {
                                    selectedTheme = themeOption
                                    onPlayChime()
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = themeOption.label.split(" ")[0],
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Certificate Preview
                DanceStarCertificateCard(
                    dancerName = dancerName,
                    milestoneTitle = milestoneTitle,
                    milestoneDescription = milestoneDescription,
                    achievementLevel = achievementLevel,
                    issuedDate = todayDate,
                    theme = selectedTheme
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Share & Print Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Share / Print Button
                    Button(
                        onClick = {
                            shareCertificate(
                                context = context,
                                dancerName = dancerName,
                                milestoneTitle = milestoneTitle,
                                issuedDate = todayDate
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("share_certificate_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.certificate_share_print),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Save / Download Certificate Action
                    Button(
                        onClick = {
                            onPlayChime()
                            Toast.makeText(
                                context,
                                "✨ Dance Star Certificate saved to your Dancer Passport!",
                                Toast.LENGTH_LONG
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BalletPink),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_certificate_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Save", tint = Color(0xFF914D5D), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.certificate_save_to_passport),
                            color = Color(0xFF914D5D),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Standard Android Share Intent helper for Printable Digital Certificate.
 */
private fun shareCertificate(
    context: Context,
    dancerName: String,
    milestoneTitle: String,
    issuedDate: String
) {
    val certificateText = """
        🩰 ✨ HAPPY DANCES BALLET ACADEMY ✨ 🩰
        ==================================================
        👑 OFFICIAL DANCE STAR CERTIFICATE OF EXCELLENCE 👑
        
        This certificate is proudly awarded to:
        👉 ${dancerName.ifEmpty { "Star Dancer" }} 👈
        
        For outstanding achievement in:
        🌟 $milestoneTitle 🌟
        
        Date of Issuance: $issuedDate
        Academy Seal: Verified Dance Star ⭐
        Master Choreographer: Madame Étoile
        
        "Keep leaping, twirling, and shining with graceful joy!"
        ==================================================
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, certificateText)
        putExtra(Intent.EXTRA_TITLE, "Dance Star Certificate - $dancerName")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Print or Share Dance Certificate")
    context.startActivity(shareIntent)
}
