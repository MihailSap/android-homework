package ru.sapegin.androidhomework.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.sapegin.androidhomework.data.local.BreweryDatabase
import ru.sapegin.androidhomework.data.repository.RoomFavoriteBreweryRepository
import ru.sapegin.androidhomework.domain.model.ListItem
import ru.sapegin.androidhomework.domain.repository.FavoriteBreweryRepository

class FavoritesViewModel(
    private val favoriteRepository: FavoriteBreweryRepository
) : ViewModel() {

    val favorites: StateFlow<List<ListItem>> =
        favoriteRepository.observeFavorites().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun removeFavorite(id: String) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(id)
        }
    }
}

class FavoritesViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            val database = BreweryDatabase.getInstance(appContext)

            val repository = RoomFavoriteBreweryRepository(
                database.favoriteBreweryDao()
            )

            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}