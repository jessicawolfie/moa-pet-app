package br.com.moapetapp.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MealTest {

    private fun meal(
        totalAmount: Int,
        dailyAmount: Int,
        purchaseDate: LocalDate = LocalDate(2026, 1, 1),
        foodType: FoodType = FoodType.DRY,
    ) = Meal(
        id = "meal-1",
        petId = "pet-1",
        foodName = "Ração Teste",
        foodType = foodType,
        totalAmount = totalAmount,
        dailyAmount = dailyAmount,
        purchaseDate = purchaseDate,
        reminderDaysBefore = 5,
        notes = null,
    )

    @Test
    fun `duracao de pacote de 15kg com 200g por dia e 75 dias`() {
        // 15000 / 200g = 75 dias
        val m = meal(totalAmount = 15000, dailyAmount = 200)
        assertEquals(75, m.estimateDurationDays)
    }

    @Test
    fun `data de fim e a compra mais a duracao`() {
        val m = meal(
            totalAmount = 15000,
            dailyAmount = 200,
            purchaseDate = LocalDate(2026, 1, 1)
        )
        // 1 jan + 75 dias = 17 mar 2026
        assertEquals(LocalDate(2026, 3, 17), m.estimatedEndDate)
    }

    @Test
    fun `comida natural conta em pacotes`(){
        // 15 pacotes / 1 pacote por dia = 15 dias
        val m = meal(totalAmount = 15, dailyAmount = 1, foodType = FoodType.RAW)
        assertEquals(15, m.estimateDurationDays)
    }

    @Test
    fun `consumo diario zero resulta em duracao nula`() {
        // guarda contra divisão por zero
        val m = meal(totalAmount = 15000, dailyAmount = 0)
        assertNull(m.estimateDurationDays)
        assertNull(m.estimatedEndDate)
    }
}