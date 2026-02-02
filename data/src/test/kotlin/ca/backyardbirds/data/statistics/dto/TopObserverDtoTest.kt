package ca.backyardbirds.data.statistics.dto

import kotlin.test.Test
import kotlin.test.assertEquals

class TopObserverDtoTest {

    @Test
    fun topObserverDto_toDomain_mapsAllFields() {
        val dto = TopObserverDto(
            userDisplayName = "John Birder",
            numSpecies = 150,
            numChecklists = 45,
            rowNum = 1,
            userId = "USR123"
        )
        val topObserver = dto.toDomain()

        assertEquals("John Birder", topObserver.userDisplayName)
        assertEquals(150, topObserver.numSpecies)
        assertEquals(45, topObserver.numChecklists)
        assertEquals(1, topObserver.rowNum)
        assertEquals("USR123", topObserver.userId)
    }

    @Test
    fun regionStatsDto_toDomain_mapsAllFields() {
        val dto = RegionStatsDto(
            numChecklists = 5000,
            numContributors = 250,
            numSpecies = 400
        )
        val stats = dto.toDomain()

        assertEquals(5000, stats.numChecklists)
        assertEquals(250, stats.numContributors)
        assertEquals(400, stats.numSpecies)
    }
}
