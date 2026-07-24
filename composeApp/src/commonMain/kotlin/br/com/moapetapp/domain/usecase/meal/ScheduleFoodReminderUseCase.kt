package br.com.moapetapp.domain.usecase.meal

import br.com.moapetapp.core.notification.NotificationScheduler
import br.com.moapetapp.domain.model.Meal
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant

class ScheduleFoodReminderUseCase(
    private val notificationScheduler: NotificationScheduler,
    private val clock: Clock = Clock.System,
) {
    companion object {
        private val REMINDER_TIME = LocalTime(hour = 9, minute = 0, second = 0)
    }

    /**
     * Agenda (ou reagenda) o lembrete de reposição de um pacote de comida.
     * O lembrete cai [meal.reminderDaysBefore] dias antes do fim do estimado pacote.
     *
     * Sempre cancela o lembrete anterior (pelo id meal) antes de reagendar,
     * cobrindo o caso de atualização sem duplicar.
     *
     * @return resultado indicando o que aconteceu.
     */
    suspend operator fun invoke(meal: Meal): FoodReminderResult {
        // 1) cancela lembrete anterior (idempotente)
        notificationScheduler.cancelNotification(meal.id)

        // 2) sem data de fim estimada = não há lembrete
        val endDate = meal.estimatedEndDate ?: return FoodReminderResult.NoEstimate

        // 3) calcula a data do lembrete
        val timeZone = TimeZone.currentSystemDefault()
        val reminderDate = endDate.minus(DatePeriod(days = meal.reminderDaysBefore))
        val reminderInstante = reminderDate.atTime(REMINDER_TIME).toInstant(timeZone)

        // 4) se o instante já passou, não agenda
        if (reminderInstante <= clock.now()) {
            return FoodReminderResult.DateInPast
        }

        // 5) agenda
        notificationScheduler.scheduleNotification(
            id = meal.id,
            title = "Reposição de comida",
            body = "A ${meal.foodName} do seu pet está acabando. Hora de repor!",
            triggerAt = reminderInstante,
        )
        return FoodReminderResult.Scheduled(reminderInstante)
    }
}


// Resultado do agendamento do lembrete de comida.
sealed interface FoodReminderResult {
    data class Scheduled(val triggerAt: Instant) : FoodReminderResult // agendado
    data object NoEstimate : FoodReminderResult // sem data de fim estimada
    data object DateInPast : FoodReminderResult // o lembrete cairia no passado
}