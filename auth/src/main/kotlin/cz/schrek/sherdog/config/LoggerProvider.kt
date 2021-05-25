package cz.schrek.sherdog.config

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


fun logster(): ReadOnlyProperty<Any?, Logger> = object : ReadOnlyProperty<Any?, Logger> {
    lateinit var logger: Logger
    override fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
        if (!::logger.isInitialized) {
            logger = thisRef?.run { LogManager.getLogger(javaClass) } ?: LogManager.getLogger()
        }
        return logger
    }
}
