package ru.sapegin.androidhomework.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BreweryApi {

    @GET("breweries")
    suspend fun getBreweries(
        @Query("per_page") perPage: Int = 20,
        @Query("by_name") byName: String? = null,
        @Query("by_type") byType: String? = null,
        @Query("by_city") byCity: String? = null
    ): List<BreweryDto>

    @GET("breweries/{id}")
    suspend fun getBreweryById(
        @Path("id") id: String
    ): BreweryDto
}