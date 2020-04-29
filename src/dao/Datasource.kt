package my.ktor.dao

import com.typesafe.config.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object Datasource {
    fun build(dbconf: Config): HikariDataSource {
        val config: HikariConfig = HikariConfig()
        config.jdbcUrl = dbconf.getString("db.url")
        config.username = dbconf.getString("db.username")
        config.password = dbconf.getString("db.password")
        config.schema = dbconf.getString("db.schema")
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        return HikariDataSource(config)
    }
}