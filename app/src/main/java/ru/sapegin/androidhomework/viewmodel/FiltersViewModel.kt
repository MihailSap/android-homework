package ru.sapegin.androidhomework.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sapegin.androidhomework.data.local.BreweryFilterStorage
import ru.sapegin.androidhomework.domain.cache.FiltersBadgeCache

class FiltersViewModel(
    private val storage: BreweryFilterStorage,
    private val filtersBadgeCache: FiltersBadgeCache
) : ViewModel() {

    private val _filters = MutableStateFlow(
        storage.loadFilters()
    )

    val filters = _filters.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)

    val error = _error.asStateFlow()

    init {
        filtersBadgeCache.update(_filters.value)
    }

    fun updateName(value: String) {
        _filters.value = _filters.value.copy(name = value)
        _error.value = null
    }

    fun updateType(value: String) {
        _filters.value = _filters.value.copy(type = value)
        _error.value = null
    }

    fun updateCity(value: String) {
        _filters.value = _filters.value.copy(city = value)
        _error.value = null
    }

    fun saveFilters(): Boolean {
        val filters = _filters.value.copy(
            name = _filters.value.name.trim(),
            city = _filters.value.city.trim()
        )

        val invalidName =
            filters.name.isNotEmpty() &&
                    filters.name.length !in 3..255

        val invalidCity =
            filters.city.isNotEmpty() &&
                    filters.city.length !in 3..255

        if (invalidName || invalidCity) {
            _error.value =
                "Название и город должны содержать от 3 до 255 символов или быть пустыми."
            return false
        }

        storage.saveFilters(filters)
        _filters.value = filters
        filtersBadgeCache.update(filters)
        _error.value = null

        return true
    }
}

class FiltersViewModelFactory(
    context: Context,
    private val filtersBadgeCache: FiltersBadgeCache
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(FiltersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FiltersViewModel(
                storage = BreweryFilterStorage(appContext),
                filtersBadgeCache = filtersBadgeCache
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}