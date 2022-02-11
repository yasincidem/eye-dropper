package com.yasincidem.eyedropper.service

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.service.quicksettings.TileService
import com.yasincidem.eyedropper.ScreenShotActivity

class ScreenShowTileService : TileService() {

    override fun onClick() {
        super.onClick()

        startActivityAndCollapse(Intent(this, ScreenShotActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            addFlags(FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        })
    }

}