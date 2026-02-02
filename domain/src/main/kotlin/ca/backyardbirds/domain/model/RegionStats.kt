package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RegionStats(
    val numChecklists: Int,
    val numContributors: Int,
    val numSpecies: Int
)
