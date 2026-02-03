package ca.backyardbirds.database

import org.flywaydb.core.Flyway
import javax.sql.DataSource

object FlywayMigration {
    fun run(dataSource: DataSource) {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
        flyway.migrate()
    }

    fun clean(dataSource: DataSource) {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .cleanDisabled(false)
            .load()
        flyway.clean()
    }
}
