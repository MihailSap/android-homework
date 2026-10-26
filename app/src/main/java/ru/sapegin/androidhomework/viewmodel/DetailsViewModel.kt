package ru.sapegin.androidhomework.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sapegin.androidhomework.data.api.RetrofitClient
import ru.sapegin.androidhomework.data.local.BreweryDatabase
import ru.sapegin.androidhomework.data.repository.NetworkBreweryRepository
import ru.sapegin.androidhomework.data.repository.RoomFavoriteBreweryRepository
import ru.sapegin.androidhomework.domain.model.ListItem
import ru.sapegin.androidhomework.domain.repository.FavoriteBreweryRepository
import ru.sapegin.androidhomework.domain.usecase.GetBreweryByIdUseCase

sealed interface DetailsUiState {

    data object Loading : DetailsUiState

    data class Success(
        val item: ListItem
    ) : DetailsUiState

    data class Error(
        val message: String
    ) : DetailsUiState
}

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getBreweryByIdUseCase: GetBreweryByIdUseCase,
    private val favoriteRepository: FavoriteBreweryRepository
) : ViewModel() {

    private val itemId: String =
        checkNotNull(savedStateHandle.get<String>("itemId"))

    private val _uiState =
        MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)

    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    init {
        loadBrewery()
    }

    private fun loadBrewery() {
        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading

            try {
                val favorite = favoriteRepository.getFavoriteById(itemId)
                val item = favorite ?: getBreweryByIdUseCase(itemId)

                _uiState.value = DetailsUiState.Success(item)
            } catch (e: Exception) {
                _uiState.value = DetailsUiState.Error(
                    message = e.message
                        ?: "Не удалось загрузить данные"
                )
            }
        }
    }
}

class DetailsViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()

            val networkRepository = NetworkBreweryRepository(
                RetrofitClient.api
            )

            val useCase = GetBreweryByIdUseCase(networkRepository)

            val database = BreweryDatabase.getInstance(appContext)

            val favoriteRepository = RoomFavoriteBreweryRepository(
                database.favoriteBreweryDao()
            )

            @Suppress("UNCHECKED_CAST")
            return DetailsViewModel(
                savedStateHandle = savedStateHandle,
                getBreweryByIdUseCase = useCase,
                favoriteRepository = favoriteRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}