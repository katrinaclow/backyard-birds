package ca.backyardbirds.data.checklist.dto

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ChecklistDtoTest {

    @Test
    fun checklistSummaryDto_toDomain_mapsAllFields() {
        val dto = ChecklistSummaryDto(
            subId = "S123456",
            locId = "L7884500",
            userDisplayName = "John Birder",
            numSpecies = 25,
            obsDt = "2024-01-15 10:30"
        )
        val summary = dto.toDomain()

        assertEquals("S123456", summary.subId)
        assertEquals("L7884500", summary.locId)
        assertEquals("John Birder", summary.userDisplayName)
        assertEquals(25, summary.numSpecies)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), summary.obsDt)
    }

    @Test
    fun checklistSummaryDto_toDomain_parsesDateOnly() {
        val dto = ChecklistSummaryDto(
            subId = "S123456",
            locId = "L7884500",
            userDisplayName = "John Birder",
            numSpecies = 25,
            obsDt = "2024-01-15"
        )
        val summary = dto.toDomain()

        assertEquals(LocalDateTime.of(2024, 1, 15, 0, 0), summary.obsDt)
    }

    @Test
    fun checklistObservationDto_toDomain_mapsAllFields() {
        val dto = ChecklistObservationDto(
            speciesCode = "hoocro1",
            howManyStr = "5"
        )
        val obs = dto.toDomain()

        assertEquals("hoocro1", obs.speciesCode)
        assertEquals(5, obs.howMany)
    }

    @Test
    fun checklistObservationDto_toDomain_preservesNullHowMany() {
        val dto = ChecklistObservationDto(
            speciesCode = "hoocro1",
            howManyStr = null
        )
        val obs = dto.toDomain()

        assertEquals("hoocro1", obs.speciesCode)
        assertNull(obs.howMany)
    }

    @Test
    fun checklistObservationDto_toDomain_handlesXCount() {
        val dto = ChecklistObservationDto(
            speciesCode = "hoocro1",
            howManyStr = "X"
        )
        val obs = dto.toDomain()

        assertEquals("hoocro1", obs.speciesCode)
        assertNull(obs.howMany)
    }

    @Test
    fun checklistDto_toDomain_mapsAllFields() {
        val dto = ChecklistDto(
            subId = "S123456",
            locId = "L7884500",
            userDisplayName = "John Birder",
            numSpecies = 2,
            obsDt = "2024-01-15 10:30",
            obs = listOf(
                ChecklistObservationDto("hoocro1", "5"),
                ChecklistObservationDto("bkcchi", "X")
            )
        )
        val checklist = dto.toDomain()

        assertEquals("S123456", checklist.subId)
        assertEquals("L7884500", checklist.locId)
        assertEquals("John Birder", checklist.userDisplayName)
        assertEquals(2, checklist.numSpecies)
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), checklist.obsDt)
        assertEquals(2, checklist.obs.size)
        assertEquals("hoocro1", checklist.obs[0].speciesCode)
        assertEquals(5, checklist.obs[0].howMany)
        assertEquals("bkcchi", checklist.obs[1].speciesCode)
        assertNull(checklist.obs[1].howMany)
    }
}
