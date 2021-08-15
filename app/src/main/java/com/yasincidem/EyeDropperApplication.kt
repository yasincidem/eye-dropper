package com.yasincidem

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.yasincidem.eyedropper.image.AppIconFetcher

class EyeDropperApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .componentRegistry {
                add(AppIconFetcher(this@EyeDropperApplication))
            }
            .build()
    }

}