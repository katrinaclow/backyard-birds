package ca.backyardbirds.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import javax.sql.DataSource

object DatabaseFactory {
    private var dataSource: HikariDataSource? = null

    fun create(
        url: String,
        user: String,
        password: String,
        maxPoolSize: Int = 10
    ): Database {
        val config = HikariConfig().apply {
            jdbcUrl = url
            username = user
            this.password = password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        dataSource = HikariDataSource(config)
        return Database.connect(dataSource!!)
    }

    fun getDataSource(): DataSource? = dataSource

    fun close() {
        dataSource?.close()
        dataSource = null
    }
}
