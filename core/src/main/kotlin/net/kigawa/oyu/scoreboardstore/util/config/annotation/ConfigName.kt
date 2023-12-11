package net.kigawa.oyu.scoreboardstore.util.config.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ConfigName(val name: String = "", val dir: String = "")
