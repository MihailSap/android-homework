package ru.sapegin.androidhomework.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.sapegin.androidhomework.domain.model.ListItem
import ru.sapegin.androidhomework.viewmodel.ListUiState
import ru.sapegin.androidhomework.viewmodel.ListViewModel
import ru.sapegin.androidhomework.viewmodel.ListViewModelFactory

@Composable
fun ListRoute(
    onItemClick: (String) -> Unit
) {
    val viewModel: ListViewModel = viewModel(
        factory = ListViewModelFactory()
    )

    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        ListUiState.Loading -> {
            LoadingScreen()
        }

        is ListUiState.Success -> {
            ListScreen(
                items = state.items,
                onItemClick = onItemClick
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
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Каталог пивоварен США",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 20.dp
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                ListItemCard(
                    item = item,
                    onClick = {
                        onItemClick(item.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun ListItemCard(
    item: ListItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
    }
}