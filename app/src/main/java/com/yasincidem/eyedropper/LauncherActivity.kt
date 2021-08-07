package com.yasincidem.eyedropper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yasincidem.eyedropper.ui.theme.EyeDropperTheme

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EyeDropperTheme {
                Scaffold(
                    backgroundColor = MaterialTheme.colors.background,
                    content = {
                        Card(
                            modifier = Modifier
                                .padding(top = 64.dp, start = 16.dp, end = 16.dp)
                                .fillMaxWidth()
                                .height(180.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {

                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EyeDropperTheme {
        Greeting("Android")
    }
}