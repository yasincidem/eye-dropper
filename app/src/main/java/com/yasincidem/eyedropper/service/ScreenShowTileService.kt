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

        startActivityAndCollapse(Intent(this, ScreenShotActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            addFlags(FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        })

        startVibrating()
    }

    private fun startVibrating() {
        serviceScope.launch {

            val v:Vibrator
            @Suppress("DEPRECATION")
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    v = getSystemService(VIBRATOR_MANAGER_SERVICE) as Vibrator
                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    v = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                else -> {
                    v = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(100)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}