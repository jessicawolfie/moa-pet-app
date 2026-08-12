package br.com.moapetapp.domain.usecase.vaccine

import br.com.moapetapp.domain.model.Species
import br.com.moapetapp.domain.model.Vaccine
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScheduleVaccineReminderUseCaseTest {

    // Clock congelado: "hoje"é sempre 1 de janeiro de 2026, meio dia UTC
    private val fixedNow: Instant = Instant.parse("2026-01-01T12:00:00Z")
    private val fixedClock = object : Clock {
        override fun now(): Instant = fixedNow
    }

    // Helper: monta uma vacina de teste, variando só o que importa
    private fun vaccine(
        id: String = "vac-1",
        nextDoseDate: LocalDate? = null,
        reminderDaysBefore: Int = 5,
    ) = Vaccine(
        id = id,
        petId = "pet-1",
        name = "V10",
        appliedDate = LocalDate(2025, 12, 1),
        nextDoseDate = nextDoseDate,
        reminderDaysBefore = reminderDaysBefore,
        veterinarian = null,
        notes = null,
    )

    @Test
    fun `agenda lembrete 5 dias antes quando ha proxima dose futura`() = runTest {
        // Arrange: próxima dose em 2026-01-20
        val fake = FakeNotificationScheduler()
        val useCase = ScheduleVaccineReminderUseCase(
            notificationScheduler = fake,
            clock = fixedClock,
        )
        val nextDose = LocalDate(2026, 1, 20)

        // Act
        val result = useCase(vaccine(nextDoseDate = nextDose))

        // Assert: agendou exatamente 1 notificação
        assertEquals(1, fake.scheduledCall.size)

        // para 5 dias antes da próxima dose
        val expectedInstante = LocalDate(2026, 1, 15)
            .atTime(9, 0)
            .toInstant(TimeZone.currentSystemDefault())
        assertEquals(expectedInstante, fake.scheduledCall.first().triggerAt)

        // e o resultado reflete o agendamento
        assertTrue(result is ReminderResult.Scheduled)
    }

    @Test
    fun `nao agenda lembrete quando nao ha proxima dose`() = runTest {
        // Arrange: nextDoseDate é null
        val fake = FakeNotificationScheduler()
        val useCase = ScheduleVaccineReminderUseCase(
            notificationScheduler = fake,
            clock = fixedClock,
        )
        // Act
        val result = useCase(vaccine(nextDoseDate = null))

        // Assert: nada foi agendado, e o resultado é NoNextDose
        assertTrue(fake.scheduledCall.isEmpty())
        assertEquals(ReminderResult.NoNextDose, result)
    }

    @Test
    fun `ao atualizar vacina cancela o lembrete anterior antes de reagendar`() = runTest {
        // Arrange
        val fake = FakeNotificationScheduler()
        val useCase = ScheduleVaccineReminderUseCase(fake, fixedClock)
        val nextDose = LocalDate(2026, 1, 20)

        // Act: agenda a mesma vacina duas vezes (simula criar e depois atualizar)
        useCase(vaccine(id = "vac-1", nextDoseDate = nextDose))
        useCase(vaccine(id = "vac-1", nextDoseDate = nextDose))

        // Assert: cancelNotification foi chamado nas duas vezes (sempre cancela antes de agendar)
        assertEquals(2, fake.cancelledIds.size)
        assertTrue(fake.cancelledIds.all { it == "vac-1" })

        // e o mesmo id foi usado pra cancelar e agendar (garante que casa)
        assertEquals("vac-1", fake.scheduledCall.last().id)
    }

    @Test
    fun `respeita reminderDaysBefore customizado ao agendar`() = runTest {
        // Arrange: próxima dose em 2026-01-20, mas avisar 10 dias antes
        val fake = FakeNotificationScheduler()
        val useCase = ScheduleVaccineReminderUseCase(
            notificationScheduler = fake,
            clock = fixedClock,
        )
        val nextDose = LocalDate(2026, 1, 20)

        // Act
        val result = useCase(vaccine(nextDoseDate = nextDose, reminderDaysBefore = 10))

        // Assert: agendou 1 notificação, para 10 dias antes (2026-01-10)
        assertEquals(1, fake.scheduledCall.size)

        val expectedInstant = LocalDate(2026, 1, 10)
            .atTime(9, 0)
            .toInstant(TimeZone.currentSystemDefault())
        assertEquals(expectedInstant, fake.scheduledCall.first().triggerAt)

        assertTrue(result is ReminderResult.Scheduled)
    }
}
