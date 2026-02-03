package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val error: String,
    val code: String? = null
)
