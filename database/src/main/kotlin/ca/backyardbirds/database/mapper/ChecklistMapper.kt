package ca.backyardbirds.database.mapper

import ca.backyardbirds.database.tables.ChecklistObservationsTable
import ca.backyardbirds.database.tables.ChecklistsTable
import ca.backyardbirds.domain.model.ChecklistObservation
import ca.backyardbirds.domain.model.ChecklistSummary
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime
import java.time.ZoneOffset

fun ResultRow.toChecklistSummary(): ChecklistSummary = ChecklistSummary(
    subId = this[ChecklistsTable.subId],
    locId = this[ChecklistsTable.locId],
    userDisplayName = this[ChecklistsTable.userDisplayName],
    numSpecies = this[ChecklistsTable.numSpecies],
    obsDt = LocalDateTime.ofInstant(this[ChecklistsTable.obsDt], ZoneOffset.UTC)
)

fun ResultRow.toChecklistObservation(): ChecklistObservation = ChecklistObservation(
    speciesCode = this[ChecklistObservationsTable.speciesCode],
    howMany = this[ChecklistObservationsTable.howMany]
)
