package ru.sapegin.androidhomework.screen.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.sapegin.androidhomework.viewmodel.FiltersViewModel
import ru.sapegin.androidhomework.viewmodel.FiltersViewModelFactory
import ru.sapegin.androidhomework.di.AppContainer

private val breweryTypes = listOf(
    "" to "Любой тип",
    "micro" to "Micro",
    "nano" to "Nano",
    "regional" to "Regional",
    "brewpub" to "Brewpub",
    "large" to "Large",
    "planning" to "Planning",
    "bar" to "Bar",
    "contract" to "Contract",
    "proprietor" to "Proprietor",
    "closed" to "Closed"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersRoute(
    onBackClick: () -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: FiltersViewModel = viewModel(
        factory = FiltersViewModelFactory(
            context = context,
            filtersBadgeCache = AppContainer.filtersBadgeCache
        )
    )

    val filters by viewModel.filters.collectAsState()
    val error by viewModel.error.collectAsState()

    var typeExpanded by remember {
        mutableStateOf(false)
    }

    val selectedType = breweryTypes
        .firstOrNull { it.first == filters.type }
        ?.second ?: "Любой тип"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Фильтры")
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = filters.name,
                onValueChange = viewModel::updateName,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Название пивоварни")
                },
                supportingText = {
                    Text("От 3 символов или оставьте пустым")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = filters.city,
                onValueChange = viewModel::updateCity,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Город")
                },
                supportingText = {
                    Text("От 3 символов или оставьте пустым")
                },
                singleLine = true
            )

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        typeExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Тип пивоварни: $selectedType")
                }

                DropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = {
                        typeExpanded = false
                    }
                ) {
                    breweryTypes.forEach { (type, title) ->
                        DropdownMenuItem(
                            text = {
                                Text(title)
                            },
                            onClick = {
                                viewModel.updateType(type)
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    if (viewModel.saveFilters()) {
                        onDone()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Готово")
            }
        }
    }
}