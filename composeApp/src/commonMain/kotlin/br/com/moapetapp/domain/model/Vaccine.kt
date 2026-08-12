package br.com.moapetapp.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn

/**
 * Representa uma vacina aplicada em um pet.
 *
 * @property id Identificador único (UUID)
 * @propertypetId FK do pet ao qual a vacina pertence (relação 1:N)
 * @property name Nome da vacina (ex: V10)
 * @property appliedDate Data em que a vacina foi aplicada
 * @property nextDoseDate Data da próximna dose. Opcional porque nem toda vacina tem reforço
 * @property reminderDaysBefore Antecedência do lembrete da próxima dose, em dias
 * @property veterinarian Nome do veterinário responsável - opcional
 * @property notes Obersevações livres - opcional
 */
data class Vaccine(
    val id: String,
    val petId: String,
    val name: String,
    val appliedDate: LocalDate,
    val nextDoseDate: LocalDate?,
    val reminderDaysBefore: Int,
    val veterinarian: String?,
    val notes: String?
) {
    /**
     * Dias até a próxima dose, contando a partir de hoje.
     * @return número de dias, ou null se não houver próxima dose agendada.
     *      Pode ser negativo se a próxima dose já estiver atrasada.
     */
    val daysUntilNextDose: Int?
        get() {
            val next = nextDoseDate ?: return null
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            return today.daysUntil(next)
        }
}