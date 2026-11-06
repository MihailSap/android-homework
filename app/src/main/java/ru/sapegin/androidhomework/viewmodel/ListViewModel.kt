package ru.sapegin.androidhomework.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sapegin.androidhomework.data.api.RetrofitClient
import ru.sapegin.androidhomework.data.local.BreweryDatabase
import ru.sapegin.androidhomework.data.local.BreweryFilterStorage
import ru.sapegin.androidhomework.data.repository.NetworkBreweryRepository
import ru.sapegin.androidhomework.data.repository.RoomFavoriteBreweryRepository
import ru.sapegin.androidhomework.domain.cache.FiltersBadgeCache
import ru.sapegin.androidhomework.domain.model.ListItem
import ru.sapegin.androidhomework.domain.repository.FavoriteBreweryRepository
import ru.sapegin.androidhomework.domain.usecase.GetBreweriesUseCase

data class BreweryListItemUiModel(
    val id: String,
    val name: String,
    val typeText: String,
    val locationText: String,
    val countryText: String
)

sealed interface ListUiState {

    data object Loading : ListUiState

    data class Success(
        val items: List<BreweryListItemUiModel>
    ) : ListUiState

    data class Error(
        val message: String
    ) : ListUiState
}

class ListViewModel(
    private val getBreweriesUseCase: GetBreweriesUseCase,
    private val filterStorage: BreweryFilterStorage,
    private val favoriteRepository: FavoriteBreweryRepository,
    private val filtersBadgeCache: FiltersBadgeCache
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ListUiState>(ListUiState.Loading)

    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val exceptionHandler = createCoroutineExceptionHandler { throwable ->
        _uiState.value = ListUiState.Error(
            message = throwable.message
                ?: "Не удалось загрузить данные"
        )
    }

    private val _favoriteIds =
        MutableStateFlow<Set<String>>(emptySet())

    val favoriteIds: StateFlow<Set<String>> =
        _favoriteIds.asStateFlow()

    private val _favoriteError =
        MutableStateFlow<String?>(null)

    val favoriteError: StateFlow<String?> =
        _favoriteError.asStateFlow()

    val shouldShowFiltersBadge: StateFlow<Boolean> =
        filtersBadgeCache.shouldShowBadge

    private var itemsById: Map<String, ListItem> = emptyMap()

    init {
        observeFavorites()
        loadBreweries()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteRepository.observeFavoriteIds().collect { ids ->
                _favoriteIds.value = ids
            }
        }
    }

    fun toggleFavorite(itemId: String) {
        val item = itemsById[itemId] ?: return

        viewModelScope.launch {
            try {
                favoriteRepository.toggleFavorite(item)
                _favoriteError.value = null
            } catch (e: Exception) {
                _favoriteError.value =
                    e.message ?: "Не удалось изменить избранное"
            }
        }
    }

    private fun loadBreweries() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.value = ListUiState.Loading
            itemsById = emptyMap()

            val filters = filterStorage.loadFilters()

            filtersBadgeCache.update(filters)

            val items = getBreweriesUseCase(filters)

            itemsById = items.associateBy { it.id }

            val uiModels = items.map { it.toUiModel() }

            _uiState.value = ListUiState.Success(uiModels)
        }
    }

    private fun ListItem.toUiModel() = BreweryListItemUiModel(
        id = id,
        name = name,
        typeText = "Тип: $breweryType",
        locationText = "Расположение: $city, $stateProvince",
        countryText = "Страна: $country"
    )
}

class ListViewModelFactory(
    context: Context,
    private val filtersBadgeCache: FiltersBadgeCache
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            val networkRepository = NetworkBreweryRepository(
                RetrofitClient.api
            )

            val useCase = GetBreweriesUseCase(networkRepository)

            val filterStorage = BreweryFilterStorage(appContext)

            val database = BreweryDatabase.getInstance(appContext)

            val favoriteRepository = RoomFavoriteBreweryRepository(
                database.favoriteBreweryDao()
            )

            @Suppress("UNCHECKED_CAST")
            return ListViewModel(
                getBreweriesUseCase = useCase,
                filterStorage = filterStorage,
                favoriteRepository = favoriteRepository,
                filtersBadgeCache = filtersBadgeCache
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}