package ru.sapegin.androidhomework.domain.usecase

import ru.sapegin.androidhomework.domain.model.BreweryFilters
import ru.sapegin.androidhomework.domain.repository.BreweryRepository

class GetBreweriesUseCase(
    private val repository: BreweryRepository
) {

    suspend operator fun invoke(
        filters: BreweryFilters
    ) = repository.getItems(filters)
}