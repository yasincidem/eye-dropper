package com.yasincidem.eyedropper

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yasincidem.eyedropper.ext.hideKeyboard
import com.yasincidem.eyedropper.service.ScreenCaptureService


class ScreenShotActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK)
                startService(ScreenCaptureService.getStartIntent(this, resultCode, data))
            else
                finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideKeyboard()
        startProjection()
        stopProjection()
    }

    private fun startProjection() {
        val mProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE)
    }

    private fun stopProjection() {
        startService(ScreenCaptureService.getStopIntent(this))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}