package cz.schrek.sherdog.service

interface SecurityService {

    fun isApiKeyAllowed(apiKey: String): Boolean

}
