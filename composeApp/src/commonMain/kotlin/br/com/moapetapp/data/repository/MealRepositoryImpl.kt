package br.com.moapetapp.data.repository

import br.com.moapetapp.data.local.dao.MealDao
import br.com.moapetapp.data.mapper.toDomain
import br.com.moapetapp.data.mapper.toEntity
import br.com.moapetapp.domain.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Implementação do repositório de alimentação usando Room.
class MealRepositoryImpl(
    private val mealDao: MealDao
) : MealRepository {

    // Entity? -> Meal? (a ? cuida do caso pet sem pacote)
    override fun getCurrentMealForPet(petId: String): Flow<Meal?> {
        return mealDao.getCurrentMealForPet(petId)
            .map { it?.toDomain() }
    }

    override fun getAllMealsForPet(petId: String): Flow<List<Meal>> {
        return mealDao.getAllMealsForPet(petId)
            .map { it.toDomain() }
    }

    override suspend fun getMealById(id: String): Result<Meal> {
        return try {
            val entity = mealDao.getMealById(id)
            if (entity != null) {
                Result.success(entity.toDomain())
            } else {
                Result.failure(Exception("Pacote de comida com ID $id não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addMeal(meal: Meal): Result<Unit> {
        return try {
            mealDao.insertMeal(meal.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMeal(meal: Meal): Result<Unit> {
        return try {
            mealDao.updateMeal(meal.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMeal(id: String): Result<Unit> {
        return try {
            val entity = mealDao.getMealById(id)
            if (entity != null) {
                mealDao.deleteMeal(entity)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Pacote de comida com ID $id não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}