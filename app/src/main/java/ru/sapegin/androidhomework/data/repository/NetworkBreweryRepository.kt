package ru.sapegin.androidhomework.data.repository

import kotlinx.coroutines.delay
import ru.sapegin.androidhomework.data.api.BreweryApi
import ru.sapegin.androidhomework.domain.model.BreweryFilters
import ru.sapegin.androidhomework.domain.model.ListItem
import ru.sapegin.androidhomework.domain.repository.BreweryRepository
import java.io.IOException

class NetworkBreweryRepository(
    private val api: BreweryApi
) : BreweryRepository {

    override suspend fun getItems(
        filters: BreweryFilters
    ): List<ListItem> {
        val name = filters.name.trim()
            .takeIf { it.length >= 3 }

        val type = filters.type
            .takeIf { it.isNotBlank() }

        val city = filters.city.trim()
            .takeIf { it.length >= 3 }

        return retryOnNetworkError {
            api.getBreweries(
                byName = name,
                byType = type,
                byCity = city
            ).map { it.toModel() }
        }
    }

    override suspend fun getItemById(id: String): ListItem {
        return retryOnNetworkError {
            api.getBreweryById(id).toModel()
        }
    }

    private suspend fun <T> retryOnNetworkError(
        attempts: Int = 3,
        request: suspend () -> T
    ): T {
        var lastException: IOException? = null

        for (attempt in 1..attempts) {
            try {
                return request()
            } catch (e: IOException) {
                lastException = e

                if (attempt < attempts) {
                    delay(500L * attempt)
                }
            }
        }

        throw lastException ?: IOException(
            "Не удалось выполнить сетевой запрос"
        )
    }
}