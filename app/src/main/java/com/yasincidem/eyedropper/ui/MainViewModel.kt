package com.yasincidem.eyedropper.ui

import androidx.annotation.ColorInt
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModel
import com.yasincidem.eyedropper.namethatcolor.exception.ColorNotFoundException
import com.yasincidem.eyedropper.namethatcolor.manager.ColorNameFinder
import com.yasincidem.eyedropper.namethatcolor.model.HexColor
import com.yasincidem.eyedropper.ui.theme.BrandyPunch
import com.yasincidem.eyedropper.ui.theme.Highland
import com.yasincidem.eyedropper.ui.theme.HippieBlue
import com.yasincidem.eyedropper.ui.theme.Koromiko
import com.yasincidem.eyedropper.ui.theme.LavenderPurple
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val snakeRegex = "_[a-zA-Z0-9]".toRegex()

    fun getColorName(@ColorInt color: Int?): String = try {
        getColorPair(color).second.name.also {
            it.replace(" ", "\n")
        }
    } catch (ex: IllegalArgumentException) {
        ""
    } catch (ex: ColorNotFoundException) {
        ""
    }

    private fun getMaterialColorName(@ColorInt color: Int?): String = try {
        ColorNameFinder.findMaterialColor(HexColor(Integer.toHexString(color ?: 0))).second.name
    } catch (ex: IllegalArgumentException) {
        ""
    } catch (ex: ColorNotFoundException) {
        ""
    }

    fun getHexColor(@ColorInt color: Int?, withNumberSign: Boolean = true): String = try {
        (if (withNumberSign) "#" else "") + getColorPair(color).first.value
    } catch (ex: IllegalArgumentException) {
        ""
    } catch (ex: ColorNotFoundException) {
        ""
    }

    fun getAnnotatedXmlColorText(colorResource: ColorResource) = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Koromiko)) {
            append("<color ")
        }
        append("""name""")
        withStyle(style = SpanStyle(color = Highland)) {
            append("""="${colorResource.key}"""")
        }
        withStyle(style = SpanStyle(color = Koromiko)) {
            append(">")
        }
        append(colorResource.value)
        withStyle(style = SpanStyle(color = Koromiko)) {
            append("</color>")
        }
    }

    fun getAnnotatedComposeColorText(colorResource: ColorResource) = buildAnnotatedString {
        withStyle(style = SpanStyle(color = BrandyPunch)) {
            append("val ")
        }
        withStyle(style = SpanStyle(color = LavenderPurple)) {
            append(colorResource.key)
        }
        append(" = ")
        withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
            append("Color")
        }
        append("(")
        withStyle(style = SpanStyle(color = HippieBlue)) {
            append(colorResource.value)
        }
        append(")")
    }

    fun getXmlColorResource(@ColorInt color: Int?) = ColorResource(
        getColorName(color).lowercase().replace(" ", "_"), getHexColor(color)
    )

    fun getComposeColorResource(@ColorInt color: Int?) = ColorResource(
        getColorName(color).replace(" ", ""),
        "0xFF" + getHexColor(color, withNumberSign = false).uppercase()
    )

    fun getXmlMaterialColorResource(@ColorInt color: Int?) = ColorResource(
        getMaterialColorName(color).lowercase().replace(" ", "_"), getHexColor(color)
    )

    fun getComposeMaterialColorResource(@ColorInt color: Int?) = ColorResource(
        getMaterialColorName(color).replace(" ", "").snakeToUpperCamelCase(),
        "0xFF" + getHexColor(color, withNumberSign = false).uppercase()
    )

    private fun getColorPair(@ColorInt color: Int?) =
        ColorNameFinder.findColor(HexColor(Integer.toHexString(color ?: 0)))

    private fun String.snakeToLowerCamelCase(): String {
        return snakeRegex.replace(this) {
            it.value.replace("_", "")
                .uppercase()
        }
    }

    private fun String.snakeToUpperCamelCase(): String {
        return this.snakeToLowerCamelCase().replaceFirstChar { it.uppercase() }
    }
}

data class ColorResource(val key: String, val value: String)