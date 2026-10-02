package ru.sapegin.androidhomework.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sapegin.androidhomework.data.BreweryRepository
import ru.sapegin.androidhomework.data.MockBreweryRepository
import ru.sapegin.androidhomework.model.ListItem

data class DetailsUiState(
    val item: ListItem? = null
)

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: BreweryRepository = MockBreweryRepository()
) : ViewModel() {

    private val itemId: String =
        checkNotNull(savedStateHandle.get<String>("itemId"))

    private val _uiState = MutableStateFlow(
        DetailsUiState(
            item = repository.getItemById(itemId)
        )
    )

    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()
}

class DetailsViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()

            @Suppress("UNCHECKED_CAST")
            return DetailsViewModel(
                savedStateHandle = savedStateHandle
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}