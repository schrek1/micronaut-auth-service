package cz.schrek.sherdog.controller.mapper

import cz.schrek.sherdog.controller.dto.AuthCheckResponse
import cz.schrek.sherdog.controller.dto.AuthResponse
import cz.schrek.sherdog.model.AuthCheckResult
import cz.schrek.sherdog.model.AuthResult
import org.mapstruct.Mapper
import org.mapstruct.NullValueMappingStrategy

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
interface AuthMapper {

    fun mapAuthResponse(authResult: AuthResult): AuthResponse

    fun mapAuthCheckResponse(authCheckResult: AuthCheckResult): AuthCheckResponse

}
