package com.example.uas_mobile_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import com.example.uas_mobile_app.ui.theme.EventScreen
import com.example.uas_mobile_app.ui.theme.UASMobileAppTheme
import com.example.uas_mobile_app.viewmodel.EventViewModel
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UASMobileAppTheme {
                Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    EventScreen(viewModel)
                }
            }
        }
    }
}