package com.yasincidem.eyedropper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.service.voice.VoiceInteractionSession
import java.io.File
import java.io.FileOutputStream

class InteractionSession(context: Context) : VoiceInteractionSession(context) {

    override fun onHandleScreenshot(screenshot: Bitmap?) {
        super.onHandleScreenshot(screenshot)
        if (screenshot == null) {
            hide()
        } else {
            val path = screenshot.path()
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("screenshot", path)
            }
            hide()
            context.startActivity(intent)
        }
    }

    private fun Bitmap.path(): String {
        val scrFile = createScreenshotFile()

        FileOutputStream(scrFile).use { stream ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 0, stream)
            } else {
                compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
        return scrFile.path
    }

    private fun createScreenshotFile(): File {
        val scrDir = File(context.cacheDir, "screenshots")
        val scrFile = File(scrDir, "scr")

        scrDir.mkdir()
        if (scrFile.exists()) {
            scrFile.delete()
        }
        scrFile.createNewFile()

        return scrFile
    }

}