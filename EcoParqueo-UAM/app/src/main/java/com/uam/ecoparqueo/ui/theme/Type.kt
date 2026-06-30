package com.uam.ecoparqueo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.uam.ecoparqueo.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Plus Jakarta Sans")

val PlusJakartaSans = FontFamily(
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = fontName, fontProvider = provider, weight = FontWeight.ExtraBold)
)

val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = PlusJakartaSans),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = PlusJakartaSans),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = PlusJakartaSans),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = PlusJakartaSans),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = PlusJakartaSans),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = PlusJakartaSans),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = PlusJakartaSans),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = PlusJakartaSans),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = PlusJakartaSans),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = PlusJakartaSans),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = PlusJakartaSans),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = PlusJakartaSans),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = PlusJakartaSans),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = PlusJakartaSans),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = PlusJakartaSans)
)