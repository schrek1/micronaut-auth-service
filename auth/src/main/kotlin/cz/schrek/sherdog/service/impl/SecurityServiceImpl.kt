package cz.schrek.sherdog.service.impl

import cz.schrek.sherdog.config.SecurityConfiguration
import cz.schrek.sherdog.service.SecurityService
import javax.inject.Singleton

@Singleton
class SecurityServiceImpl(
    private val securityConfiguration: SecurityConfiguration
) : SecurityService {

    override fun isApiKeyAllowed(apiKey: String): Boolean = securityConfiguration.getAllowedApiKeys().contains(apiKey)

}
