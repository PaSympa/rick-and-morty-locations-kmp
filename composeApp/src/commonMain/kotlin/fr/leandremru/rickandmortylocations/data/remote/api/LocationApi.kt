package fr.leandremru.rickandmortylocations.data.remote.api

import fr.leandremru.rickandmortylocations.data.remote.dto.LocationDto
import fr.leandremru.rickandmortylocations.data.remote.dto.PaginatedDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/** Remote service for the `/location` endpoint of the Rick and Morty API. */
class LocationApi(private val httpClient: HttpClient) {

    /** Fetches a single page of locations (1-based). */
    suspend fun fetchLocations(page: Int = 1): PaginatedDto<LocationDto> =
        httpClient.get("$BASE_URL/location?page=$page").body()

    /** Fetches the detail of a single location by its identifier. */
    suspend fun fetchLocation(id: Int): LocationDto =
        httpClient.get("$BASE_URL/location/$id").body()

    private companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api"
    }
}
