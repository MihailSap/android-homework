package ru.sapegin.androidhomework.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.sapegin.androidhomework.di.AppContainer
import ru.sapegin.androidhomework.domain.model.ListItem
import ru.sapegin.androidhomework.viewmodel.ListUiState
import ru.sapegin.androidhomework.viewmodel.ListViewModel
import ru.sapegin.androidhomework.viewmodel.ListViewModelFactory

@Composable
fun ListRoute(
    onItemClick: (String) -> Unit,
    onFiltersClick: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: ListViewModel = viewModel(
        factory = ListViewModelFactory(
            context = context,
            filtersBadgeCache = AppContainer.filtersBadgeCache
        )
    )

    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val favoriteError by viewModel.favoriteError.collectAsState()
    val shouldShowFiltersBadge by
    viewModel.shouldShowFiltersBadge.collectAsState()

    when (val state = uiState) {
        ListUiState.Loading -> {
            LoadingScreen()
        }

        is ListUiState.Success -> {
            ListScreen(
                items = state.items,
                favoriteIds = favoriteIds,
                favoriteError = favoriteError,
                shouldShowFiltersBadge = shouldShowFiltersBadge,
                onItemClick = onItemClick,
                onFiltersClick = onFiltersClick,
                onFavoriteClick = viewModel::toggleFavorite
            )
        }

        is ListUiState.Error -> {
            ErrorScreen(
                message = state.message
            )
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ошибка загрузки:\n$message",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ListScreen(
    items: List<ListItem>,
    favoriteIds: Set<String>,
    favoriteError: String?,
    shouldShowFiltersBadge: Boolean,
    onItemClick: (String) -> Unit,
    onFiltersClick: () -> Unit,
    onFavoriteClick: (ListItem) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 20.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Каталог пивоварен США",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = onFiltersClick
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = "Фильтры"
                    )

                    if (shouldShowFiltersBadge) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(
                                    x = 3.dp,
                                    y = (-3).dp
                                )
                                .size(9.dp)
                                .background(
                                    color = Color(0xFFF79009),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Text("Фильтры")
            }
        }

        favoriteError?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("По заданным фильтрам ничего не найдено")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(
                    items = items,
                    key = { it.id }
                ) { item ->
                    ListItemCard(
                        item = item,
                        isFavorite = item.id in favoriteIds,
                        onClick = {
                            onItemClick(item.id)
                        },
                        onFavoriteClick = {
                            onFavoriteClick(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListItemCard(
    item: ListItem,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Тип: ${item.breweryType}",
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Расположение: ${item.city}, ${item.stateProvince}",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Страна: ${item.country}",
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(
                onClick = onFavoriteClick
            ) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Star
                    } else {
                        Icons.Outlined.StarBorder
                    },
                    contentDescription = if (isFavorite) {
                        "Убрать из избранного"
                    } else {
                        "Добавить в избранное"
                    },
                    tint = if (isFavorite) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}