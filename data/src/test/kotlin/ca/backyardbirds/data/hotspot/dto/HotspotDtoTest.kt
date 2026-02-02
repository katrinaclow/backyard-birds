package ca.backyardbirds.data.hotspot.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HotspotDtoTest {

    private fun sampleDto(
        locId: String = "L7884500",
        locName: String = "Central Park",
        countryCode: String = "US",
        subnational1Code: String = "US-NY",
        subnational2Code: String? = "US-NY-061",
        lat: Double = 40.7829,
        lng: Double = -73.9654,
        latestObsDt: String? = "2024-01-15 10:30",
        numSpeciesAllTime: Int? = 275
    ) = HotspotDto(
        locId = locId,
        locName = locName,
        countryCode = countryCode,
        subnational1Code = subnational1Code,
        subnational2Code = subnational2Code,
        lat = lat,
        lng = lng,
        latestObsDt = latestObsDt,
        numSpeciesAllTime = numSpeciesAllTime
    )

    @Test
    fun toDomain_mapsAllFields_correctly() {
        val dto = sampleDto()
        val hotspot = dto.toDomain()

        assertEquals("L7884500", hotspot.locId)
        assertEquals("Central Park", hotspot.locName)
        assertEquals("US", hotspot.countryCode)
        assertEquals("US-NY", hotspot.subnational1Code)
        assertEquals("US-NY-061", hotspot.subnational2Code)
        assertEquals(40.7829, hotspot.lat)
        assertEquals(-73.9654, hotspot.lng)
        assertEquals("2024-01-15 10:30", hotspot.latestObsDt)
        assertEquals(275, hotspot.numSpeciesAllTime)
    }

    @Test
    fun toDomain_preservesNullOptionalFields() {
        val dto = sampleDto(
            subnational2Code = null,
            latestObsDt = null,
            numSpeciesAllTime = null
        )
        val hotspot = dto.toDomain()

        assertNull(hotspot.subnational2Code)
        assertNull(hotspot.latestObsDt)
        assertNull(hotspot.numSpeciesAllTime)
    }
}
