package br.com.moapetapp.domain.usecase.vaccine

import br.com.moapetapp.core.notification.NotificationScheduler
import br.com.moapetapp.domain.model.Vaccine
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant

class ScheduleVaccineReminderUseCase(
    private val notificationScheduler: NotificationScheduler,
    private val clock: Clock = Clock.System,
) {
    companion object {
        private val REMINDER_TIME = LocalTime(hour = 9, minute = 0)
    }

    // Agenda ou reagenda o lembrete da próxima dose de uma vacina
    // @return resultado indicando o que aconteceu
    suspend operator fun invoke(vaccine: Vaccine): ReminderResult {
        // 1) Cancela qualquer lembrete anterior dessa vacina (idempotente: no-op se não existir)
        notificationScheduler.cancelNotification(vaccine.id)
        
        // 2) sem próxima dose -> não agendar lembrete
        val nextDose = vaccine.nextDoseDate ?: return ReminderResult.NoNextDose
        
        // 3) Calcula a data do lembrete
        val timeZone = TimeZone.currentSystemDefault()
        val reminderDate = nextDose.minus(DatePeriod(days = vaccine.reminderDaysBefore))
        val reminderInstant = reminderDate.atTime(REMINDER_TIME).toInstant(timeZone)
        
        // 4) Se o instante já passou, não agendar lembrete
        if (reminderInstant <= clock.now())
            return ReminderResult.DateInPast
        
        // 5) Agenda notificação
        notificationScheduler.scheduleNotification(
            id = vaccine.id,
            title = "Lembrete de vacina",
            body = "A vacina ${vaccine.name} está próxima da próxima dose!",
            triggerAt = reminderInstant,
        )
        
        return ReminderResult.Scheduled(reminderInstant)
    }
}

// Resultado do lembrete
sealed interface ReminderResult {
    data class Scheduled(val triggerAt: Instant) : ReminderResult // agendado com sucesso
    data object NoNextDose : ReminderResult // não tem próxima dose
    data object DateInPast : ReminderResult // já foi notificado
}
