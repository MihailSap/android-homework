package ru.sapegin.androidhomework.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.sapegin.androidhomework.domain.model.ListItem

@Entity(tableName = "favorite_breweries")
data class FavoriteBreweryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val breweryType: String,
    val address1: String?,
    val address2: String?,
    val address3: String?,
    val city: String,
    val stateProvince: String,
    val postalCode: String,
    val country: String,
    val longitude: Double,
    val latitude: Double,
    val phone: String,
    val websiteUrl: String?,
    val state: String,
    val street: String?
)

fun ListItem.toFavoriteEntity(): FavoriteBreweryEntity {
    return FavoriteBreweryEntity(
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
        longitude = longitude,
        latitude = latitude,
        phone = phone,
        websiteUrl = websiteUrl,
        state = state,
        street = street
    )
}

fun FavoriteBreweryEntity.toListItem(): ListItem {
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
        longitude = longitude,
        latitude = latitude,
        phone = phone,
        websiteUrl = websiteUrl,
        state = state,
        street = street
    )
}