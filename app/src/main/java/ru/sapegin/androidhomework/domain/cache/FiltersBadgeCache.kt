package ru.sapegin.androidhomework.domain.cache

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sapegin.androidhomework.domain.model.BreweryFilters

class FiltersBadgeCache {

    private val _shouldShowBadge = MutableStateFlow(false)

    val shouldShowBadge: StateFlow<Boolean> =
        _shouldShowBadge.asStateFlow()

    fun update(filters: BreweryFilters) {
        _shouldShowBadge.value =
            filters.name.isNotBlank() ||
                    filters.type.isNotBlank() ||
                    filters.city.isNotBlank()
    }
}