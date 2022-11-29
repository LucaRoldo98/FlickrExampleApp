package com.example.knowitmoc.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.knowitmoc.ui.navigation.NavGraphScreen
import com.example.knowitmoc.ui.screens.FlickrViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlickrPhotosApp(flickrViewModel : FlickrViewModel, navController: NavHostController, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraphScreen(navHostController = navController, viewModel = flickrViewModel)
        }
    }
}