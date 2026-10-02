package ru.sapegin.androidhomework.screen.details

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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.sapegin.androidhomework.model.ListItem
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
    item: ListItem?,
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
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 20.dp
                    )
            ) {
                val (
                    name,
                    breweryType,
                    address1,
                    address2,
                    address3,
                    city,
                    stateProvince,
                    postalCode,
                    country,
                    longitude,
                    latitude,
                    phone,
                    websiteUrl,
                    state,
                    street
                ) = createRefs()

                Text(
                    text = "Название: ${item.name}",
                    modifier = Modifier.constrainAs(name) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Тип пивоварни: ${item.breweryType}",
                    modifier = Modifier.constrainAs(breweryType) {
                        top.linkTo(name.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Адрес 1: ${item.address1 ?: "не указан"}",
                    modifier = Modifier.constrainAs(address1) {
                        top.linkTo(breweryType.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Адрес 2: ${item.address2 ?: "не указан"}",
                    modifier = Modifier.constrainAs(address2) {
                        top.linkTo(address1.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Адрес 3: ${item.address3 ?: "не указан"}",
                    modifier = Modifier.constrainAs(address3) {
                        top.linkTo(address2.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Город: ${item.city}",
                    modifier = Modifier.constrainAs(city) {
                        top.linkTo(address3.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Штат/провинция: ${item.stateProvince}",
                    modifier = Modifier.constrainAs(stateProvince) {
                        top.linkTo(city.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Почтовый индекс: ${item.postalCode}",
                    modifier = Modifier.constrainAs(postalCode) {
                        top.linkTo(stateProvince.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Страна: ${item.country}",
                    modifier = Modifier.constrainAs(country) {
                        top.linkTo(postalCode.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Долгота: ${item.longitude}",
                    modifier = Modifier.constrainAs(longitude) {
                        top.linkTo(country.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Широта: ${item.latitude}",
                    modifier = Modifier.constrainAs(latitude) {
                        top.linkTo(longitude.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Телефон: ${item.phone}",
                    modifier = Modifier.constrainAs(phone) {
                        top.linkTo(latitude.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Сайт: ${item.websiteUrl ?: "не указан"}",
                    modifier = Modifier.constrainAs(websiteUrl) {
                        top.linkTo(phone.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Штат: ${item.state}",
                    modifier = Modifier.constrainAs(state) {
                        top.linkTo(websiteUrl.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                    }
                )
                Text(
                    text = "Улица: ${item.street ?: "не указана"}",
                    modifier = Modifier.constrainAs(street) {
                        top.linkTo(state.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                )
            }
        }
    }
}