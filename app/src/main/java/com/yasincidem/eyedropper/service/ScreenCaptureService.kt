package com.yasincidem.eyedropper.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.yasincidem.eyedropper.MainActivity
import com.yasincidem.eyedropper.NotificationUtils
import java.io.File
import java.io.FileOutputStream

class ScreenCaptureService : Service() {

    private lateinit var file: File
    private var mMediaProjection: MediaProjection? = null
    private lateinit var mStoreDir: String
    private var mImageReader: ImageReader? = null
    private var mHandler: Handler? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mDensity = 0
    private var mWidth = 0
    private var mHeight = 0

    private inner class ImageAvailableListener : OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader) {

            mHandler?.postDelayed({
                mImageReader?.acquireLatestImage().use { image ->
                    if (image != null) {
                        val planes = image.planes
                        val buffer = planes[0].buffer
                        val pixelStride = planes[0].pixelStride
                        val rowStride = planes[0].rowStride
                        val rowPadding = rowStride - pixelStride * mWidth

                        val bitmap = Bitmap.createBitmap(
                            mWidth + rowPadding / pixelStride,
                            mHeight,
                            Bitmap.Config.ARGB_8888
                        )
                        bitmap?.copyPixelsFromBuffer(buffer)

                        val isSuccess = FileOutputStream("${file.path}/ss.png").use { stream ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                bitmap?.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 0, stream)
                            } else {
                                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                            }
                        }

                        if (isSuccess == true) {
                            startActivity(
                                Intent(
                                    this@ScreenCaptureService,
                                    MainActivity::class.java
                                ).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    putExtra("screenshot", file.path.plus("/ss.png"))
                                }
                            )
                        }

                        reader.close()
                        stopSelf()
                    }
                }
            }, 500L)
        }
    }

    private inner class MediaProjectionStopCallback : MediaProjection.Callback() {
        override fun onStop() {
            mHandler?.post {
                if (mVirtualDisplay != null) mVirtualDisplay?.release()
                if (mImageReader != null) mImageReader?.setOnImageAvailableListener(null, null)
                mMediaProjection?.unregisterCallback(this@MediaProjectionStopCallback)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        val externalFilesDir = getExternalFilesDir(null)
        if (externalFilesDir != null) {
            mStoreDir = externalFilesDir.absolutePath + "/screenshots/"
            file = File(mStoreDir)
            if (!file.exists()) {
                val success = file.mkdirs()
                if (!success) {
                    stopSelf()
                }
            }
        } else {
            stopSelf()
        }

        object : Thread() {
            override fun run() {
                Looper.prepare()
                mHandler = Handler()
                Looper.loop()
            }
        }.start()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when {
            isStartCommand(intent) -> {
                // create notification
                val notification = NotificationUtils.getNotification(this)
                startForeground(notification.first, notification.second)
                // start projection
                val resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED)
                val data = intent.getParcelableExtra<Intent>(DATA)
                startProjection(resultCode, data)
            }
            isStopCommand(intent) -> {
                stopProjection()
                stopSelf()
            }
            else -> {
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startProjection(resultCode: Int, data: Intent?) {
        val mpManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        if (mMediaProjection == null) {
            mMediaProjection = data?.let { mpManager.getMediaProjection(resultCode, it) }
            if (mMediaProjection != null) {
                mDensity = Resources.getSystem().displayMetrics.densityDpi
                createVirtualDisplay()
                mMediaProjection?.registerCallback(MediaProjectionStopCallback(), mHandler)
            }
        }
    }

    private fun stopProjection() {
        if (mHandler != null) {
            mHandler?.post {
                if (mMediaProjection != null) {
                    mMediaProjection?.stop()
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun createVirtualDisplay() {

        mWidth = Resources.getSystem().displayMetrics.widthPixels
        mHeight = Resources.getSystem().displayMetrics.heightPixels

        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 1)
        mVirtualDisplay = mMediaProjection?.createVirtualDisplay(
            SCREENCAP_NAME, mWidth, mHeight,
            mDensity, virtualDisplayFlags, mImageReader?.surface, null, mHandler
        )
        mImageReader?.setOnImageAvailableListener(ImageAvailableListener(), mHandler)
    }

    companion object {
        private const val TAG = "ScreenCaptureService"
        private const val RESULT_CODE = "RESULT_CODE"
        private const val DATA = "DATA"
        private const val ACTION = "ACTION"
        private const val START = "START"
        private const val STOP = "STOP"
        private const val SCREENCAP_NAME = "screencap"
        fun getStartIntent(context: Context?, resultCode: Int, data: Intent?): Intent {
            val intent = Intent(context, ScreenCaptureService::class.java)
            intent.putExtra(ACTION, START)
            intent.putExtra(RESULT_CODE, resultCode)
            intent.putExtra(DATA, data)
            return intent
        }

        fun getStopIntent(context: Context?): Intent {
            val intent = Intent(context, ScreenCaptureService::class.java)
            intent.putExtra(ACTION, STOP)
            return intent
        }

        private fun isStartCommand(intent: Intent) =
            (intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA) && intent.hasExtra(ACTION) && intent.getStringExtra(
                ACTION
            ) == START)

        private fun isStopCommand(intent: Intent) =
            intent.hasExtra(ACTION) && intent.getStringExtra(ACTION) == STOP

        private val virtualDisplayFlags: Int
            get() = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
    }
}