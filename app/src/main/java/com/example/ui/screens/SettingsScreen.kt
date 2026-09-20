package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.shadow
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.CardSoftShadowColor
import com.example.ui.theme.DeepPink
import com.example.ui.theme.LightPink
import com.example.ui.theme.PrimaryPink
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftRose
import com.example.ui.theme.CoralPeriod
import com.example.ui.theme.FertileGreen
import com.example.ui.theme.WarningRed
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.ui.viewmodel.HerCareUiState

@Composable
fun SettingsScreen(
    uiState: HerCareUiState,
    onLanguageChange: (AppLanguage) -> Unit,
    onToggleBiometric: (Boolean, String?) -> Unit,
    onUpdatePersonalInfo: (name: String, dobMillis: Long) -> Unit = { _, _ -> },
    onTestMedicine: () -> Unit,
    onTestPeriod: () -> Unit,
    onTestIrregular: () -> Unit,
    onTestPad: () -> Unit,
    onTestHealth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = uiState.language
    val profile = uiState.userProfile
    val context = LocalContext.current

    var showEditProfileDialog by remember { mutableStateOf(false) }

    var notificationPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationPermissionGranted = isGranted
    }

    // Edit Personal Info Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(profile.name) }
        var editDobMillis by remember { mutableStateOf(profile.birthDateMillis) }
        var editNameError by remember { mutableStateOf(false) }

        val dobFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = HerCareStrings.editPersonalInfo(language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = DeepPink
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = {
                            editName = it
                            if (it.isNotBlank()) editNameError = false
                        },
                        label = { Text(HerCareStrings.enterName(language)) },
                        isError = editNameError,
                        supportingText = {
                            if (editNameError) {
                                Text(
                                    text = if (language == AppLanguage.BANGLA) "নাম ফাঁকা রাখা যাবে না" else "Name cannot be empty",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Name", tint = DeepPink)
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_name_input"),
                        singleLine = true
                    )

                    Column {
                        Text(
                            text = HerCareStrings.dateOfBirth(language),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance().apply { timeInMillis = editDobMillis }
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        val selectedCal = Calendar.getInstance().apply {
                                            set(Calendar.YEAR, y)
                                            set(Calendar.MONTH, m)
                                            set(Calendar.DAY_OF_MONTH, d)
                                            set(Calendar.HOUR_OF_DAY, 0)
                                            set(Calendar.MINUTE, 0)
                                            set(Calendar.SECOND, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }
                                        editDobMillis = selectedCal.timeInMillis
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).apply {
                                    datePicker.maxDate = System.currentTimeMillis()
                                }.show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("settings_dob_picker_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "DOB", modifier = Modifier.size(18.dp), tint = DeepPink)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(dobFormatter.format(editDobMillis), color = PrimaryText)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isBlank()) {
                            editNameError = true
                        } else {
                            onUpdatePersonalInfo(editName.trim(), editDobMillis)
                            showEditProfileDialog = false
                            Toast.makeText(
                                context,
                                HerCareStrings.savedSuccessfully(language),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPink),
                    modifier = Modifier.testTag("save_personal_info_button")
                ) {
                    Text(HerCareStrings.save(language), color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditProfileDialog = false },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SoftRose)
                ) {
                    Text(HerCareStrings.cancel(language), color = DeepPink)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = HerCareStrings.settingsTitle(language),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp
                ),
                color = DeepPink
            )
            Text(
                text = if (language == AppLanguage.BANGLA) "ভাষা, ব্যক্তিগত তথ্য, নিরাপত্তা ও স্বাস্থ্য বিধি" else "Language, personal info, privacy lock & clinical sources",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }

        // 0. Personal Information Section (View/Edit Name & DOB)
        item {
            val dobDate = remember(profile.birthDateMillis) {
                val cal = Calendar.getInstance().apply { timeInMillis = profile.birthDateMillis }
                val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                sdf.format(cal.time)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(LightPink),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Personal Info",
                                    tint = DeepPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = HerCareStrings.personalInfoTitle(language),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryText
                                )
                                Text(
                                    text = HerCareStrings.personalInfoSubtitle(language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryText
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, DeepPink),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepPink),
                            modifier = Modifier.testTag("edit_personal_info_button")
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(16.dp),
                                tint = DeepPink
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == AppLanguage.BANGLA) "সম্পাদনা" else "Edit",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = DeepPink
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(LightPink.copy(alpha = 0.4f))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = HerCareStrings.enterName(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryText
                            )
                            Text(
                                text = profile.name.ifBlank { if (language == AppLanguage.BANGLA) "দেয়া হয়নি" else "Not set" },
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryText
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = HerCareStrings.dateOfBirth(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryText
                            )
                            Text(
                                text = dobDate,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryText
                            )
                        }
                    }
                }
            }
        }

        // 1. Language Preference Section (English & Bangla)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(LightPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Language",
                                tint = DeepPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = HerCareStrings.languagePreference(language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = language == AppLanguage.ENGLISH,
                            onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                            label = { Text("English", fontWeight = FontWeight.Bold) },
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepPink,
                                selectedLabelColor = Color.White,
                                containerColor = LightPink.copy(alpha = 0.4f),
                                labelColor = PrimaryText
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("select_english_button")
                        )
                        FilterChip(
                            selected = language == AppLanguage.BANGLA,
                            onClick = { onLanguageChange(AppLanguage.BANGLA) },
                            label = { Text("বাংলা (Bangla)", fontWeight = FontWeight.Bold) },
                            shape = RoundedCornerShape(14.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepPink,
                                selectedLabelColor = Color.White,
                                containerColor = LightPink.copy(alpha = 0.4f),
                                labelColor = PrimaryText
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("select_bangla_button")
                        )
                    }
                }
            }
        }

        // 2. Biometric Security & App Lock Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).padding(end = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(LightPink),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = "Security",
                                    tint = DeepPink,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = HerCareStrings.biometricTitle(language),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryText
                                )
                                Text(
                                    text = HerCareStrings.biometricDescription(language),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )
                            }
                        }

                        Switch(
                            checked = profile.isBiometricEnabled,
                            onCheckedChange = { isChecked ->
                                onToggleBiometric(isChecked, if (isChecked) "1234" else null)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DeepPink,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = SoftRose.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("biometric_switch")
                        )
                    }
                }
            }
        }

        // 3. 🔔 NOTIFICATION SYSTEM & TESTING PANEL (CRITICAL REQUIREMENT)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(LightPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = DeepPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = HerCareStrings.notificationsSection(language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DeepPink
                            )
                            Text(
                                text = if (language == AppLanguage.BANGLA) "অ্যান্ড্রয়েড সিস্টেম-লেভেল লোকাল অ্যালার্ট টেস্ট করুন" else "Test system-level local notifications",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!notificationPermissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (language == AppLanguage.BANGLA) "নোটিফিকেশন অনুমতি প্রদান করুন" else "Grant Notification Permission",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        text = if (language == AppLanguage.BANGLA) "৫টি আবশ্যকীয় নোটিফিকেশন যাচাই করুন:" else "Test Required Notification Types:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Medicine Reminder Button
                    OutlinedButton(
                        onClick = onTestMedicine,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SoftRose),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = LightPink.copy(alpha = 0.25f),
                            contentColor = DeepPink
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("test_medicine_notification_button")
                    ) {
                        Text(
                            text = "💊  " + HerCareStrings.testMedicineNotification(language),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = DeepPink
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Period Reminder Button
                    OutlinedButton(
                        onClick = onTestPeriod,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SoftRose),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = LightPink.copy(alpha = 0.25f),
                            contentColor = DeepPink
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("test_period_notification_button")
                    ) {
                        Text(
                            text = "🩸  " + HerCareStrings.testPeriodNotification(language),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = DeepPink
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Irregular Cycle Warning Button
                    OutlinedButton(
                        onClick = onTestIrregular,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SoftRose),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = LightPink.copy(alpha = 0.25f),
                            contentColor = DeepPink
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("test_irregular_notification_button")
                    ) {
                        Text(
                            text = "🔴  " + HerCareStrings.testIrregularNotification(language),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = DeepPink
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Pad Purchase Reminder Button
                    OutlinedButton(
                        onClick = onTestPad,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SoftRose),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = LightPink.copy(alpha = 0.25f),
                            contentColor = DeepPink
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("test_pad_notification_button")
                    ) {
                        Text(
                            text = "🧻  " + HerCareStrings.testPadNotification(language),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = DeepPink
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 5. Health Warning Button
                    OutlinedButton(
                        onClick = onTestHealth,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SoftRose),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = LightPink.copy(alpha = 0.25f),
                            contentColor = DeepPink
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("test_health_notification_button")
                    ) {
                        Text(
                            text = "⚠️  " + HerCareStrings.testHealthNotification(language),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = DeepPink
                        )
                    }
                }
            }
        }

        // 4. Medical Sources & Scientific Foundation
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(22.dp), spotColor = CardSoftShadowColor),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = HerCareStrings.medicalSourcesTitle(language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = DeepPink
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = HerCareStrings.medicalSourcesText(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // 5. Official App Branding Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(LightPink),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hercare_icon),
                        contentDescription = "HerCare Logo",
                        modifier = Modifier.size(56.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "HerCare — Women's Health & Cycle Care",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = DeepPink
                )
                Text(
                    text = "Version 1.0 • Offline-First & Private",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
