package ru.sapegin.androidhomework.screen.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.sapegin.androidhomework.viewmodel.DetailsUiModel
import ru.sapegin.androidhomework.viewmodel.DetailsViewModel
import ru.sapegin.androidhomework.viewmodel.DetailsViewModelFactory

@Composable
fun DetailsRoute(
    onBackClick: () -> Unit
) {
    val viewModel: DetailsViewModel = viewModel(
        factory = DetailsViewModelFactory()
    )

    val uiState by viewModel.uiState.collectAsState()

    DetailsScreen(
        item = uiState.item,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsScreen(
    item: DetailsUiModel?,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Детали")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (item == null) {
            Text(
                text = "Элемент не найден",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 16.dp,
                        vertical = 20.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = item.nameText)
                Text(text = item.breweryTypeText)
                Text(text = item.address1Text)
                Text(text = item.address2Text)
                Text(text = item.address3Text)
                Text(text = item.cityText)
                Text(text = item.stateProvinceText)
                Text(text = item.postalCodeText)
                Text(text = item.countryText)
                Text(text = item.longitudeText)
                Text(text = item.latitudeText)
                Text(text = item.phoneText)
                Text(text = item.websiteUrlText)
                Text(text = item.stateText)
                Text(text = item.streetText)
            }
        }
    }
}