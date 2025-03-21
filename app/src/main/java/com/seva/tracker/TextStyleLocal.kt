package com.seva.tracker

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

val TextUnit.nonScaledSp
    @Composable
    get() = (this.value / LocalDensity.current.fontScale).sp

object TextStyleLocal {
    val headerLarge: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotomedium)),
            fontSize = 70.0.sp,
            letterSpacing = (0.0).sp,
            lineHeight = 70.0.sp.nonScaledSp
        )
    val headerLarge2: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotomedium)),
            fontSize = 40.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 40.0.sp.nonScaledSp
        )

    val robotoBold26: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotobold)),
            fontSize = 26.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 32.0.sp.nonScaledSp
        )
    val headerSmall: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotolight)),
            fontSize = 22.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 22.0.sp.nonScaledSp
        )

    val regular12: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotolight)),
            fontSize = 12.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 12.0.sp.nonScaledSp
        )

    val regular14: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotolight)),
            fontSize = 14.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 14.0.sp.nonScaledSp
        )

    val regular16: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotolight)),
            fontSize = 16.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 16.0.sp.nonScaledSp
        )
    val semibold14: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotomedium)),
            fontSize = 14.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 14.0.sp.nonScaledSp
        )

    val semibold16: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotomedium)),
            fontSize = 16.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 16.0.sp.nonScaledSp
        )


    val semibold18: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotomedium)),
            fontSize = 18.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 18.0.sp.nonScaledSp
        )
    val semibold20: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotomedium)),
            fontSize = 20.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 20.0.sp.nonScaledSp
        )
    val semibold24: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotomedium)),
            fontSize = 24.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 24.0.sp.nonScaledSp
        )
    val semibold30: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotomedium)),
            fontSize = 30.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 30.0.sp.nonScaledSp
        )

    val bold16: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotobold)),
            fontSize = 16.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 16.0.sp.nonScaledSp
        )
    val bold18: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotobold)),
            fontSize = 18.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 18.0.sp.nonScaledSp
        )
    val bold22: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotobold)),
            fontSize = 22.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 22.0.sp.nonScaledSp
        )
    val bold24: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = FontFamily(Font(R.font.robotobold)),
            fontSize = 24.0.sp.nonScaledSp,
            letterSpacing = (0.0).sp,
            lineHeight = 24.0.sp.nonScaledSp
        )


//
//    val digitalNumbers: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(R.font.DigitalNumbers_Regular)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//    val bungee: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.bungeeregular)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//    val audiowide: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.Audiowide_Regular)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//
//    val zkoolkualite: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.ZCOOLKuaiLe_Regular)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//    val orbitron: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.Orbitron_VariableFont_wght)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//    val monoton: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.Monoton_Regular)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//    val bungeeinline: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.BungeeInline_Regular)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//    val goldman: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.Goldman_Regular)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//
//    val limelight: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.Limelight_Regular)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//    val bruno: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.BrunoAceSC_Regular)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
//    val pressstart: TextStyle
//        @Composable
//        get() = TextStyle(
//            fontFamily = FontFamily(Font(Res.font.presstart)),
//            fontSize = 18.0.sp.nonScaledSp,
//            letterSpacing = (0.0).sp,
//            lineHeight = 18.0.sp.nonScaledSp
//        )
}