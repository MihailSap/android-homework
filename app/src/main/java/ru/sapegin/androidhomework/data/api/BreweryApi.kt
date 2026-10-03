package ru.sapegin.androidhomework.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BreweryApi {

    @GET("breweries")
    suspend fun getBreweries(
        @Query("per_page") perPage: Int = 20
    ): List<BreweryDto>

    @GET("breweries/{id}")
    suspend fun getBreweryById(
        @Path("id") id: String
    ): BreweryDto
}