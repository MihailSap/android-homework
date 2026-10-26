package ru.sapegin.androidhomework.data.local

import android.content.Context
import ru.sapegin.androidhomework.domain.model.BreweryFilters

class BreweryFilterStorage(context: Context) {

    private val preferences = context.applicationContext
        .getSharedPreferences(
            "brewery_filters",
            Context.MODE_PRIVATE
        )

    fun loadFilters(): BreweryFilters {
        return BreweryFilters(
            name = preferences.getString("name", "").orEmpty(),
            type = preferences.getString("type", "").orEmpty(),
            city = preferences.getString("city", "").orEmpty()
        )
    }

    fun saveFilters(filters: BreweryFilters) {
        preferences.edit()
            .putString("name", filters.name)
            .putString("type", filters.type)
            .putString("city", filters.city)
            .apply()
    }
}