package net.kigawa.oyu.scoreboardstore.util.config

import net.kigawa.oyu.scoreboardstore.util.PluginBase
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class ConfigManager(
  private val configUtil: ConfigUtil,
  plugin: PluginBase,
) {

  private val yaml = Yaml(CustomClassLoaderConstructor(plugin.pluginClassLoader, LoaderOptions()))
  fun save(config: ConfigParent) {
    createFile(config)

    val data = createYamlData(config)
    OutputStreamWriter(configUtil.file(config).outputStream()).use {
      yaml.dump(data, it)
    }
  }

  private fun createYamlData(obj: Any?): Any? {
    if (obj is Iterable<*>) return obj.map { createYamlData(it) }
    if (obj !is ConfigObject) return obj
    val data = mutableMapOf<String, Any?>()
    configUtil.configFields(obj).forEach {
      data[it.name] = createYamlData(it.get())
    }
    return data
  }

  fun load(config: ConfigParent) {
    createFile(config)

    val data = InputStreamReader(configUtil.file(config).inputStream()).use {
      yaml.loadAs(it, Map::class.java)
    } ?: return

    configUtil.configFields(config).forEach {
      it.set(data[it.name])
    }
  }

  fun createFile(config: ConfigParent) {
    configUtil.dir(config).mkdirs()
    configUtil.file(config).createNewFile()
  }
}