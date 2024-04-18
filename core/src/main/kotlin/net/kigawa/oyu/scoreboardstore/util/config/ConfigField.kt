package net.kigawa.oyu.scoreboardstore.util.config

import net.kigawa.oyu.scoreboardstore.util.config.annotation.ConfigValue
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.safeCast

class ConfigField(
    private val config: ConfigObject,
    private val member: KProperty1<out ConfigObject, *>,
    private val configName: ConfigValue,
    private val configUtil: ConfigUtil
) {

    val name: String
        get() {
            return if (configName.name == "") this.member.name else configName.name
        }
    val type
        get() = member.returnType.jvmErasure

    fun get(): Any? {
        member.isAccessible = true
        return member.javaField?.get(config)
    }

    fun set(value: Any?) {
        if (value == null) {
            if (!member.returnType.isMarkedNullable) return
        }

        if (ConfigObject::class.java.isAssignableFrom(type.java)) {
            val obj = ConfigObject::class.safeCast(get())!!
            val data = Map::class.safeCast(value)!!
            configUtil.configFields(obj).forEach {
                it.set(data[it.name])
            }
        } else member.javaField?.apply {
            isAccessible = true
            set(config, value)
        }
    }
}