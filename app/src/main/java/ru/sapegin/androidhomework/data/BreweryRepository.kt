package ru.sapegin.androidhomework.data

import ru.sapegin.androidhomework.model.ListItem

interface BreweryRepository {

    fun getItems(): List<ListItem>

    fun getItemById(id: String): ListItem?
}

class MockBreweryRepository : BreweryRepository {

    private val items = listOf(
        ListItem(
            id = "2907b143-57b4-49ec-aa41-07df64d1e14b",
            name = "Barrel Dog Brewing",
            breweryType = "micro",
            address1 = null,
            address2 = null,
            address3 = null,
            city = "Evergreen",
            stateProvince = "Colorado",
            postalCode = "80439",
            country = "United States",
            longitude = -105.321458,
            latitude = 39.6361637,
            phone = "5599176846",
            websiteUrl = null,
            state = "Colorado",
            street = null
        ),
        ListItem(
            id = "12345678-1234-1234-1234-123456789abc",
            name = "Mountain View Brewery",
            breweryType = "brewpub",
            address1 = "123 Main Street",
            address2 = null,
            address3 = null,
            city = "Denver",
            stateProvince = "Colorado",
            postalCode = "80202",
            country = "United States",
            longitude = -104.9903,
            latitude = 39.7392,
            phone = "5551234567",
            websiteUrl = "https://example.com",
            state = "Colorado",
            street = "Main Street"
        ),
        ListItem(
            id = "87654321-4321-4321-4321-cba987654321",
            name = "Rocky Mountain Brewing",
            breweryType = "micro",
            address1 = "45 Mountain Road",
            address2 = null,
            address3 = null,
            city = "Boulder",
            stateProvince = "Colorado",
            postalCode = "80302",
            country = "United States",
            longitude = -105.2705,
            latitude = 40.015,
            phone = "5559876543",
            websiteUrl = null,
            state = "Colorado",
            street = "Mountain Road"
        ),
        ListItem(
            id = "a1b2c3d4-5678-4abc-9def-123456789012",
            name = "Golden Peak Brewing",
            breweryType = "regional",
            address1 = "789 Mountain Avenue",
            address2 = null,
            address3 = null,
            city = "Golden",
            stateProvince = "Colorado",
            postalCode = "80401",
            country = "United States",
            longitude = -105.2211,
            latitude = 39.7555,
            phone = "5554567890",
            websiteUrl = "https://goldenpeak.example.com",
            state = "Colorado",
            street = "Mountain Avenue"
        ),
        ListItem(
            id = "b2c3d4e5-6789-4abc-8def-234567890123",
            name = "Denver Craft Brewers",
            breweryType = "large",
            address1 = "250 Brewery Street",
            address2 = "Building B",
            address3 = null,
            city = "Denver",
            stateProvince = "Colorado",
            postalCode = "80205",
            country = "United States",
            longitude = -104.9782,
            latitude = 39.7618,
            phone = "5557890123",
            websiteUrl = "https://denvercraft.example.com",
            state = "Colorado",
            street = "Brewery Street"
        )
    )

    override fun getItems(): List<ListItem> = items

    override fun getItemById(id: String): ListItem? {
        return items.firstOrNull { it.id == id }
    }
}