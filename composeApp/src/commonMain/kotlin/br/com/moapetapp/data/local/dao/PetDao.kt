package br.com.moapetapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.moapetapp.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pet: PetEntity)

    @Update
    suspend fun update(pet: PetEntity)

    @Delete
    suspend fun delete(pet: PetEntity)

    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getPetById(id: Long): PetEntity?

    @Query("SELECT * FROM pets ORDER BY name ASC")
    fun getAllPets(): Flow<List<PetEntity>>
}
