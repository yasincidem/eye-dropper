package com.yasincidem.eyedropper.ui.feature.appicons.ui

import android.net.Uri
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.yasincidem.eyedropper.R

@ExperimentalMaterialApi
@OptIn(ExperimentalCoilApi::class)
@Composable
fun AppIconsCard(
    modifier: Modifier = Modifier,
    viewModel: AppIconsViewModel
) {

    val appIconUris: List<Uri> by viewModel.appIconUris.collectAsState(listOf())

    val listState = rememberLazyListState()

    Card(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ),
        shape = RoundedCornerShape(16.dp),
        onClick = {

        }
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 2.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.app_icons),
                        style = MaterialTheme.typography.body1
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.app_icons_count,
                                appIconUris.size
                            ),
                            style = MaterialTheme.typography.subtitle1.copy(color = Color.LightGray)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.last_updated),
                                style = MaterialTheme.typography.subtitle2
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.ic_sort),
                                tint = Color.Gray,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            val width = remember { mutableStateOf(0f) }
            val height = remember { mutableStateOf(0f) }

            val density = LocalDensity.current.density

            Box {

                val iconSize = 48.dp

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.onGloballyPositioned {
                        width.value = (it.size.width / density)
                        height.value = (it.size.height / density)
                    },
                    state = listState
                ) {
                    items(items = appIconUris, itemContent = { uri ->
                        Image(
                            painter = rememberImagePainter(
                                data = uri
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(iconSize)
                        )
                    })
                }

                val startShadowTransition =
                    updateTransition(targetState = listState, label = "startShadowTransition")
                val endShadowTransition =
                    updateTransition(targetState = listState, label = "endShadowTransition")

                val startShadowColor: Color by startShadowTransition.animateColor(
                    label = "startShadowColor",
                    transitionSpec = {
                        tween(durationMillis = 300, delayMillis = 30)
                    }
                ) { state ->
                    if (state.firstVisibleItemScrollOffset < iconSize.value / 2 && state.firstVisibleItemIndex < 1) Color.Transparent else MaterialTheme.colors.surface
                }

                val endShadowColor: Color by endShadowTransition.animateColor(
                    label = "endShadowColor",
                    transitionSpec = {
                        tween(durationMillis = 300, delayMillis = 30)
                    }
                ) { state ->
                    if (state.isScrolledToTheEnd() ) Color.Transparent else MaterialTheme.colors.surface
                }

                Box(
                    Modifier
                        .size(width.value.dp, height.value.dp)
                        .padding(top = 8.dp)
                        .background(
                            Brush.horizontalGradient(
                                0.0f to startShadowColor,
                                0.08f to Color.Transparent,
                                0.92f to Color.Transparent,
                                1.0f to endShadowColor
                            )
                        )
                )
            }
        }
    }
}

fun LazyListState.isScrolledToTheEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
