package cz.schrek.sherdog.config

import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaType

@MicronautTest(rebuildContext = true)
internal class SecurityConfigurationTest : TestPropertyProvider {

    @Inject
    lateinit var securityConfiguration: SecurityConfiguration

    @Test
    @Property(name = "app.security.allowedApiKeys", value = "aaa,bbb")
    fun allowedApiKeys_keysSet_shouldReturnThem() {
        assertThat(securityConfiguration.getAllowedApiKeys()).containsExactlyInAnyOrder("aaa", "bbb")
    }

    @Test
    fun allowedApiKeys_keysMissing_shouldReturnThem() {
        assertThat(securityConfiguration.getAllowedApiKeys()).isEmpty()
    }

    override fun getProperties(): MutableMap<String, String> = mutableMapOf()

}
