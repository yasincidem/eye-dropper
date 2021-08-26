package com.yasincidem.eyedropper

import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transition.CrossfadeTransition
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.yasincidem.eyedropper.datasource.InstalledPackagesDataSource
import com.yasincidem.eyedropper.image.AppIconFetcher
import com.yasincidem.eyedropper.ui.feature.appicon.AppIconsCard
import com.yasincidem.eyedropper.ui.feature.appicon.AppIconsViewModel
import com.yasincidem.eyedropper.ui.theme.EyeDropperTheme
import kotlin.math.roundToInt

class LauncherActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val appIconsViewModel = viewModel<AppIconsViewModel>()
            appIconsViewModel.getAppIconUris()

            EyeDropperTheme {
                ProvideWindowInsets {
                    val scrollState = rememberScrollState()

                    val toolbarHeight = 64.dp
                    val toolbarHeightPx =
                        with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
                    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }

                    val nestedScrollConnection = remember {
                        object : NestedScrollConnection {
                            override fun onPreScroll(
                                available: Offset,
                                source: NestedScrollSource
                            ): Offset {

                                val delta = available.y
                                val newOffset = toolbarOffsetHeightPx.value + delta
                                toolbarOffsetHeightPx.value =
                                    newOffset.coerceIn(-toolbarHeightPx, 0f)
                                return Offset.Zero
                            }
                        }
                    }

                    Scaffold(
                        modifier = Modifier.systemBarsPadding(),
                        backgroundColor = MaterialTheme.colors.background,
                        content = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .nestedScroll(nestedScrollConnection)
                            ) {
                                LazyColumn(
                                    contentPadding = PaddingValues(top = toolbarHeight)
                                ) {
                                    item {
                                        AppIconsCard(
                                            Modifier.padding(16.dp),
                                            appIconsViewModel
                                        )
                                    }
                                }

                                TopAppBar(
                                    modifier = Modifier
                                        .height(toolbarHeight)
                                        .offset {
                                            IntOffset(
                                                x = 0,
                                                y = toolbarOffsetHeightPx.value.roundToInt()
                                            )
                                        },
                                    title = {
                                        Column {
                                            Text("toolbar offset is ${toolbarOffsetHeightPx.value}")
                                            Text(
                                                text = "My Color Gallery",
                                                style = TextStyle(fontSize = 32.sp)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}