package com.yasincidem.eyedropper.ui.feature.appicons.domain.usecase

import android.net.Uri
import com.yasincidem.eyedropper.image.AppIconFetcher
import com.yasincidem.eyedropper.ui.feature.appicons.data.AppIconsRepository
import javax.inject.Inject

class GetFilteredAppIconsUseCase @Inject constructor(
    private val appIconsRepository: AppIconsRepository
) {

    operator fun invoke(): List<Uri> = with(appIconsRepository) {
        getAppIcons().filter {
            getLaunchIntent(it.packageName) != null
        }.sortedByDescending {
            it.lastUpdateTime
        }.map {
            Uri.parse("${AppIconFetcher.SCHEME_PNAME}:${it.applicationInfo.packageName}")
        }
    }
}