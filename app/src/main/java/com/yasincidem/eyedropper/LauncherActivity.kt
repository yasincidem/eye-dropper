package com.yasincidem.eyedropper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.yasincidem.eyedropper.ui.feature.appicons.ui.AppIconsCard
import com.yasincidem.eyedropper.ui.feature.appicons.ui.AppIconsViewModel
import com.yasincidem.eyedropper.ui.theme.EyeDropperTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val appIconsViewModel = viewModel<AppIconsViewModel>()

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