package com.yasincidem.eyedropper

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.PAN_LIMIT_CENTER
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ZOOM_FOCUS_CENTER
import com.yasincidem.eyedropper.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Color as ComposeColor


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val screenShotImageView: SubsamplingScaleImageView by lazy {
        binding.screenshot
    }
    private val colorContainer: ComposeView by lazy {
        binding.colorContainer
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val gestureDetector =
            GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    return super.onSingleTapConfirmed(e)
                }

                override fun onSingleTapUp(e: MotionEvent?): Boolean {

                    return super.onSingleTapUp(e)
                }

                override fun onContextClick(e: MotionEvent?): Boolean {

                    return super.onContextClick(e)
                }

                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    return super.onDoubleTap(e)
                }

                override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
                    return super.onDoubleTapEvent(e)
                }

                override fun onDown(e: MotionEvent?): Boolean {
                    return super.onDown(e)
                }

                override fun onLongPress(e: MotionEvent?) {

                    super.onLongPress(e)
                }

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {

                    return super.onFling(e1, e2, velocityX, velocityY)
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    val sCoord: PointF? =
                        screenShotImageView.viewToSourceCoord(e1?.x ?: 0f, e1?.y ?: 0f)
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }

                override fun onShowPress(e: MotionEvent?) {

                    super.onShowPress(e)
                }
            })

        val screenshotUri = intent.getStringExtra("screenshot")
        if (screenshotUri.isNullOrEmpty()) {

            //Toast.makeText(this, "screenshot is null", Toast.LENGTH_SHORT).show()
            finishAffinity()
        } else {

            lifecycleScope.launchWhenCreated {
                val bitmap: Bitmap = withContext(Dispatchers.IO) {
                    Glide.with(this@MainActivity)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .load(screenshotUri)
                        .submit()
                        .get()
                }

                screenShotImageView.apply {
                    setBackgroundColor(Color.BLACK)
                    setImage(ImageSource.bitmap(bitmap))
                    setDoubleTapZoomStyle(ZOOM_FOCUS_CENTER)
                    setOnTouchListener { _, event ->
                        gestureDetector.onTouchEvent(event)
                    }
                    isPanEnabled = true
                    setPanLimit(PAN_LIMIT_CENTER)
                    maxScale = 100F
                }

                val builder = Palette.Builder(bitmap)

                builder.generate { palette: Palette? ->

                    val colorMap = hashMapOf(
                        0 to (palette?.vibrantSwatch to "Vibrant Swatch"),
                        1 to (palette?.darkVibrantSwatch to "Dark Vibrant Swatch"),
                        2 to (palette?.lightVibrantSwatch to "Light Vibrant Swatch"),
                        3 to (palette?.mutedSwatch to "Muted Swatch"),
                        4 to (palette?.darkMutedSwatch to "Dark Muted Swatch"),
                        5 to (palette?.lightMutedSwatch to "Light Muted Swatch"),
                    )

                    colorContainer.setContent {
                        var selectedColorIndex by remember { mutableStateOf(-1) }

                        Row(
                            Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (i in 0..5) {
                                val color = colorMap[i]?.first?.rgb
                                ColorContainer(
                                    color = ComposeColor(color ?: 0),
                                    isActive = selectedColorIndex == i,
                                    isVisible = color != null,
                                    textOnLongPress = colorMap[i]?.second ?: "",
                                    textColor = colorMap[i]?.first?.bodyTextColor ?: 0
                                ) {
                                    color?.let {
                                        selectedColorIndex = i
                                        copyToClipboard(Integer.toHexString(it))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            finishAffinity()
        return super.onKeyDown(keyCode, event)
    }
}

enum class SizeState {
    Small,
    Large
}

data class ColorContainerState(val sizeState: SizeState, val isActive: Boolean)

@Composable
fun RowScope.ColorContainer(
    color: ComposeColor,
    isActive: Boolean,
    isVisible: Boolean,
    textOnLongPress: String,
    textColor: Int,
    selectColor: () -> Unit
) {

    val context = LocalContext.current
    var heightState by remember { mutableStateOf(SizeState.Small) }

    val transition =
        updateTransition(targetState = ColorContainerState(heightState, isActive), label = "height")
    val currentHeight by transition.animateDp(
        label = "changing height anim",
    ) { state ->
        if (state.isActive) 80.dp else 45.dp
    }

    val roundedShape = RoundedCornerShape(16.dp)

    if (isVisible) {
        Box(
            Modifier
                .weight(1f)
                .height(currentHeight)
                .padding(horizontal = 4.dp)
                .background(color = color, shape = roundedShape)
                .border(width = 0.8.dp, color = ComposeColor.White, shape = roundedShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            Toast
                                .makeText(context, textOnLongPress, Toast.LENGTH_SHORT)
                                .apply {
                                    setGravity(Gravity.TOP, 0, 250)
                                    show()
                                }
                        },
                        onPress = {
                            selectColor()
                            heightState = when (heightState) {
                                SizeState.Small -> SizeState.Large
                                SizeState.Large -> SizeState.Small
                            }
                        }
                    )
                }

        ) {
            if (isActive) {
                Text(
                    modifier = Modifier
                        .rotate(-90F)
                        .align(Alignment.Center),
                    text = "Copied!",
                    fontSize = 12.sp,
                    maxLines = 1,
                    color = ComposeColor(textColor)
                )
            }
        }
    }
}

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    clipboard?.setPrimaryClip(ClipData.newPlainText("", text))
}