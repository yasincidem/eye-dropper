package com.yasincidem.eyedropper.ui.feature.appicons.data

import android.content.Context
import android.content.pm.PackageInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppIconsLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun fetchAppIcons(): List<PackageInfo> = context.packageManager.getInstalledPackages(0)
}