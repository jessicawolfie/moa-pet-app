package br.com.moapetapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.moapetapp.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

// DAO para acesso à tabela de pets
// Room gera a implementação automaticamente
@Dao
interface PetDao {

    // Retorna todos os pets ordenados por nome
    @Query("SELECT * FROM pets ORDER by name ASC")
    fun getAllPets(): Flow<List<PetEntity>>

    // Retorna um pet específico por ID
    @Query("SELECT * FROM pets WHERE id = :id")
    suspend fun getPetById(id: String): PetEntity?

    // Insere um novo pet no banco
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity)

    // Atualiza um pet existente
    @Update
    suspend fun updatePet(pet: PetEntity)

    // Deleta um pet do banco
    @Delete
    suspend fun deletePet(pet: PetEntity)

    // Conta quantos pets existem no banco
    @Query("SELECT COUNT(*) FROM pets")
    suspend fun getPetCount(): Int
}
