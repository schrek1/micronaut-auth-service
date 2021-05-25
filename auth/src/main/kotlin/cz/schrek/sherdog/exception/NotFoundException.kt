package cz.schrek.sherdog.exception

class NotFoundException(
    message: String = ""
) : RuntimeException(message) {
}
