package io.github.oneservermc.onedatabase.util.config

import io.github.oneservermc.onedatabase.util.PluginBase
import io.github.oneservermc.onedatabase.util.caseformat.CaseFormat
import io.github.oneservermc.onedatabase.util.config.annotation.ConfigName
import io.github.oneservermc.onedatabase.util.config.annotation.ConfigValue
import net.kigawa.kutil.kutil.reflection.KutilReflect
import java.io.File

class ConfigUtil(private val plugin: PluginBase) {

  fun type(config: Config): Class<out Config> {
    return config.javaClass
  }

  fun fileName(config: Config): String {
    val name = type(config).getAnnotation(ConfigName::class.java)?.name
    if (name != null) return name

    return CaseFormat.HIGHER_CAMEL_CASE.caseString(type(config).simpleName).toFormatStr(CaseFormat.KEBAB_CASE) + ".yml"
  }

  fun dir(config: Config): File {
    val path = type(config).getAnnotation(ConfigName::class.java)?.dir ?: ""
    if (path != "") return File(path)

    return plugin.dataFolder
  }

  fun file(config: Config): File {
    return File(dir(config), fileName(config))
  }

  fun configFields(config: Config): List<ConfigField> {
    return KutilReflect.getAllExitFields(type(config)).mapNotNull {
      val configName = it.getAnnotation(ConfigValue::class.java) ?: return@mapNotNull null
      return@mapNotNull ConfigField(config, it, configName)
    }
  }
}