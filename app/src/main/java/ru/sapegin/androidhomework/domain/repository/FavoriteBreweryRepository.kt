package ru.sapegin.androidhomework.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.sapegin.androidhomework.domain.model.ListItem

interface FavoriteBreweryRepository {

    fun observeFavorites(): Flow<List<ListItem>>

    fun observeFavoriteIds(): Flow<Set<String>>

    suspend fun getFavoriteById(id: String): ListItem?

    suspend fun toggleFavorite(item: ListItem)

    suspend fun removeFavorite(id: String)
}