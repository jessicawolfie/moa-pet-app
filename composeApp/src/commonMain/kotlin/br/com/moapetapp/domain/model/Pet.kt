package br.com.moapetapp.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Duration.Companion.days

/**
 * Representa um pet no domínio da aplicação.
 *
 * @property id Identificador único (UUID)
 * @property name Nome do pet
 * @property species Espécie (DOG, CAT, etc)
 * @property breed Raça específica (ex: "Golden Retriever", "Persa"). Opcional.
 * @property birthDate Data de nascimento. Opcional — nem sempre o tutor sabe.
 * @property weightKg Peso em quilogramas. Opcional.
 * @property photoPath Caminho da foto no armazenamento local. Opcional.
 */

data class Pet (
    val id: String,
    val name: String,
    val species: Species,
    val breed: String?,
    val birthDate: LocalDate?,
    val weightKg: Double?,
    val photoPath: String?
) {
    // Propriedades computadas - calculadas dinamicamente, não armazenada

    // Idade do pet em meses - calculada dinamicamente com base na data atual
    // @return Idade em meses ou null se birthDate não foi informado
    val ageInMonths: Int?
        get() {
            val birth = birthDate ?: return null
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

            // Calcula diferença de meses
            val years = today.year - birth.year
            val months = today.monthNumber - birth.monthNumber

            return (years * 12) + months
        }

    // Idade do pet formatada como texto legível
    // @return Texto formatado ou "idade desconhecida"se não houver birthDate
    val ageFormatted: String
        get() {
            val months = ageInMonths ?: return "Idade desconhecida"

            return when {
                months < 12 -> "$months ${if (months == 1) "mês" else "meses"}"
                months % 12 == 0 -> {
                    val years = months / 12
                    "$years ${if (years == 1) "ano" else "anos"}"
                }

                else -> {
                    val years = months / 12
                    val remainingMonths = months % 12
                    "$years ${if (years == 1) "ano" else "anos"} e $remainingMonths ${if (remainingMonths == 1) "mês" else "meses"}"
                }
            }
        }

    // Verifica se o pet é considerado filhote
    // Critério: menos de 12 meses de idade
    // @return true se filhote, false caso não ou se idade desconhecida
    val isPuppy: Boolean
        get() = (ageInMonths ?: Int.MAX_VALUE) < 12
}
