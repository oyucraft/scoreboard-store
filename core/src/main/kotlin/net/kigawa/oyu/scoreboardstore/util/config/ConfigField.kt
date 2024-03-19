package net.kigawa.oyu.scoreboardstore.util.config

import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

class ConfigField(
  private val config: ConfigObject,
  private val member: KProperty1<out ConfigObject, *>,
  private val configName: ConfigValue,
) {

  val name: String
    get() {
      return if (configName.name == "") this.member.name else configName.name
    }


  fun get(): Any? {
    member.isAccessible = true
    return member.javaField?.get(config)
  }

  fun set(value: Any?) {
    member.isAccessible = true
    if (value == null) {
      if (!member.returnType.isMarkedNullable) return
    }

    member.javaField?.set(config, value)
  }
}