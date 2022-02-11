package com.yasincidem.eyedropper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.accompanist.insets.ProvideWindowInsets
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.palette.BitmapPalette
import com.yasincidem.eyedropper.ext.copyToClipboard
import com.yasincidem.eyedropper.ui.MainViewModel
import com.yasincidem.eyedropper.ui.theme.JetBrainsMonoFontFamily
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color as ComposeColor

val LocalCopy = staticCompositionLocalOf<(String) -> Unit> { { } }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retrieveAndSetImageUri()

        if (imageUri.isNullOrEmpty()) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
            finishAffinity()
        } else {
            setContent {
                ProvideWindowInsets {
                    CompositionLocalProvider(LocalCopy provides this::copyToClipboard) {
                        App(
                            imageUri = imageUri.toString()
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            finishAffinity()
        return super.onKeyDown(keyCode, event)
    }

    private fun retrieveAndSetImageUri() {
        val screenshotUri = intent.getStringExtra("screenshot")
        imageUri = if (screenshotUri.isNullOrEmpty().not())
            screenshotUri.toString()
        else
            handleSendImage()
    }

    private fun handleSendImage(): String? {
        when (intent.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                        return it.toString()
                    }
                }
            }
        }
        return null
    }
}

data class Swatch(val value: Palette.Swatch?, val description: String)

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun App(
    viewModel: MainViewModel = viewModel(),
    imageUri: String,
) {
    var palette by remember { mutableStateOf<Palette?>(null) }

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenWidthDp

    val colorList by remember {
        derivedStateOf {
            listOf(
                Swatch(palette?.vibrantSwatch, "Vibrant"),
                Swatch(palette?.darkVibrantSwatch, "Dark Vibrant"),
                Swatch(palette?.lightVibrantSwatch, "Light Vibrant"),
                Swatch(palette?.mutedSwatch, "Muted"),
                Swatch(palette?.darkMutedSwatch, "Dark Muted"),
                Swatch(palette?.lightMutedSwatch, "Light Muted")
            ).filter { it.value != null }
        }
    }

    val sizePx = with(LocalDensity.current) { -screenHeight.div(8).dp.toPx() }
    val sizeTopPx = with(LocalDensity.current) { -screenHeight.div(2).dp.toPx() }
    val sizeTopTopPx = with(LocalDensity.current) { -screenHeight.div(1.2).dp.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1, sizeTopPx to 2, sizeTopTopPx to 3)
    val swipeableState = rememberSwipeableState(1)

    val overflowStateDp by remember {
        derivedStateOf {
            swipeableState.overflow.value.coerceIn(-(screenHeight.dp.value * 2)..0f).absoluteValue.div(
                48).dp
        }
    }
    val colorBoxSize = screenWidth.div((colorList.size * 2).coerceAtLeast(1)).dp

    Surface(
        Modifier
            .fillMaxSize()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                orientation = Orientation.Vertical
            ),
        color = ComposeColor.Black
    ) {
        Crossfade(targetState = swipeableState) { state ->
            when (state.currentValue) {
                3 -> {
                    TopComponent(
                        modifier = Modifier,
                        colorList = colorList,
                        screenHeight = screenHeight,
                        colorBoxSize = colorBoxSize,
                        viewModel = viewModel,
                        overflowStateDp = overflowStateDp
                    )
                }
                2 -> {
                    CenterComponent(
                        colorList = colorList,
                        screenHeight = screenHeight,
                        colorBoxSize = colorBoxSize,
                        viewModel = viewModel
                    )
                }
                0, 1 -> {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = screenHeight.div(1.1).dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemsIndexed(colorList,
                            key = { index, item -> "${index}:${item.value?.rgb}" },
                            itemContent = { _, swatch ->
                                val currentItem = swatch.value?.rgb
                                ColorBox(
                                    colorBoxSize,
                                    ComposeColor(currentItem ?: 0),
                                    hexColor = viewModel.getHexColor(currentItem)
                                )
                            })
                    }
                }
            }
        }

        GlideImage(
            imageModel = imageUri,
            bitmapPalette = BitmapPalette(imageModel = imageUri, useCache = false) {
                palette = it
            },
            requestOptions = {
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
            },
            contentScale = ContentScale.FillHeight,
            alignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(0, swipeableState.offset.value.roundToInt())
                }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopComponent(
    modifier: Modifier = Modifier,
    colorList: List<Swatch>,
    screenHeight: Int,
    colorBoxSize: Dp,
    viewModel: MainViewModel,
    overflowStateDp: Dp,
) {
    Column(modifier.then(Modifier
        .fillMaxSize()
        .padding(
            bottom = 16.dp,
            start = 16.dp,
            end = 16.dp,
            top = screenHeight
                .div(6)
                .plus(56).dp
        )
        .offset(y = -overflowStateDp)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        colorList.forEach { swatch ->
            val currentColor = swatch.value?.rgb

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp,
                    Alignment.Start)
            ) {
                Column(
                    Modifier
                        .width(100.dp)
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = viewModel.getColorName(currentColor),
                        color = ComposeColor.White,
                        style = MaterialTheme.typography.subtitle2,
                        textAlign = TextAlign.Center)

                    Text(
                        text = viewModel.getHexColor(currentColor),
                        color = ComposeColor.White,
                        style = MaterialTheme.typography.overline)

                    ColorBox(colorBoxSize,
                        ComposeColor(currentColor ?: 0),
                        modifier = Modifier.padding(top = 8.dp),
                        viewModel.getHexColor(swatch.value?.rgb))
                }

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    with(viewModel) {
                        ResourceCard(getAnnotatedXmlColorText(getXmlColorResource(currentColor)))
                        ResourceCard(getAnnotatedComposeColorText(getComposeColorResource(
                            currentColor)))
                    }
                }

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    with(viewModel) {
                        ResourceCard(getAnnotatedXmlColorText(getXmlMaterialColorResource(
                            currentColor)))
                        ResourceCard(getAnnotatedComposeColorText(
                            getComposeMaterialColorResource(
                                currentColor)))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResourceCard(text: AnnotatedString) {
    val copy = LocalCopy.current

    Card(
        backgroundColor = ComposeColor.Black,
        border = BorderStroke(1.dp, ComposeColor.DarkGray),
        shape = RoundedCornerShape(4.dp),
        indication = rememberRipple(color = ComposeColor.White),
        onClick = {
            copy(text.toString())
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(8.dp),
                color = ComposeColor.White,
                fontFamily = JetBrainsMonoFontFamily
            )
        }
    }
}

@Composable
fun CenterComponent(
    modifier: Modifier = Modifier,
    colorList: List<Swatch>,
    screenHeight: Int,
    colorBoxSize: Dp,
    viewModel: MainViewModel,
) {
    Row(
        modifier.then(Modifier
            .padding(
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp,
                top = screenHeight
                    .div(2)
                    .plus(56).dp
            )
            .fillMaxSize()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        ColorColumn(colorList, viewModel) { _, swatch ->
            SwatchColorBox(swatch, colorBoxSize, viewModel.getHexColor(swatch.value?.rgb))
        }

        ColorColumn(colorList, viewModel) { index, swatch ->
            TextColorBox(
                index,
                "Title Text Color",
                colorBoxSize,
                swatch.value?.titleTextColor ?: 0,
                viewModel.getHexColor(swatch.value?.rgb)
            )
        }

        ColorColumn(colorList, viewModel) { index, swatch ->
            TextColorBox(
                index,
                "Body Text Color",
                colorBoxSize,
                swatch.value?.bodyTextColor ?: 0,
                viewModel.getHexColor(swatch.value?.rgb)
            )
        }
    }
}

@Composable
fun ColorBox(size: Dp, color: ComposeColor, modifier: Modifier = Modifier, hexColor: String) {
    val circleShape = CircleShape
    val copy = LocalCopy.current

    Box(modifier = modifier
        .then(Modifier
            .size(size)
            .border(border = BorderStroke(1.dp, ComposeColor.LightGray),
                shape = circleShape)
            .clip(circleShape)
            .background(color = color)
            .padding(32.dp)
            .clickable {
                copy(hexColor)
            }
        )
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColorColumn(
    colorList: List<Swatch>,
    viewModel: MainViewModel,
    itemContent: @Composable (Int, Swatch) -> Unit,
) {
    val copy = LocalCopy.current

    Column(
        Modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Max),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        colorList.forEachIndexed { index, swatch ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = ComposeColor.Transparent,
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    val hexColor = viewModel.getHexColor(swatch.value?.rgb)
                    copy(hexColor)
                },
                indication = rememberRipple(color = ComposeColor.White)
            ) {
                itemContent(index, swatch)
            }
        }
    }
}

@Composable
fun SwatchColorBox(swatch: Swatch, colorBoxSize: Dp, hexColor: String) {
    Column(
        Modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = swatch.description,
            color = ComposeColor.White,
            style = MaterialTheme.typography.overline)

        ColorBox(colorBoxSize, ComposeColor(swatch.value?.rgb ?: 0), hexColor = hexColor)
    }
}

@Composable
fun TextColorBox(index: Int, text: String, colorBoxSize: Dp, color: Int, hexColor: String) {
    Column(
        Modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (index == 0) {
            Text(
                text = text,
                color = ComposeColor.White,
                style = MaterialTheme.typography.overline)
        }

        ColorBox(colorBoxSize, ComposeColor(color), hexColor = hexColor)
    }
}