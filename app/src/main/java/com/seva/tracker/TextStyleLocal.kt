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
}