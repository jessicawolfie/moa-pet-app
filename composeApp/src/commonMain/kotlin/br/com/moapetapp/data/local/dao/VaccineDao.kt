package br.com.moapetapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.moapetapp.data.local.entity.VaccineEntity
import br.com.moapetapp.domain.model.Vaccine
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccineDao {
    // Vacinas de um pet - mais recentes primeiro
    @Query("SELECT * FROM vaccines WHERE petId = :petId ORDER BY appliedDate DESC")
    fun getVaccinesForPet(petId: String): Flow<List<VaccineEntity>>

    // Vacina específica por id
    @Query("SELECT * FROM vaccines WHERE id = :id")
    suspend fun getVaccineById(id: String): VaccineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccine(vaccine: VaccineEntity)

    @Update
    suspend fun updateVaccine(vaccine: VaccineEntity)

    @Delete
    suspend fun deleteVaccine(vaccine: VaccineEntity)
}