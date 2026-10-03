package ru.sapegin.androidhomework.data.api

import com.google.gson.annotations.SerializedName
import ru.sapegin.androidhomework.domain.model.ListItem

data class BreweryDto(
    val id: String,
    val name: String,
    @SerializedName("brewery_type")
    val breweryType: String,
    @SerializedName("address_1")
    val address1: String?,
    @SerializedName("address_2")
    val address2: String?,
    @SerializedName("address_3")
    val address3: String?,
    val city: String,
    @SerializedName("state_province")
    val stateProvince: String,
    @SerializedName("postal_code")
    val postalCode: String,
    val country: String,
    val longitude: Double?,
    val latitude: Double?,
    val phone: String?,
    @SerializedName("website_url")
    val websiteUrl: String?,
    val state: String?,
    val street: String?
) {
    fun toModel(): ListItem {
        return ListItem(
            id = id,
            name = name,
            breweryType = breweryType,
            address1 = address1,
            address2 = address2,
            address3 = address3,
            city = city,
            stateProvince = stateProvince,
            postalCode = postalCode,
            country = country,
            longitude = longitude ?: 0.0,
            latitude = latitude ?: 0.0,
            phone = phone ?: "не указан",
            websiteUrl = websiteUrl,
            state = state ?: "не указан",
            street = street
        )
    }
}