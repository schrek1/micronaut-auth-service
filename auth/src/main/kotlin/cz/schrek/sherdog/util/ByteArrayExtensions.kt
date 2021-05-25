package cz.schrek.sherdog.util

fun ByteArray?.toHexString(numberSeparator: String = ""): String? {
    return this?.asSequence()
        ?.map { String.format("%02x", it) }
        ?.joinToString(separator = numberSeparator) { it }
}


