package net.kigawa.oyu.scoreboardstore.status

enum class StatusType(
    val key: Int,
    val defaultValue: Int,
    val label: String,
) {
    HP(1, 20, "oyu_mag.hp"),
    MAX_HP(2, 20, "oyu_mag.hp_max"),

    ;

    companion object {
        fun from(label: String): StatusType {
            return entries.first { it.label == label }
        }
    }
}