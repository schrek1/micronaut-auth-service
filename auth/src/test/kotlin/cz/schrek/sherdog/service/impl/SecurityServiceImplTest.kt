package cz.schrek.sherdog.service.impl

import cz.schrek.sherdog.config.SecurityConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SecurityServiceImplTest {

    @Test
    fun isApiKeyAllowed_allowedApiKey_shouldAllowedThem() {
        val API_KEY = "927f5b53ba3d"

        val allowedApiKeys = listOf("78973asd", API_KEY, "45vfd6789")
        val target = SecurityServiceImpl(SecurityConfiguration(allowedApiKeys))

        val result = target.isApiKeyAllowed(API_KEY)

        assertThat(result).isTrue
    }

    @Test
    fun isApiKeyAllowed_notAllowedApiKey_shouldRejectedThem() {
        val API_KEY = "927f5b53ba3d"

        val allowedApiKeys = listOf("78973asd", "asdas8d973", "45vfd6789")
        val target = SecurityServiceImpl(SecurityConfiguration(allowedApiKeys))

        val result = target.isApiKeyAllowed(API_KEY)

        assertThat(result).isFalse
    }


}
