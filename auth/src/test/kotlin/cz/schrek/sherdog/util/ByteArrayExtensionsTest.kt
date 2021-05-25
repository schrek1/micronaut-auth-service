package cz.schrek.sherdog.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ByteArrayExtensionsTest {

    @Nested
    inner class ToHexString {
        @Test
        fun validArray_shouldReturnHex() {
            val ARRAY = "abc".toByteArray()

            assertThat(ARRAY.toHexString())
                .isNotNull
                .isEqualTo("616263")
        }

        @Test
        fun nullArray_shouldReturnNull() {
            val ARRAY: ByteArray? = null

            assertThat((ARRAY).toHexString()).isNull()
        }
    }
}
