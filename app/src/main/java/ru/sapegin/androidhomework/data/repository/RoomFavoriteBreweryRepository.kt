package ru.sapegin.androidhomework.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sapegin.androidhomework.data.local.FavoriteBreweryDao
import ru.sapegin.androidhomework.data.local.toFavoriteEntity
import ru.sapegin.androidhomework.data.local.toListItem
import ru.sapegin.androidhomework.domain.model.ListItem
import ru.sapegin.androidhomework.domain.repository.FavoriteBreweryRepository

class RoomFavoriteBreweryRepository(
    private val dao: FavoriteBreweryDao
) : FavoriteBreweryRepository {

    override fun observeFavorites(): Flow<List<ListItem>> {
        return dao.observeAll().map { items ->
            items.map { it.toListItem() }
        }
    }

    override fun observeFavoriteIds(): Flow<Set<String>> {
        return dao.observeIds().map { ids ->
            ids.toSet()
        }
    }

    override suspend fun getFavoriteById(id: String): ListItem? {
        return dao.findById(id)?.toListItem()
    }

    override suspend fun toggleFavorite(item: ListItem) {
        if (dao.isFavorite(item.id)) {
            dao.deleteById(item.id)
        } else {
            dao.insert(item.toFavoriteEntity())
        }
    }

    override suspend fun removeFavorite(id: String) {
        dao.deleteById(id)
    }
}