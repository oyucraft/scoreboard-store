package net.kigawa.oyu.scoreboardstore.util.config

import net.kigawa.oyu.scoreboardstore.util.PluginBase
import net.kigawa.oyu.scoreboardstore.util.caseformat.CaseFormat
import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigName
import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class ConfigUtil(private val plugin: PluginBase) {

  fun type(config: Config): KClass<out Config> {
    return config::class
  }

  fun fileName(config: Config): String {
    val name = type(config).findAnnotation<ConfigName>()?.name
    if (name != null) return name

    return CaseFormat.HIGHER_CAMEL_CASE.caseString(
      type(config).simpleName ?: "anonymous"
    ).toFormatStr(CaseFormat.KEBAB_CASE) + ".yml"
  }

  fun dir(config: Config): File {
    val path = type(config).findAnnotation<ConfigName>()?.dir ?: ""
    if (path != "") return File(path)

    return plugin.dataFolder
  }

  fun file(config: Config): File {
    return File(dir(config), fileName(config))
  }

  fun configFields(config: Config): List<ConfigField> {
    return type(config).memberProperties.mapNotNull {
      val configName = it.javaField?.getAnnotation(ConfigValue::class.java) ?: return@mapNotNull null
      return@mapNotNull ConfigField(config, it, configName)
    }
  }
}