package net.kigawa.oyu.scoreboardstore.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.ScoreboardStoreConfig
import java.sql.Connection

@Kunit
class Connections(
  pluginConfig: ScoreboardStoreConfig,
) {
  val hikari: HikariDataSource

  init {
    val config = HikariConfig()
    config.driverClassName = "com.mysql.jdbc.Driver"
    config.jdbcUrl = "jdbc:mysql://${pluginConfig.dbHost}:${pluginConfig.dbPort}/${pluginConfig.dbName}"
    config.addDataSourceProperty("user", pluginConfig.dbUser)
    config.addDataSourceProperty("password", pluginConfig.dbPass)
    config.addDataSourceProperty("cachePrepStmts", "true")
    config.addDataSourceProperty("prepStmtCacheSize", "250")
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    config.addDataSourceProperty("useServerPrepStmts", "true")
    config.connectionInitSql = "SELECT 1"
    hikari = HikariDataSource(config)
  }
  fun get(): Connection = hikari.connection
}