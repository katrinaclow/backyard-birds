package ca.backyardbirds.data.taxonomy.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TaxonomyEntryDtoTest {

    private fun sampleDto(
        speciesCode: String = "hoocro1",
        commonName: String = "Hooded Crow",
        scientificName: String = "Corvus cornix",
        category: String = "species",
        taxonOrder: Double = 24143.0,
        bandingCodes: List<String> = listOf("HOCR"),
        comNameCodes: List<String> = listOf("HOCR"),
        sciNameCodes: List<String> = listOf("COCO"),
        order: String? = "Passeriformes",
        familyCode: String? = "corvid1",
        familyComName: String? = "Crows, Jays, and Magpies",
        familySciName: String? = "Corvidae"
    ) = TaxonomyEntryDto(
        speciesCode = speciesCode,
        commonName = commonName,
        scientificName = scientificName,
        category = category,
        taxonOrder = taxonOrder,
        bandingCodes = bandingCodes,
        comNameCodes = comNameCodes,
        sciNameCodes = sciNameCodes,
        order = order,
        familyCode = familyCode,
        familyComName = familyComName,
        familySciName = familySciName
    )

    @Test
    fun toDomain_mapsAllFields_correctly() {
        val dto = sampleDto()
        val entry = dto.toDomain()

        assertEquals("hoocro1", entry.speciesCode)
        assertEquals("Hooded Crow", entry.commonName)
        assertEquals("Corvus cornix", entry.scientificName)
        assertEquals("species", entry.category)
        assertEquals(24143.0, entry.taxonOrder)
        assertEquals(listOf("HOCR"), entry.bandingCodes)
        assertEquals(listOf("HOCR"), entry.comNameCodes)
        assertEquals(listOf("COCO"), entry.sciNameCodes)
        assertEquals("Passeriformes", entry.order)
        assertEquals("corvid1", entry.familyCode)
        assertEquals("Crows, Jays, and Magpies", entry.familyComName)
        assertEquals("Corvidae", entry.familySciName)
    }

    @Test
    fun toDomain_preservesNullOptionalFields() {
        val dto = sampleDto(
            order = null,
            familyCode = null,
            familyComName = null,
            familySciName = null
        )
        val entry = dto.toDomain()

        assertNull(entry.order)
        assertNull(entry.familyCode)
        assertNull(entry.familyComName)
        assertNull(entry.familySciName)
    }

    @Test
    fun toDomain_handlesEmptyLists() {
        val dto = sampleDto(
            bandingCodes = emptyList(),
            comNameCodes = emptyList(),
            sciNameCodes = emptyList()
        )
        val entry = dto.toDomain()

        assertEquals(emptyList(), entry.bandingCodes)
        assertEquals(emptyList(), entry.comNameCodes)
        assertEquals(emptyList(), entry.sciNameCodes)
    }
}
