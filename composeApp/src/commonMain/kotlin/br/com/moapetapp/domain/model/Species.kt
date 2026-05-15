package br.com.moapetapp.domain.model

/**
 * Espécies de pets suportadas pelo app.
 */
enum class Species {
    DOG,
    CAT,
    BIRD,
    OTHER;

    fun displayName(): String = when (this) {
        Species.DOG -> "Cachorro"
        Species.CAT -> "Gato"
        Species.BIRD -> "Pássaro"
        Species.OTHER -> "Outro"
    }

    companion object {
         // @param value String representando a espécie (ex: "dog", "cat")
         // @return Species correspondente ou OTHER se inválido
         fun fromString(value: String): Species {
             return entries.find { it.name == value } ?: Species.OTHER
         }
    }
}