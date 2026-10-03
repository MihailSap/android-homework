package ru.sapegin.androidhomework.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sapegin.androidhomework.data.repository.NetworkBreweryRepository
import ru.sapegin.androidhomework.data.api.RetrofitClient
import ru.sapegin.androidhomework.domain.usecase.GetBreweriesUseCase
import ru.sapegin.androidhomework.domain.model.ListItem

sealed interface ListUiState {

    data object Loading : ListUiState

    data class Success(
        val items: List<ListItem>
    ) : ListUiState

    data class Error(
        val message: String
    ) : ListUiState
}

class ListViewModel(
    private val getBreweriesUseCase: GetBreweriesUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ListUiState>(ListUiState.Loading)

    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        loadBreweries()
    }

    private fun loadBreweries() {
        viewModelScope.launch {
            _uiState.value = ListUiState.Loading

            try {
                val items = getBreweriesUseCase()
                _uiState.value = ListUiState.Success(items)
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(
                    message = e.message ?: "Не удалось загрузить данные"
                )
            }
        }
    }
}

class ListViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            val repository = NetworkBreweryRepository(
                RetrofitClient.api
            )

            val useCase = GetBreweriesUseCase(repository)

            @Suppress("UNCHECKED_CAST")
            return ListViewModel(useCase) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}