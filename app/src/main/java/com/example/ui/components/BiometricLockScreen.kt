package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.CardSoftShadowColor
import com.example.ui.theme.DeepPink
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText

@Composable
fun BiometricLockScreen(
    language: AppLanguage,
    onTriggerBiometric: () -> Unit,
    onPinUnlock: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var showPinInput by remember { mutableStateOf(false) }

    HerCareBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(28.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Official HerCare Logo Branding
                    Surface(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp), spotColor = CardSoftShadowColor),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hercare_icon),
                            contentDescription = "HerCare Logo",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = HerCareStrings.appName(language),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = DeepPink
                    )

                    Text(
                        text = HerCareStrings.biometricTitle(language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryText,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = HerCareStrings.unlockSubtitle(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                    )

                    if (!showPinInput) {
                        Button(
                            onClick = onTriggerBiometric,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepPink),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("biometric_unlock_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint",
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = HerCareStrings.unlockAppPrompt(language),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = { showPinInput = true }
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "PIN", modifier = Modifier.size(18.dp), tint = DeepPink)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                if (language == AppLanguage.BANGLA) "পিন কোড দিয়ে আনলক করুন" else "Unlock with PIN",
                                color = DeepPink,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = {
                                if (it.length <= 6) {
                                    enteredPin = it
                                    pinError = false
                                }
                            },
                            label = { Text(if (language == AppLanguage.BANGLA) "৪-৬ সংখ্যার পিন" else "Enter PIN") },
                            isError = pinError,
                            supportingText = {
                                if (pinError) {
                                    Text(
                                        text = if (language == AppLanguage.BANGLA) "ভুল পিন কোড" else "Incorrect PIN",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (onPinUnlock(enteredPin)) {
                                    pinError = false
                                } else {
                                    pinError = true
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepPink),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                if (language == AppLanguage.BANGLA) "যাচাই করুন" else "Verify & Unlock",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(
                            onClick = { showPinInput = false }
                        ) {
                            Text(
                                if (language == AppLanguage.BANGLA) "বায়োমেট্রিক ব্যবহারে ফিরে যান" else "Use Biometrics",
                                color = DeepPink,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
