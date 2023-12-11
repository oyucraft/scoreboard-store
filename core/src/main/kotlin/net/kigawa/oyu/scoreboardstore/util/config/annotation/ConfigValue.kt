package net.kigawa.oyu.scoreboardstore.util.config.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ConfigValue(val name: String = "")
