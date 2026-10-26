package ru.sapegin.androidhomework.domain.repository

import ru.sapegin.androidhomework.domain.model.BreweryFilters
import ru.sapegin.androidhomework.domain.model.ListItem

interface BreweryRepository {

    suspend fun getItems(
        filters: BreweryFilters
    ): List<ListItem>

    suspend fun getItemById(id: String): ListItem
}