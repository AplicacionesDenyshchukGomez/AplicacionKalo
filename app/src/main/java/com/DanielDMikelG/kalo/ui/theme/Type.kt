package com.DanielDMikelG.kalo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.DanielDMikelG.kalo.R

val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_black, FontWeight.Black),
)

// Estilo "cartoon": numeros y titulos muy gordos, nada de pesos finos.
val Typography = Typography(
    // Numeros grandes (kcal restantes)
    displayMedium = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.Black,
        fontSize = 44.sp, lineHeight = 48.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.Black,
        fontSize = 36.sp, lineHeight = 40.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.Black,
        fontSize = 30.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp, lineHeight = 34.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp, lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp, lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.Bold,
        fontSize = 16.sp, lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.Bold,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.ExtraBold,
        fontSize = 12.sp, lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.Bold,
        fontSize = 11.sp, lineHeight = 16.sp,
    ),
)
