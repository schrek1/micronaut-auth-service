package cz.schrek.sherdog.config

import io.micronaut.context.annotation.ConfigurationInject
import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.annotation.Nullable

@ConfigurationProperties("app.security")
data class SecurityConfiguration @ConfigurationInject constructor(
    @Nullable private val allowedApiKeys: List<String>?
) {
    fun getAllowedApiKeys() = allowedApiKeys ?: emptyList()
}
