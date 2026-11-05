package ru.sapegin.androidhomework.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sapegin.androidhomework.data.BreweryRepository
import ru.sapegin.androidhomework.data.MockBreweryRepository
import ru.sapegin.androidhomework.model.ListItem

data class DetailsUiState(
    val item: DetailsUiModel? = null
)

class DetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: BreweryRepository = MockBreweryRepository()
) : ViewModel() {

    private val itemId: String =
        checkNotNull(savedStateHandle.get<String>("itemId"))

    private val _uiState = MutableStateFlow(
        DetailsUiState(
            item = repository.getItemById(itemId)?.toDetailsUiModel()
        )
    )

    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()
}

private fun ListItem.toDetailsUiModel(): DetailsUiModel {
    return DetailsUiModel(
        nameText = "Название: $name",
        breweryTypeText = "Тип пивоварни: $breweryType",
        address1Text = "Адрес 1: ${address1 ?: "не указан"}",
        address2Text = "Адрес 2: ${address2 ?: "не указан"}",
        address3Text = "Адрес 3: ${address3 ?: "не указан"}",
        cityText = "Город: $city",
        stateProvinceText = "Штат/провинция: ${stateProvince ?: "не указан"}",
        postalCodeText = "Почтовый индекс: ${postalCode ?: "не указан"}",
        countryText = "Страна: $country",
        longitudeText = "Долгота: ${longitude ?: "не указана"}",
        latitudeText = "Широта: ${latitude ?: "не указана"}",
        phoneText = "Телефон: ${phone ?: "не указан"}",
        websiteUrlText = "Сайт: ${websiteUrl ?: "не указан"}",
        stateText = "Штат: ${state ?: "не указан"}",
        streetText = "Улица: ${street ?: "не указана"}"
    )
}

class DetailsViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()

            @Suppress("UNCHECKED_CAST")
            return DetailsViewModel(
                savedStateHandle = savedStateHandle
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}