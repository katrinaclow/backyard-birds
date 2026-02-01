package ca.backyardbirds.data.obs.dto

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ObservationDtoTest {
    // Builder helper — override only the fields you need to vary per test.
    // Use real response values from https://ebird-api-ui.com/ to fill in fixtures.
    private fun sampleDto(
        speciesCode: String = "hoocro1",
        commonName: String = "Hooded Crow",
        scientificName: String = "Corvus cornix",
        locationId: String = "L7884500",
        locationName: String = "Test Location",
        observationDate: String = "2020-01-21 16:35",
        howMany: Int? = 3,
        latitude: Double = 43.530936,
        longitude: Double = 79.455132,
        isValid: Boolean = true,
        isReviewed: Boolean = false,
        isLocationPrivate: Boolean = true,
        submissionId: String = "S63619695"
    ) = ObservationDto(
        speciesCode = speciesCode,
        commonName = commonName,
        scientificName = scientificName,
        locationId = locationId,
        locationName = locationName,
        observationDate = observationDate,
        howMany = howMany,
        latitude = latitude,
        longitude = longitude,
        isValid = isValid,
        isReviewed = isReviewed,
        isLocationPrivate = isLocationPrivate,
        submissionId = submissionId
    )

    @Test
    fun toDomain_mapsAllFields_correctly() {
        val dto = sampleDto()
        val observation = dto.toDomain()

        assertEquals("hoocro1", observation.speciesCode)
        assertEquals("Hooded Crow", observation.commonName)
        assertEquals("Corvus cornix", observation.scientificName)
        assertEquals("L7884500", observation.locationId)
        assertEquals("Test Location", observation.locationName)
        assertEquals(LocalDateTime.of(2020, 1, 21, 16, 35), observation.observationDate)
        assertEquals(3, observation.howMany)
        assertEquals(43.530936, observation.latitude)
        assertEquals(79.455132, observation.longitude)
        assertEquals(true, observation.isValid)
        assertEquals(false, observation.isReviewed)
        assertEquals(true, observation.isLocationPrivate)
        assertEquals("S63619695", observation.submissionId)
    }

    @Test
    fun toDomain_preservesNullHowMany() {
        val dto = sampleDto(howMany = null)
        val observation = dto.toDomain()

        assertNull(observation.howMany)
    }

    @Test
    fun toDomain_parsesObservationDate_withTime() {
        val dto = sampleDto(observationDate = "2021-06-15 09:00")
        val observation = dto.toDomain()

        assertEquals(LocalDateTime.of(2021, 6, 15, 9, 0), observation.observationDate)
    }

    @Test
    fun toDomain_parsesObservationDate_dateOnly() {
        // eBird omits the time component when the observer didn't record one.
        // The mapper falls back to midnight for these entries.
        val dto = sampleDto(observationDate = "2026-01-28")
        val observation = dto.toDomain()

        assertEquals(LocalDateTime.of(2026, 1, 28, 0, 0), observation.observationDate)
    }
}
