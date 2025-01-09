package net.kigawa.oyucraft.oyubingo.config.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ConfigName(val name: String = "", val dir: String = "")
