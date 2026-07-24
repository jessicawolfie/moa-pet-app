package br.com.moapetapp.data.repository

import br.com.moapetapp.domain.model.Meal
import kotlinx.coroutines.flow.Flow

// Operações de acesso aos dados de alimentação, sem revelar a fonte
interface MealRepository {

    // Observa o pacote atual do pet (mais recente). Emite null se não houver nenhum.
    fun getCurrentMealForPet(petId: String): Flow<Meal?>

    // Observa histórico completo de pacotes do pet
    fun getAllMealsForPet(petId: String): Flow<List<Meal>>

    // Busca um pacote específico pelo ID
    suspend fun getMealById(id: String): Result<Meal>

    // Insere um novo pacote
    suspend fun addMeal(meal: Meal): Result<Unit>

    // Atualiza um pacote existente
    suspend fun updateMeal(meal: Meal): Result<Unit>

    // Remove um pacote
    suspend fun deleteMeal(id: String): Result<Unit>
}