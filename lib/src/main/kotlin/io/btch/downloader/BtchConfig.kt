package io.btch.downloader

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Configuration for [BtchDownloader] client.
 *
 * @property baseUrl Base API URL (default: `https://backend1.tioo.eu.org`)
 * @property timeoutMs Request timeout in milliseconds (default: 60000)
 * @property json Custom [Json] instance for deserialization
 * @property logLevel Logging level for Ktor client (default: NONE)
 * @property httpClient Pre-configured [HttpClient]; if null, a default CIO-based client is created
 */
public data class BtchConfig(
    val baseUrl: String = "https://backend1.tioo.eu.org",
    val timeoutMs: Long = 60_000L,
    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    },
    val logLevel: LogLevel = LogLevel.NONE,
    val httpClient: HttpClient? = null,
) {
    internal fun buildClient(): HttpClient {
        return httpClient ?: HttpClient(CIO) {
            install(ContentNegotiation) {
                json(this@BtchConfig.json)
            }
            install(Logging) {
                level = this@BtchConfig.logLevel
            }
            install(HttpTimeout) {
                requestTimeoutMillis = this@BtchConfig.timeoutMs
                connectTimeoutMillis = 10_000L
            }
            defaultRequest {
                url(baseUrl)
            }
        }
    }
}
