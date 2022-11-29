package com.example.knowitmoc.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.knowitmoc.ui.screens.DetailsScreen
import com.example.knowitmoc.ui.screens.FlickrViewModel
import com.example.knowitmoc.ui.screens.ListScreen

@Composable
fun NavGraphScreen(
    navHostController: NavHostController,
    viewModel: FlickrViewModel
) {
    NavHost(navController = navHostController, startDestination = "home_screen") {
        composable(Screens.Home.route) {
            ListScreen(
            viewModel = viewModel,
            flickrUiState = viewModel.flickrUiState,
            navController = navHostController
        )
        }
        composable(Screens.Details.route) {
            DetailsScreen(
                viewModel = viewModel,
                detailScreenUiState = viewModel.detailScreenUiState)
        }
    }
}