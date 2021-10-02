package com.yasincidem.eyedropper.appstartup

import android.content.Context
import androidx.startup.Initializer
import coil.Coil
import coil.ImageLoader
import coil.util.CoilUtils
import com.yasincidem.eyedropper.image.AppIconFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class CoilInitializer : Initializer<Unit>, CoroutineScope by MainScope() {

    override fun create(context: Context) {
        launch(Dispatchers.IO) {
            setCoilImageLoader(context)
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    private fun setCoilImageLoader(appContext: Context) {
        Coil.setImageLoader(
            ImageLoader.Builder(appContext)
                .availableMemoryPercentage(0.25)
                .crossfade(true)
                .okHttpClient {
                    OkHttpClient.Builder()
                        .cache(CoilUtils.createDefaultCache(appContext))
                        .build()
                }
                .componentRegistry {
                    add(AppIconFetcher(appContext))
                }
                .build()
        )
    }
}