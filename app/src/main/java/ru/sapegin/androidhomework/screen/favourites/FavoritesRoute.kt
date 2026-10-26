package ru.sapegin.androidhomework.screen.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.sapegin.androidhomework.domain.model.ListItem
import ru.sapegin.androidhomework.viewmodel.FavoritesViewModel
import ru.sapegin.androidhomework.viewmodel.FavoritesViewModelFactory

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun FavoritesRoute(
    onItemClick: (String) -> Unit
) {
    val context = LocalContext.current

    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(context)
    )

    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Избранное")
                }
            )
        }
    ) { innerPadding ->

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Избранное пока пусто",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(
                    items = favorites,
                    key = { it.id }
                ) { item ->
                    FavoriteItemCard(
                        item = item,
                        onClick = {
                            onItemClick(item.id)
                        },
                        onRemoveClick = {
                            viewModel.removeFavorite(item.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteItemCard(
    item: ListItem,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                onClick = onRemoveClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Убрать из избранного",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}