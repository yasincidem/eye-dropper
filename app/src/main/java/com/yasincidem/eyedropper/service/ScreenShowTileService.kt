package com.yasincidem.eyedropper.service

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.service.quicksettings.TileService
import com.yasincidem.eyedropper.ScreenShotActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class ScreenShowTileService : TileService() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onClick() {
        super.onClick()


        startVibrating()
        startActivityAndCollapse(Intent(this, ScreenShotActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            addFlags(FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        })
    }

    private fun startVibrating() {
        serviceScope.launch {
            val vibrator = (getSystemService(VIBRATOR_SERVICE) as Vibrator)
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                vibrator.vibrate(VibrationEffect.createOneShot(100, 200))
            else
                vibrator.vibrate(100)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}