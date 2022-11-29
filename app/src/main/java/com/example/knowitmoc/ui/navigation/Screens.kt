package com.example.knowitmoc.ui.navigation

sealed class Screens(
    val route : String
) {
object Home : Screens(route = "home_screen")
object Details : Screens(route = "details_screen") }