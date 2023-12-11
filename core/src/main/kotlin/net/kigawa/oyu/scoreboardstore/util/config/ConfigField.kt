package net.kigawa.oyu.scoreboardstore.util.config

import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue
import java.lang.reflect.Field

class ConfigField(
  private val config: Config,
  private val field: Field,
  private val configName: ConfigValue,
) {

  val name: String
    get() {
      return if (configName.name == "") this.field.name else configName.name
    }
  val type: Class<Any?>
    get() {
      @Suppress("UNCHECKED_CAST")
      return this.field.type as Class<Any?>
    }


  fun get(): Any? {
    field.isAccessible = true
    return field.get(config)
  }

  fun set(value: Any?) {
    field.isAccessible = true
    return field.set(config, value)
  }
}