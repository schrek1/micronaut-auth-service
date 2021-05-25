package cz.schrek.sherdog

import io.micronaut.runtime.Micronaut.build
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "sherdog-auth",
        version = "0.0"
    )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        build()
            .args(*args)
            .packages("cz.schrek.sherdog")
            .start()
    }
}

