package ca.backyardbirds.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object TaxonomyTable : Table("taxonomy") {
    val speciesCode = varchar("species_code", 20)
    val commonName = varchar("common_name", 255)
    val scientificName = varchar("scientific_name", 255)
    val category = varchar("category", 50)
    val taxonOrder = double("taxon_order")
    val bandingCodes = text("banding_codes").nullable() // Stored as JSON array string
    val comNameCodes = text("com_name_codes").nullable()
    val sciNameCodes = text("sci_name_codes").nullable()
    val taxonOrderName = varchar("taxon_order_name", 100).nullable()
    val familyCode = varchar("family_code", 20).nullable()
    val familyCommonName = varchar("family_common_name", 255).nullable()
    val familyScientificName = varchar("family_scientific_name", 255).nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    override val primaryKey = PrimaryKey(speciesCode)
}
