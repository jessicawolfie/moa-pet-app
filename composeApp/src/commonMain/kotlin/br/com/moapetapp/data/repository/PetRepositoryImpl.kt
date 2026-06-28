package br.com.moapetapp.data.repository

import br.com.moapetapp.data.local.dao.PetDao
import br.com.moapetapp.data.mapper.toDomain
import br.com.moapetapp.data.mapper.toEntity
import br.com.moapetapp.domain.model.Pet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Implementação do repositório de pets usando Room Database
class PetRepositoryImpl (
    private val petDao: PetDao
) : PetRepository {

    //Retorna flow observável de todos os pets
    // Flow do DAO(PetEntity) é mapeado para Flow de pet(domínio)
    override fun getAllPets(): Flow<List<Pet>> {
        return petDao.getAllPets()
            .map { entities -> entities.toDomain()} // converte cada PetEntity para pet
    }

    // Busca um pet por ID
    override suspend fun getPetById(id: String): Result<Pet> {
        return try {
            val entity = petDao.getPetById(id)
            if (entity!= null) {
                Result.success(entity.toDomain())
            } else {
                Result.failure(Exception("Pet com ID $id não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Adicona novo pet
    override suspend fun addPet(pet: Pet): Result<Unit> {
        return try {
            petDao.insertPet(pet.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Atualiza pet existente
    override suspend fun updatePet(pet: Pet): Result<Unit> {
        return try {
            petDao.updatePet(pet.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Deleta pet por ID
    override suspend fun deletePet(petID: String): Result<Unit> {
        return try {
            val entity =  petDao.getPetById(petID)
            if (entity != null) {
                petDao.deletePet(entity)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Pet com ID $petID não encontrado para deletar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Retorna quantidade de pets cadastrados
    override suspend fun getPetCount(): Int {
        return try {
            petDao.getPetCount()
        }catch (e: Exception) {
            0 // se der erro, assume lista vazia
        }
    }
}