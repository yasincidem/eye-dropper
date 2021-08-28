package com.yasincidem.eyedropper.ui.feature.appicons.data

import android.content.Intent
import android.content.pm.PackageInfo
import javax.inject.Inject

class AppIconsRepository @Inject constructor(
    private val dataSource: AppIconsLocalDataSource
) {
    fun getAppIcons(): List<PackageInfo> = dataSource.getAppIcons()
    fun getLaunchIntent(packageName: String): Intent? = dataSource.getLaunchIntent(packageName)
}