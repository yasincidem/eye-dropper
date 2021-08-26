package com.yasincidem.eyedropper.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.net.Uri
import com.yasincidem.eyedropper.image.AppIconFetcher

class InstalledPackagesDataSource(
    val context: Context
) {

    @SuppressLint("QueryPermissionsNeeded")
    fun getInstalledPackages(): List<PackageInfo> = context.packageManager.getInstalledPackages(0)

    fun getInstalledLaunchablePackages(): List<Uri> {
        return getInstalledPackages().filter {
            context.packageManager.getLaunchIntentForPackage(it.packageName) != null
        }.sortedByDescending {
            it.lastUpdateTime
        }.map {
            Uri.parse("${AppIconFetcher.SCHEME_PNAME}:${it.applicationInfo.packageName}")
        }
    }
}