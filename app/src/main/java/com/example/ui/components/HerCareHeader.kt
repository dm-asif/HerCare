package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.CardSoftShadowColor
import com.example.ui.theme.DeepPink
import com.example.ui.theme.LightPink
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftRose

@Composable
fun HerCareHeader(
    userName: String,
    currentLanguage: AppLanguage,
    onLanguageToggle: (AppLanguage) -> Unit,
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Official Logo + App Title + Slogan / Greeting
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Squircle Official HerCare Logo container with delicate shadow & border
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hercare_icon),
                    contentDescription = "HerCare Official Logo",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = HerCareStrings.appName(currentLanguage),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp
                    ),
                    color = DeepPink
                )
                Text(
                    text = if (currentLanguage == AppLanguage.BANGLA) "আপনার স্বাস্থ্য • আমাদের যত্ন ♡" else "Your Health • Our Care ♡",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    ),
                    color = SecondaryText
                )
                if (userName.isNotBlank()) {
                    Text(
                        text = if (currentLanguage == AppLanguage.BANGLA) "স্বাগতম, $userName" else "Hello, $userName",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = PrimaryPink
                    )
                }
            }
        }

        // Right Actions: Language Pill & Notification Bell
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Refined Pill Language Selector
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        val nextLang = if (currentLanguage == AppLanguage.ENGLISH) AppLanguage.BANGLA else AppLanguage.ENGLISH
                        onLanguageToggle(nextLang)
                    }
                    .testTag("language_toggle_chip"),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, CardBorderColor),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Change Language",
                        tint = DeepPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "বাংলা" else "English",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryText
                    )
                }
            }

            // Notification Bell with unread badge dot
            Box {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onNotificationClick() }
                        .testTag("notification_bell_button"),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, CardBorderColor),
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = DeepPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Active Notification Indicator Dot
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(PrimaryPink)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}
