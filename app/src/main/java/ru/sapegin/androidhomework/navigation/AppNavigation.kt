package ru.sapegin.androidhomework.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.sapegin.androidhomework.screen.details.DetailsRoute
import ru.sapegin.androidhomework.screen.favorites.FavoritesRoute
import ru.sapegin.androidhomework.screen.filters.FiltersRoute
import ru.sapegin.androidhomework.screen.list.ListRoute

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val backStackEntry = navController
        .currentBackStackEntryAsState().value

    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != AppDestination.Filters.route) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemSelected = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(
                                navController.graph.startDestinationId
                            ) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = AppDestination.List.route,
            modifier = androidx.compose.ui.Modifier.padding(
                innerPadding
            )
        ) {
            composable(AppDestination.List.route) {
                ListRoute(
                    onItemClick = { itemId ->
                        navController.navigate("details/$itemId")
                    },
                    onFiltersClick = {
                        navController.navigate(
                            AppDestination.Filters.route
                        )
                    }
                )
            }

            composable(AppDestination.Favorites.route) {
                FavoritesRoute(
                    onItemClick = { itemId ->
                        navController.navigate("details/$itemId")
                    }
                )
            }

            composable(AppDestination.Filters.route) {
                FiltersRoute(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onDone = {
                        navController.navigate(
                            AppDestination.List.route
                        ) {
                            popUpTo(AppDestination.List.route) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route = AppDestination.Details.route,
                arguments = listOf(
                    navArgument("itemId") {
                        type = NavType.StringType
                    }
                )
            ) {
                DetailsRoute(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentRoute: String?,
    onItemSelected: (AppDestination) -> Unit
) {
    NavigationBar {
        bottomNavigationItems.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    onItemSelected(destination)
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title
                    )
                },
                label = {
                    Text(destination.title)
                }
            )
        }
    }
}