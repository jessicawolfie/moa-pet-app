package br.com.moapetapp.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/**
 * Tipo de alimentação do pet
 * A unidade de medida muda conforme o tipo, mas a lógica de estoque é a mesma
 *
 * @property displayName rótulo exibido na UI
 * @property unitLabel unidade em que o estoque é medido
 */
enum class FoodType(val displayName: String, val unitLabel: String) {
    DRY("Ração", "g"), // comprada em sacos, medidas em gramas
    RAW("Natural", "pacotes"), // comprada em porções, medidas em pacotes
}

/**
 * Um pacote/estoque de comida de um pet
 *
 *  @property id Identificador único (UUID)
 *  @property petId FK do pet (relação 1:N)
 *  @property foodName Nome do produto (ex: "Premium Plus", "Frango e legumes")
 *  @property foodType Ração ou Natural — define a unidade de medida
 *  @property totalAmount Quantidade total comprada (gramas se DRY, pacotes se RAW)
 *  @property dailyAmount Consumo por dia, na mesma unidade de [totalAmount]
 *  @property purchaseDate Data da compra — base do cálculo de duração
 *  @property reminderDaysBefore Antecedência do lembrete de reposição, em dias
 *  @property notes Observações livres. Opcional
 */
data class Meal(
    val id: String,
    val petId: String,
    val foodName: String,
    val foodType: FoodType,
    val totalAmount: Int,
    val dailyAmount: Int,
    val purchaseDate: LocalDate,
    val reminderDaysBefore: Int,
    val notes: String?,
) {
    /**
     * Quantos dias o pacote dura, dado o consumo diário
     * @return dias de duração, ou null se o consumo diário for inválido (zero/negativo)
     */
    val estimateDurationDays: Int?
        get() {
            if (dailyAmount <= 0) return null
            return (totalAmount / dailyAmount).toInt()
        }

    /**
     * Data estimada em que o pacote acaba (compra + duração)
     * @return a data, ou null se a duração não puder ser calculada
     */
    val estimatedEndDate: LocalDate?
        get() {
            val duration = estimateDurationDays ?: return null
            return purchaseDate.plus(DatePeriod(days = duration))
        }

    /**
     * Dias restantes a partir de hoje até o fim estimado.
     * Pode ser negativo se o pacote já deveria ter acabado.
     * @return dias restantes, ou null se não houver estimativa
     */
    val daysRemaining: Int?
        get() {
            val endDate = estimatedEndDate ?: return null
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            return today.daysUntil(endDate)
        }

    /**
     * Percentual do pacote já consumido (0.0 a 1.0), para a barra de progresso da UI.
     * @return fração consumida, ou null se não houver estimativa.
     */
    val consumedFraction: Float?
        get() {
            val duration = estimateDurationDays ?: return null
            if (duration <= 0) return null
            val remaining = daysRemaining ?: return null
            val consumed = (duration - remaining).toFloat() / duration
            return consumed.coerceIn(0f, 1f)
        }
}
