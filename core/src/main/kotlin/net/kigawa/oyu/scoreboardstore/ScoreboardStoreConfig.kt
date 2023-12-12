package net.kigawa.oyu.scoreboardstore

import net.kigawa.kutil.unitapi.annotation.Kunit
import net.kigawa.oyu.scoreboardstore.util.config.Config

@Kunit
class ScoreboardStoreConfig : Config() {
  val dbHost = "localhost"
  val dbPort = 3306
  val dbUser = "root"
  val dbPass = "root"
  val dbName = "score_store"
}