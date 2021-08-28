package com.yasincidem.eyedropper.image

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.util.Log
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.size.Size

class AppIconFetcher(private val context: Context) : Fetcher<Uri> {

    private val pm: PackageManager
    private val dpi: Int
    private var defaultAppIcon: Bitmap? = null

    private val fullResDefaultActivityIcon: Bitmap
        get() {
            if (defaultAppIcon == null) {
                val drawable: Drawable? =
                    Resources.getSystem().getDrawableForDensity(
                        android.R.mipmap.sym_def_app_icon, dpi, null
                    )
                defaultAppIcon = drawableToBitmap(drawable)
            }
            return defaultAppIcon ?: drawableToBitmap(ColorDrawable(Color.TRANSPARENT))
        }


    init {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        dpi = am.launcherLargeIconDensity
        pm = context.packageManager
    }

    override fun key(data: Uri): String = data.toString()

    override fun handles(data: Uri) = data.scheme == SCHEME_PNAME

    override suspend fun fetch(
        pool: BitmapPool,
        data: Uri,
        size: Size,
        options: Options
    ): FetchResult {
        val iconBitmap = getFullResIcon(
            data.toString().split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()[1]
        )
        val iconDrawable = BitmapDrawable(context.resources, iconBitmap)
        return DrawableResult(
            drawable = iconDrawable,
            isSampled = true,
            dataSource = DataSource.DISK
        )
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun getFullResIcon(packageName: String): Bitmap {
        return getFullResIcon(pm.getApplicationInfo(packageName, 0))
    }

    private fun getFullResIcon(info: ApplicationInfo): Bitmap {
        try {
            val resources = pm.getResourcesForApplication(info.packageName)
            val iconId = info.icon
            if (iconId != 0) {
                return getFullResIcon(resources, iconId)
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
            // TODO()
        }

        return fullResDefaultActivityIcon
    }

    private fun getFullResIcon(resources: Resources, iconId: Int): Bitmap {
        val drawable: Drawable?

        try {
            drawable = resources.getDrawableForDensity(iconId, dpi, null)
        } catch (e: Resources.NotFoundException) {
            return fullResDefaultActivityIcon
        }

        return drawableToBitmap(drawable)
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        } else if (drawable is PictureDrawable) {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            canvas.drawPicture(drawable.picture)
            return bitmap
        }
        var width = drawable?.intrinsicWidth ?: 0
        width = if (width > 0) width else 1
        var height = drawable?.intrinsicHeight ?: 0
        height = if (height > 0) height else 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        return bitmap
    }

    companion object {
        const val SCHEME_PNAME = "pname"
    }

}
