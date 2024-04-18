package net.kigawa.oyu.scoreboardstore.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.config.ScoreStoreConfig
import java.sql.Connection

@Kunit
class Connections(
  pluginConfig: ScoreStoreConfig,
) {
  val hikari: HikariDataSource

  init {
    val config = HikariConfig()
    config.driverClassName = "com.mysql.jdbc.Driver"
    config.jdbcUrl = "jdbc:mysql://${pluginConfig.db.host}:${pluginConfig.db.port}/${pluginConfig.db.name}"

    config.addDataSourceProperty("user", pluginConfig.db.user)
    config.addDataSourceProperty("password", pluginConfig.db.pass)
    config.addDataSourceProperty("cachePrepStmts", "true")
    config.addDataSourceProperty("prepStmtCacheSize", "250")
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    config.addDataSourceProperty("useServerPrepStmts", "true")
    config.connectionInitSql = "SELECT 1"
    hikari = HikariDataSource(config)
  }
  fun get(): Connection = hikari.connection
}