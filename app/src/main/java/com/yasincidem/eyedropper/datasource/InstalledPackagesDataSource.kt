package com.yasincidem.eyedropper.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo

class InstalledPackagesDataSource(
    val context: Context
) {

    @SuppressLint("QueryPermissionsNeeded")
    fun getInstalledPackages(): MutableList<PackageInfo> = context.packageManager.getInstalledPackages(0)

}