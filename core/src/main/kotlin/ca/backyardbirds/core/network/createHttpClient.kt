package ca.backyardbirds.core.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

<<<<<<<< HEAD:core/src/main/kotlin/ca/backyardbirds/core/network/HttpClientFactory.kt
class HttpClientFactory {
    fun create(): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
========
fun createHttpClient(): HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
>>>>>>>> 7be9ec698cf2f7d0ddbdb5edc4ab1fc2b73c4622:core/src/main/kotlin/ca/backyardbirds/core/network/createHttpClient.kt
    }
}
