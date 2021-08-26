package com.yasincidem.eyedropper.ui.feature.appicon

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transition.CrossfadeTransition
import com.yasincidem.eyedropper.R

@ExperimentalMaterialApi
@OptIn(ExperimentalCoilApi::class)
@Composable
fun AppIconsCard(
    modifier: Modifier = Modifier,
    viewModel: AppIconsViewModel
) {

    val appIconUris: List<Uri> by viewModel.appIconUris.collectAsState(listOf())

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
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.app_icons),
                        style = MaterialTheme.typography.body1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                                style = MaterialTheme.typography.subtitle1
                            )

                            Icon(
                                painter = painterResource(id = R.drawable.ic_sort),
                                tint = Color.Gray,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            ) {
                items(items = appIconUris, itemContent = { uri ->

                    Image(
                        painter = rememberImagePainter(
                            data = uri,
                            builder = {
                                transition(CrossfadeTransition())
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                })
            }
        }
    }
}