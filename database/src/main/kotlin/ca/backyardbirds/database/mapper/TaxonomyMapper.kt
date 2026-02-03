package ca.backyardbirds.database.mapper

import ca.backyardbirds.database.tables.TaxonomyTable
import ca.backyardbirds.domain.model.TaxonomyEntry
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toTaxonomyEntry(): TaxonomyEntry = TaxonomyEntry(
    speciesCode = this[TaxonomyTable.speciesCode],
    commonName = this[TaxonomyTable.commonName],
    scientificName = this[TaxonomyTable.scientificName],
    category = this[TaxonomyTable.category],
    taxonOrder = this[TaxonomyTable.taxonOrder],
    bandingCodes = this[TaxonomyTable.bandingCodes]?.parseJsonArray() ?: emptyList(),
    comNameCodes = this[TaxonomyTable.comNameCodes]?.parseJsonArray() ?: emptyList(),
    sciNameCodes = this[TaxonomyTable.sciNameCodes]?.parseJsonArray() ?: emptyList(),
    order = this[TaxonomyTable.taxonOrderName],
    familyCode = this[TaxonomyTable.familyCode],
    familyComName = this[TaxonomyTable.familyCommonName],
    familySciName = this[TaxonomyTable.familyScientificName]
)

// Simple JSON array parsing without external dependencies
private fun String.parseJsonArray(): List<String> {
    return this.trim()
        .removePrefix("[")
        .removeSuffix("]")
        .split(",")
        .map { it.trim().removeSurrounding("\"") }
        .filter { it.isNotBlank() }
}

fun List<String>.toJsonArray(): String =
    "[${joinToString(",") { "\"$it\"" }}]"
