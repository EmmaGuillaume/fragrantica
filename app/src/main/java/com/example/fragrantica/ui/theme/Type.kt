package com.fragrantica.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.fragrantica.R

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation

@OptIn(ExperimentalTextApi::class)
private fun variableFont(resId: Int, weight: FontWeight) = Font(
    resId = resId,
    weight = weight,
    style = FontStyle.Normal,
    variationSettings = FontVariation.Settings(weight, FontStyle.Normal),
)

val Manrope = FontFamily(
    variableFont(R.font.manrope_variable, FontWeight.Normal),
    variableFont(R.font.manrope_variable, FontWeight.Medium),
    variableFont(R.font.manrope_variable, FontWeight.SemiBold),
    variableFont(R.font.manrope_variable, FontWeight.Bold),
)

val Inter = FontFamily(
    variableFont(R.font.inter_variable, FontWeight.Normal),
    variableFont(R.font.inter_variable, FontWeight.Medium),
    variableFont(R.font.inter_variable, FontWeight.SemiBold),
    variableFont(R.font.inter_variable, FontWeight.Bold),
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 40.sp),
    headlineLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),

    bodyLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp), // boutons
    labelMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp),
)