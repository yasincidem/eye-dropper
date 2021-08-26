package com.yasincidem.eyedropper.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yasincidem.eyedropper.R

// Set of Material typography styles to start with

val AtkinsonHyperlegibleFontFamily = FontFamily(
    Font(R.font.atkinson_hyperlegible_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold),
    Font(R.font.atkinson_hyperlegible_bold_italic, FontWeight.Bold, FontStyle.Italic)
)

val Typography = Typography(
    defaultFontFamily = AtkinsonHyperlegibleFontFamily,
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.15.sp,
        color = Color.Gray
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)