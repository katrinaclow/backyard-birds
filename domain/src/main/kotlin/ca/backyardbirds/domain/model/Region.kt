package ca.backyardbirds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Region(
    val code: String,
    val name: String
)

@Serializable
data class RegionInfo(
    val code: String,
    val name: String,
    val bounds: RegionBounds?
)

@Serializable
data class RegionBounds(
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double
)
