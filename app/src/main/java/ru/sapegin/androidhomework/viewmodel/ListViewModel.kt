package ru.sapegin.androidhomework.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sapegin.androidhomework.data.BreweryRepository
import ru.sapegin.androidhomework.data.MockBreweryRepository
import ru.sapegin.androidhomework.model.ListItem

data class ListUiState(
    val items: List<ListItem> = emptyList()
)

class ListViewModel(
    private val repository: BreweryRepository = MockBreweryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ListUiState(
            items = repository.getItems()
        )
    )

    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()
}