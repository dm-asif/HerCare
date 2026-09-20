package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// HerCare Exact Color System (Mandated by Design Specifications)
val PrimaryPink = Color(0xFFE91E63)          // Primary brand color
val DeepPink = Color(0xFFC2185B)             // Deep Pink / Magenta accents
val RoseAccent = Color(0xFFEC407A)           // Rose
val SoftPink = Color(0xFFF8BBD0)             // Soft Pink borders and highlights
val LightPink = Color(0xFFFCE4EC)            // Light Pink chips, badges, containers
val VeryLightBackground = Color(0xFFFFF7FA)  // Very Light Background
val PureWhite = Color(0xFFFFFFFF)            // Pure White cards and surfaces
val PrimaryText = Color(0xFF35152B)          // Deep plum primary text
val SecondaryText = Color(0xFF76586C)        // Soft plum secondary text
val SoftPurpleAccent = Color(0xFF9C6BB7)     // Soft purple accent
val SuccessGreen = Color(0xFF4CAF7A)         // Success
val WarningAmber = Color(0xFFF2A93B)         // Warning
val DangerRed = Color(0xFFE53935)            // Danger

// Aliases and UI Surface Tokens
val AppBackground = VeryLightBackground
val CardBackground = PureWhite
val CardBorderColor = Color(0xFFFFDFEA)
val CardSoftShadowColor = Color(0x1AD81B60)
val GlassmorphicCard = Color(0xCCFFFFFF)
val GlassmorphicBorder = Color(0x66F8BBD0)

// Gradients
val HeroGradientStart = Color(0xFFFFF0F5)
val HeroGradientEnd = Color(0xFFFFDFEC)
val PillGradientStart = Color(0xFFEC407A)
val PillGradientEnd = Color(0xFFC2185B)

// Backward Compatibility Aliases
val SoftRose = RoseAccent
val VeryLightPink = VeryLightBackground
val AccentPurple = SoftPurpleAccent
val WarningRed = DangerRed
val WarningRedContainer = Color(0xFFFFEBEE)
val FertileGreen = SuccessGreen
val FertileGreenContainer = Color(0xFFE8F8F0)
val CoralPeriod = DangerRed
val RosePeriod = PrimaryPink
val SoftPinkPeriod = LightPink

val PinkPrimary = PrimaryPink
val PinkOnPrimary = PureWhite
val PinkPrimaryContainer = LightPink
val PinkOnPrimaryContainer = DeepPink

val RoseSecondary = RoseAccent
val RoseOnSecondary = PureWhite
val RoseSecondaryContainer = LightPink
val RoseOnSecondaryContainer = PrimaryText

val LilacTertiary = SoftPurpleAccent
val LilacOnTertiary = PureWhite
val LilacTertiaryContainer = Color(0xFFF6EEFA)
val LilacOnTertiaryContainer = PrimaryText

val TealPrimary = PinkPrimary
val TealOnPrimary = PinkOnPrimary
val TealPrimaryContainer = PinkPrimaryContainer
val TealOnPrimaryContainer = PinkOnPrimaryContainer

val EmeraldSecondary = RoseSecondary
val EmeraldOnSecondary = RoseOnSecondary
val EmeraldSecondaryContainer = RoseSecondaryContainer
val EmeraldOnSecondaryContainer = RoseOnSecondaryContainer

val MintTertiary = LilacTertiary
val MintOnTertiary = LilacOnTertiary
val MintTertiaryContainer = LilacTertiaryContainer
val MintOnTertiaryContainer = LilacOnTertiaryContainer

val NeutralBackground = AppBackground
val NeutralSurface = CardBackground
val NeutralSurfaceVariant = LightPink
val NeutralOnSurface = PrimaryText
val NeutralOnSurfaceVariant = SecondaryText
val NeutralOutline = CardBorderColor

val DarkTextPrimary = PrimaryText
val DarkTextSecondary = SecondaryText

// Dark theme is intentionally mapped to a high-contrast elegant light rose-plum palette
val PinkPrimaryDark = PrimaryPink
val PinkOnPrimaryDark = PureWhite
val PinkPrimaryContainerDark = LightPink
val PinkOnPrimaryContainerDark = DeepPink

val RoseSecondaryDark = RoseAccent
val RoseOnSecondaryDark = PureWhite
val RoseSecondaryContainerDark = LightPink
val RoseOnSecondaryContainerDark = PrimaryText

val LilacTertiaryDark = SoftPurpleAccent
val LilacOnTertiaryDark = PureWhite
val LilacTertiaryContainerDark = Color(0xFFF6EEFA)
val LilacOnTertiaryContainerDark = PrimaryText

val NeutralBackgroundDark = VeryLightBackground
val NeutralSurfaceDark = PureWhite
val NeutralSurfaceVariantDark = LightPink
val NeutralOnSurfaceDark = PrimaryText
val NeutralOnSurfaceVariantDark = SecondaryText
val NeutralOutlineDark = CardBorderColor


