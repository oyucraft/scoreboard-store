package io.github.oneservermc.onedatabase.util.config.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ConfigName(val name: String = "", val dir: String = "")
