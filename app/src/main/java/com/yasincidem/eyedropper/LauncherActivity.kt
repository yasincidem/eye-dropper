package com.yasincidem.eyedropper

import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.yasincidem.eyedropper.datasource.InstalledPackagesDataSource
import com.yasincidem.eyedropper.image.AppIconFetcher
import com.yasincidem.eyedropper.ui.theme.EyeDropperTheme

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packages = InstalledPackagesDataSource(this).getInstalledPackages()

        setContent {
            EyeDropperTheme {
                ProvideWindowInsets {
                    Scaffold(
                        modifier = Modifier.systemBarsPadding(),
                        backgroundColor = MaterialTheme.colors.background,
                        content = {
                            QuickExpandableCell(
                                Modifier.padding(16.dp),
                                packages = packages
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickExpandableCell(
    modifier: Modifier = Modifier,
    packages: List<PackageInfo>
) {
    Card(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "App icons"
                )
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = packages, itemContent = { pack ->

                    val uri =
                        Uri.parse("${AppIconFetcher.SCHEME_PNAME}:${pack.applicationInfo.packageName}")

                    Image(
                        painter = rememberImagePainter(
                            data = uri
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                })
            }
        }
    }
}