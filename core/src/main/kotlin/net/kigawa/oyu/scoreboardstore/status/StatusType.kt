package net.kigawa.oyu.scoreboardstore.status

enum class StatusType(
  val key: Int,
  val defaultValue: Int,
) {
  HP(1, 20),
  MAX_HP(1, 20),
}