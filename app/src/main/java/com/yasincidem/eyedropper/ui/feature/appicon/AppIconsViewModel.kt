package com.yasincidem.eyedropper.ui.feature.appicon

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yasincidem.eyedropper.image.AppIconFetcher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AppIconsViewModel(@ApplicationContext val context: Application) : AndroidViewModel(context) {

    private val _appIconUris = MutableStateFlow<List<Uri>>(listOf())
    val appIconUris: StateFlow<List<Uri>> = _appIconUris

    @SuppressLint("QueryPermissionsNeeded")
    fun getAppIconUris() = viewModelScope.launch {
        val packageManager = context.packageManager

        val packages = packageManager.getInstalledPackages(0).filter {
            context.packageManager.getLaunchIntentForPackage(it.packageName) != null
        }

        withContext(Dispatchers.IO) {

            val allApps =
                packages.sortedByDescending {
                    it.lastUpdateTime
                }.map {
                    Uri.parse("${AppIconFetcher.SCHEME_PNAME}:${it.applicationInfo.packageName}")
                }

            launch {
                _appIconUris.emit(allApps)
            }

        }
    }
}