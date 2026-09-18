package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.HerCareStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun OnboardingScreen(
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onComplete: (
        name: String,
        birthDateMillis: Long,
        lastPeriodStartMillis: Long,
        cycleLength: Int,
        periodDuration: Int,
        initialPads: Int,
        language: AppLanguage
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedLang by remember { mutableStateOf(currentLanguage) }
    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    // Date of Birth
    val calNow = Calendar.getInstance()
    var birthDateMillis by remember {
        mutableLongStateOf(
            Calendar.getInstance().apply { add(Calendar.YEAR, -24) }.timeInMillis
        )
    }
    var birthDateSelected by remember { mutableStateOf(false) }

    // Last Period Start Date (Defaults to 14 days ago)
    var lastPeriodStartMillis by remember {
        mutableLongStateOf(
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -14) }.timeInMillis
        )
    }

    var cycleLength by remember { mutableFloatStateOf(28f) }
    var periodDuration by remember { mutableFloatStateOf(5f) }
    var initialPads by remember { mutableIntStateOf(15) }

    val dateFormat = SimpleDateFormat(
        "MMM dd, yyyy",
        if (selectedLang == AppLanguage.BANGLA) Locale("bn") else Locale.ENGLISH
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Official HerCare Logo Branding
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hercare_icon),
                contentDescription = "HerCare Official Logo",
                modifier = Modifier.size(88.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = HerCareStrings.welcomeTitle(selectedLang),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = HerCareStrings.onboardingSubtitle(selectedLang),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language Choice Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = HerCareStrings.chooseLanguage(selectedLang),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterChip(
                        selected = selectedLang == AppLanguage.ENGLISH,
                        onClick = {
                            selectedLang = AppLanguage.ENGLISH
                            onLanguageChange(AppLanguage.ENGLISH)
                        },
                        label = { Text("English") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedLang == AppLanguage.BANGLA,
                        onClick = {
                            selectedLang = AppLanguage.BANGLA
                            onLanguageChange(AppLanguage.BANGLA)
                        },
                        label = { Text("বাংলা") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 1. Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) nameError = false
                    },
                    label = { Text(HerCareStrings.enterName(selectedLang)) },
                    placeholder = { Text(if (selectedLang == AppLanguage.BANGLA) "যেমন: সারা" else "e.g. Sarah") },
                    isError = nameError,
                    supportingText = {
                        if (nameError) {
                            Text(
                                if (selectedLang == AppLanguage.BANGLA) "অনুগ্রহ করে আপনার নাম লিখুন" else "Please enter your name",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Female, contentDescription = "Name")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Date of Birth
                Text(
                    text = HerCareStrings.dateOfBirth(selectedLang),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance().apply { timeInMillis = birthDateMillis }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val selected = Calendar.getInstance().apply {
                                    set(year, month, dayOfMonth, 0, 0, 0)
                                }
                                birthDateMillis = selected.timeInMillis
                                birthDateSelected = true
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "DOB")
                    Spacer(modifier = Modifier.width(8.dp))
                    val age = ((System.currentTimeMillis() - birthDateMillis) / (365.25 * 24 * 60 * 60 * 1000L)).toInt()
                    Text(
                        text = "${dateFormat.format(Date(birthDateMillis))} " +
                                if (selectedLang == AppLanguage.BANGLA) "($age বছর)" else "($age yrs)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Last Period Start Date (Includes Historical Dates)
                Text(
                    text = HerCareStrings.lastPeriodStartDate(selectedLang),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = HerCareStrings.selectOlderDateHint(selectedLang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = {
                        val cal = Calendar.getInstance().apply { timeInMillis = lastPeriodStartMillis }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val selected = Calendar.getInstance().apply {
                                    set(year, month, dayOfMonth, 0, 0, 0)
                                }
                                lastPeriodStartMillis = selected.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            // Can select any historical date up to today
                            datePicker.maxDate = System.currentTimeMillis()
                        }.show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("last_period_picker_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Last Period Date")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateFormat.format(Date(lastPeriodStartMillis)),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Average Cycle Length (21 to 35 days standard)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = HerCareStrings.averageCycleLength(selectedLang),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = if (selectedLang == AppLanguage.BANGLA) "${cycleLength.toInt()} দিন" else "${cycleLength.toInt()} days",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = HerCareStrings.cycleLengthHint(selectedLang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = cycleLength,
                    onValueChange = { cycleLength = it },
                    valueRange = 21f..35f,
                    steps = 13,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Average Period Duration (2 to 7 days standard)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = HerCareStrings.averagePeriodDuration(selectedLang),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = if (selectedLang == AppLanguage.BANGLA) "${periodDuration.toInt()} দিন" else "${periodDuration.toInt()} days",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = HerCareStrings.periodDurationHint(selectedLang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = periodDuration,
                    onValueChange = { periodDuration = it },
                    valueRange = 2f..7f,
                    steps = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 6. Starting Pad Stock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = HerCareStrings.initialPadsCount(selectedLang),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = if (selectedLang == AppLanguage.BANGLA) "$initialPads টি" else "$initialPads pads",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = initialPads.toFloat(),
                    onValueChange = { initialPads = it.toInt() },
                    valueRange = 0f..40f,
                    steps = 39,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isBlank()) {
                    nameError = true
                } else {
                    onComplete(
                        name.trim(),
                        birthDateMillis,
                        lastPeriodStartMillis,
                        cycleLength.toInt(),
                        periodDuration.toInt(),
                        initialPads,
                        selectedLang
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("get_started_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = HerCareStrings.getStarted(selectedLang),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = "Privacy",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (selectedLang == AppLanguage.BANGLA) "আপনার স্বাস্থ্য তথ্য সম্পূর্ণ নিরাপদ এবং আপনার ফোনেই সংরক্ষিত থাকে।" else "Your sensitive health data stays strictly private on your device.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
