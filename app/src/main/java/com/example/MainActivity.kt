package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.security.BiometricHelper
import com.example.ui.components.BiometricLockScreen
import com.example.ui.navigation.HerCareAppContent
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.HerCareTheme
import com.example.ui.viewmodel.HerCareViewModel

class MainActivity : FragmentActivity() {

    private val viewModel: HerCareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            // Notification Permission Launcher (Android 13+)
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* Handled */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            HerCareTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when {
                        // App is locked by Biometrics or PIN
                        uiState.isAppLocked -> {
                            BiometricLockScreen(
                                language = uiState.language,
                                onTriggerBiometric = {
                                    if (BiometricHelper.isBiometricAvailable(this@MainActivity)) {
                                        BiometricHelper.promptBiometric(
                                            activity = this@MainActivity,
                                            title = if (uiState.language.code == "bn") "HerCare আনলক করুন" else "Unlock HerCare",
                                            subtitle = if (uiState.language.code == "bn") "আপনার স্বাস্থ্য তথ্য দেখতে আঙ্গুলের ছাপ দিন" else "Verify your biometric to access private health logs",
                                            onSuccess = { viewModel.unlockApp() },
                                            onError = { errMsg ->
                                                Toast.makeText(this@MainActivity, errMsg, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            if (uiState.language.code == "bn") "ডিভাইসে কোনো বায়োমেট্রিক সেন্সর পাওয়া যায়নি। পিন ব্যবহার করুন।" else "No biometrics enrolled. Use PIN.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                onPinUnlock = { enteredPin ->
                                    val savedPin = uiState.userProfile.pinCode ?: "1234"
                                    if (enteredPin == savedPin) {
                                        viewModel.unlockApp()
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )
                        }

                        // First Launch / Onboarding
                        !uiState.isOnboardingComplete -> {
                            OnboardingScreen(
                                currentLanguage = uiState.language,
                                onLanguageChange = { viewModel.setLanguage(it) },
                                onComplete = { name, dob, lastPeriod, cycleLen, periodDur, pads, lang ->
                                    viewModel.completeOnboarding(
                                        name = name,
                                        birthDateMillis = dob,
                                        lastPeriodStartMillis = lastPeriod,
                                        cycleLength = cycleLen,
                                        periodDuration = periodDur,
                                        initialPads = pads,
                                        lang = lang
                                    )
                                }
                            )
                        }

                        // Main HerCare Application Dashboard & Navigation
                        else -> {
                            HerCareAppContent(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
