package ru.sapegin.androidhomework.domain.model

data class ListItem(
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