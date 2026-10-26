package ru.sapegin.androidhomework.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    data object List : AppDestination(
        route = "list",
        title = "Список",
        icon = Icons.Outlined.List
    )

    data object Favorites : AppDestination(
        route = "favorites",
        title = "Избранное",
        icon = Icons.Filled.Star
    )

    data object Details : AppDestination(
        route = "details/{itemId}",
        title = "Детали",
        icon = Icons.Outlined.List
    )

    data object Filters : AppDestination(
        route = "filters",
        title = "Фильтры",
        icon = Icons.Outlined.List
    )
}

val bottomNavigationItems = listOf(
    AppDestination.List,
    AppDestination.Favorites
)