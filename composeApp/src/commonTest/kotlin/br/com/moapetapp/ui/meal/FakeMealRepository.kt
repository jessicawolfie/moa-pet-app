package br.com.moapetapp.ui.meal

import br.com.moapetapp.data.repository.MealRepository
import br.com.moapetapp.domain.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake do MealRepository para testes de ViewModel/use case.
 * Guarda em memória o que foi inserido/atualizado, e permite forçar falha de
 * escrita para exercitar o caminho de erro de persistência.
 */
class FakeMealRepository : MealRepository {

    val addedMeals = mutableListOf<Meal>()      // o que passou por addMeal
    val updatedMeals = mutableListOf<Meal>()    // o que passou por updateMeal
    private val stored = mutableMapOf<String, Meal>()

    // Quando true, addMeal/updateMeal retornam Result.failure (simula falha do Room)
    var failOnWrite: Boolean = false

    // Pré-carrega um pacote (usado no teste de modo edição)
    fun seed(meal: Meal) {
        stored[meal.id] = meal
    }

    override fun getCurrentMealForPet(petId: String): Flow<Meal?> =
        flowOf(stored.values.firstOrNull { it.petId == petId })

    override fun getAllMealsForPet(petId: String): Flow<List<Meal>> =
        flowOf(stored.values.filter { it.petId == petId })

    override suspend fun getMealById(id: String): Result<Meal> =
        stored[id]?.let { Result.success(it) }
            ?: Result.failure(Exception("Pacote $id não encontrado"))

    override suspend fun addMeal(meal: Meal): Result<Unit> {
        if (failOnWrite) return Result.failure(Exception("Falha ao salvar o pacote"))
        addedMeals.add(meal)
        stored[meal.id] = meal
        return Result.success(Unit)
    }

    override suspend fun updateMeal(meal: Meal): Result<Unit> {
        if (failOnWrite) return Result.failure(Exception("Falha ao atualizar o pacote"))
        updatedMeals.add(meal)
        stored[meal.id] = meal
        return Result.success(Unit)
    }

    override suspend fun deleteMeal(id: String): Result<Unit> {
        stored.remove(id)
        return Result.success(Unit)
    }
}
