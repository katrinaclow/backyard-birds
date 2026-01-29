package ca.backyardbirds.domain.model

data class Species(
    val speciesCode: String,
    val commonName: String,
    val scientificName: String,
    val category: String? = null,
    val order: String? = null,
    val family: String? = null
)
