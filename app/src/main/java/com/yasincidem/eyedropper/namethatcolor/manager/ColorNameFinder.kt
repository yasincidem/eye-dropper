package com.yasincidem.eyedropper.namethatcolor.manager

import com.yasincidem.eyedropper.namethatcolor.exception.ColorNotFoundException
import com.yasincidem.eyedropper.namethatcolor.model.Color
import com.yasincidem.eyedropper.namethatcolor.model.HexColor
import com.yasincidem.eyedropper.namethatcolor.util.colorsMaterialNames
import com.yasincidem.eyedropper.namethatcolor.util.colorsNames
import com.yasincidem.eyedropper.namethatcolor.util.hsl
import com.yasincidem.eyedropper.namethatcolor.util.rgb
import kotlin.math.pow

object ColorNameFinder {

    private var colors: List<Color> =
        colorsNames.map { entry -> Color(entry.key, entry.value, entry.key.rgb(), entry.key.hsl()) }
    private var materialColors: List<Color> = colorsMaterialNames.map { entry ->
        Color(entry.key,
            entry.value,
            entry.key.rgb(),
            entry.key.hsl())
    }

    /**
     * look for the Color of an hexadecimal color
     */
    fun findColor(color: HexColor) = find(color, colors)

    /**
     * look for the Color of an hexadecimal material color
     */
    fun findMaterialColor(color: HexColor) = find(color, materialColors)

    /**
     * look for the Color of an hexadecimal color
     */
    private fun find(color: HexColor, colors: List<Color>): Pair<HexColor, Color> {

        val (r, g, b) = color.rgb()
        val (h, s, l) = color.hsl()

        var cl = -1
        var df = -1

        colors.forEachIndexed { index, col ->

            if (color.value == col.hexCode) return color to col
            else {
                val ndf1 = (r - col.rgb.r).toDouble().pow(2.0).toInt() + (g - col.rgb.g).toDouble()
                    .pow(2.0).toInt() + (b - col.rgb.b).toDouble().pow(2.0).toInt()
                val ndf2 = (h - col.hsl.h).toDouble().pow(2.0).toInt() + (s - col.hsl.s).toDouble()
                    .pow(2.0).toInt() + (l - col.hsl.l).toDouble().pow(2.0).toInt()
                val ndf = ndf1 + ndf2 * 2
                if (df < 0 || ndf < df) {
                    df = ndf
                    cl = index
                }
            }
        }

        // if not found a close by one, we return an error
        if (cl < 0) throw ColorNotFoundException()
        // if found, return the name
        return color to colors[cl]
    }
}