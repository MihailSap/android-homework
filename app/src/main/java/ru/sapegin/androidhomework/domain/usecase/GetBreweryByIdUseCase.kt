package ru.sapegin.androidhomework.domain.usecase

import ru.sapegin.androidhomework.domain.repository.BreweryRepository

class GetBreweryByIdUseCase(
    private val repository: BreweryRepository
) {

    suspend operator fun invoke(id: String) = repository.getItemById(id)
}