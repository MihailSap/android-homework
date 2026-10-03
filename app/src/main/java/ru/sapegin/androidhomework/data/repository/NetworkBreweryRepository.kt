package ru.sapegin.androidhomework.data.repository

import ru.sapegin.androidhomework.data.api.BreweryApi
import ru.sapegin.androidhomework.domain.model.ListItem
import ru.sapegin.androidhomework.domain.repository.BreweryRepository

class NetworkBreweryRepository(
    private val api: BreweryApi
) : BreweryRepository {

    override suspend fun getItems(): List<ListItem> {
        return api.getBreweries().map { it.toModel() }
    }

    override suspend fun getItemById(id: String): ListItem {
        return api.getBreweryById(id).toModel()
    }
}