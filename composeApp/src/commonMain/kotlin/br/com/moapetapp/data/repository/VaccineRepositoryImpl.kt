package br.com.moapetapp.data.repository

import br.com.moapetapp.data.local.dao.VaccineDao
import br.com.moapetapp.data.mapper.toDomain
import br.com.moapetapp.data.mapper.toEntity
import br.com.moapetapp.domain.model.Vaccine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Implementação do repositório de vacinas usando Room
class VaccineRepositoryImpl(
    private val vaccineDao: VaccineDao,
) : VaccineRepository {

    // Flow do DAO (VaccineEntity) mapeado para Flow de Vaccine (domínio)
    override fun getVaccinesForPet(petId: String): Flow<List<Vaccine>> {
        return vaccineDao.getVaccinesForPet(petId)
            .map { entities -> entities.toDomain() }
    }

    override suspend fun getVaccineById(id: String): Result<Vaccine> {
        return try {
            val entity = vaccineDao.getVaccineById(id)
            if (entity != null) {
                Result.success(entity.toDomain())
            } else {
                Result.failure(Exception("Vacina com ID $id não encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addVaccine(vaccine: Vaccine): Result<Unit> {
        return try {
            vaccineDao.insertVaccine(vaccine.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateVaccine(vaccine: Vaccine): Result<Unit> {
        return try {
            vaccineDao.updateVaccine(vaccine.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteVaccine(id: String): Result<Unit> {
        return try {
            val entity = vaccineDao.getVaccineById(id)
            if (entity != null) {
                vaccineDao.deleteVaccine(entity)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Vacina com ID $id não encontrada para deletar"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}