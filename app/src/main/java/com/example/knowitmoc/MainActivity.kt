package com.example.knowitmoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.knowitmoc.ui.FlickrPhotosApp
import com.example.knowitmoc.ui.screens.FlickrViewModel
import com.example.knowitmoc.ui.theme.KnowItMOCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KnowItMOCTheme {
                val viewModel : FlickrViewModel = viewModel(factory = FlickrViewModel.Factory)
                val navController = rememberNavController()
                FlickrPhotosApp(viewModel, navController)
            }
        }
    }
}
