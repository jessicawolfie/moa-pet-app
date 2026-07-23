package br.com.moapetapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.moapetapp.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    /**
     * Pacote atual do pet: o mais recente por data de compra.
     * Emite null se o pet não tem nenhum pacote registrado.
     */
    @Query("SELECT * FROM meals WHERE petId = :petId ORDER BY purchaseDate DESC LIMIT 1")
    fun getCurrentMealForPet(petId: String): Flow<MealEntity?>

    // Histórico completo do pet, mais recente primeiro
    @Query("SELECT * FROM meals WHERE petId = :petId ORDER BY purchaseDate DESC")
    fun getAllMealsForPet(petId: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: String): MealEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)
}