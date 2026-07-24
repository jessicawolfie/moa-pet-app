package br.com.moapetapp.domain.usecase.meal

import br.com.moapetapp.domain.model.FoodType
import br.com.moapetapp.domain.model.Meal
import br.com.moapetapp.domain.usecase.vaccine.FakeNotificationScheduler   // reusa o do EP3
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScheduleFoodReminderUseCaseTest {

    // "Hoje" fixo: 1 jan 2026, meio-dia UTC
    private val fixedNow: Instant = Instant.parse("2026-01-01T12:00:00Z")
    private val fixedClock = object : Clock {
        override fun now(): Instant = fixedNow
    }

    private fun meal(
        totalAmount: Int = 15000,
        dailyAmount: Int = 200, // dura 75 dias
        purchaseDate: LocalDate = LocalDate(2026, 1, 1),
        reminderDaysBefore: Int = 5,
    ) = Meal(
        id = "meal-1",
        petId = "pet-1",
        foodName = "Ração Teste",
        foodType = FoodType.DRY,
        totalAmount = totalAmount,
        dailyAmount = dailyAmount,
        purchaseDate = purchaseDate,
        reminderDaysBefore = reminderDaysBefore,
        notes = null,
    )

    @Test
    fun `agenda lembrete conforme reminderDaysBefore quando o fim e futuro`() = runTest {
        val fake = FakeNotificationScheduler()
        val useCase = ScheduleFoodReminderUseCase(fake, fixedClock)

        // Pacote comprado hoje, dura 75 dias -> fim em 17/03/2026
        // reminderDaysBefore = 5 -> lembrete em 12/03/2026
        val result = useCase(meal())

        assertEquals(1, fake.scheduledCall.size)
        assertTrue(result is FoodReminderResult.Scheduled)
    }

    @Test
    fun `respeita reminderDaysBefore customizado`() = runTest {
        val fake = FakeNotificationScheduler()
        val useCase = ScheduleFoodReminderUseCase(fake, fixedClock)

        // com 10 dias de antecedência, o lembrete cai mais cedo - mas ainda no futuro
        val result = useCase(meal(reminderDaysBefore = 10))

        assertEquals(1, fake.scheduledCall.size)
        assertTrue(result is FoodReminderResult.Scheduled)
    }

    @Test
    fun `nao agenda quando o lembrete cairia no passado`() = runTest {
        val fake = FakeNotificationScheduler()
        val useCase = ScheduleFoodReminderUseCase(fake, fixedClock)

        // pacote pequeno: 3 pacotes 1/dia, comprado hoje -> fim em 3 dias (04/01)
        // reminderDaysBefore = 5 -> lembrete em 30/12/2025, que já passou
        val m = meal(totalAmount = 3, dailyAmount = 1, reminderDaysBefore = 5)
        val result = useCase(m)

        assertTrue(fake.scheduledCall.isEmpty())
        assertEquals(FoodReminderResult.DateInPast, result)
    }

    @Test
    fun `ao atualizar cancela o lembrete anterior antes de reagendar`() = runTest {
        val fake = FakeNotificationScheduler()
        val useCase = ScheduleFoodReminderUseCase(fake, fixedClock)

        useCase(meal())
        useCase(meal())

        // cancelNotification chamado nas duas vezes (sempre cancela antes de agendar)
        assertEquals(2, fake.cancelledIds.size)
        assertTrue(fake.cancelledIds.all { it == "meal-1" })
    }
}