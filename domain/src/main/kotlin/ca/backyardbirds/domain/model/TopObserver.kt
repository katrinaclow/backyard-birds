package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TopObserver(
    val userDisplayName: String,
    val numSpecies: Int,
    val numChecklists: Int,
    val rowNum: Int,
    val userId: String
)
