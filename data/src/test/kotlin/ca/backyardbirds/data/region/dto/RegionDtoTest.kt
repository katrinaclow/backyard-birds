package ca.backyardbirds.data.region.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RegionDtoTest {

    @Test
    fun regionDto_toDomain_mapsAllFields() {
        val dto = RegionDto(
            code = "US-CA",
            name = "California"
        )
        val region = dto.toDomain()

        assertEquals("US-CA", region.code)
        assertEquals("California", region.name)
    }

    @Test
    fun regionInfoDto_toDomain_mapsAllFields() {
        val dto = RegionInfoDto(
            code = "US",
            name = "United States",
            bounds = RegionBoundsDto(
                minX = -124.733,
                maxX = -66.954,
                minY = 24.544,
                maxY = 49.384
            )
        )
        val regionInfo = dto.toDomain()

        assertEquals("US", regionInfo.code)
        assertEquals("United States", regionInfo.name)
        assertEquals(-124.733, regionInfo.bounds?.minX)
        assertEquals(-66.954, regionInfo.bounds?.maxX)
        assertEquals(24.544, regionInfo.bounds?.minY)
        assertEquals(49.384, regionInfo.bounds?.maxY)
    }

    @Test
    fun regionInfoDto_toDomain_preservesNullBounds() {
        val dto = RegionInfoDto(
            code = "US",
            name = "United States",
            bounds = null
        )
        val regionInfo = dto.toDomain()

        assertNull(regionInfo.bounds)
    }

    @Test
    fun regionBoundsDto_toDomain_mapsAllFields() {
        val dto = RegionBoundsDto(
            minX = -124.733,
            maxX = -66.954,
            minY = 24.544,
            maxY = 49.384
        )
        val bounds = dto.toDomain()

        assertEquals(-124.733, bounds.minX)
        assertEquals(-66.954, bounds.maxX)
        assertEquals(24.544, bounds.minY)
        assertEquals(49.384, bounds.maxY)
    }
}
