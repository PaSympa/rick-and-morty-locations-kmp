package fr.leandremru.rickandmortylocations.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Builds the application-wide [HttpClient] used by remote services.
 *
 * The HTTP engine itself (Android / Java) is auto-discovered by Ktor from
 * the platform-specific dependencies declared in `composeApp/build.gradle.kts`,
 * so this factory stays in `commonMain` with no `expect` / `actual` indirection.
 */
fun createHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        )
    }
    install(Logging) {
        level = LogLevel.INFO
    }
}
