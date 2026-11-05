package ru.sapegin.androidhomework.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sapegin.androidhomework.data.BreweryRepository
import ru.sapegin.androidhomework.data.MockBreweryRepository
import ru.sapegin.androidhomework.model.ListItem

data class ListUiState(
    val items: List<ListItemUiModel> = emptyList()
)

class ListViewModel(
    private val repository: BreweryRepository = MockBreweryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ListUiState(
            items = repository.getItems().map { it.toUiModel() }
        )
    )

    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()
}

private fun ListItem.toUiModel(): ListItemUiModel {
    return ListItemUiModel(
        id = id,
        name = name,
        typeText = "Тип: $breweryType",
        locationText = "Расположение: $city, ${stateProvince ?: "не указан"}",
        countryText = "Страна: $country"
    )
}