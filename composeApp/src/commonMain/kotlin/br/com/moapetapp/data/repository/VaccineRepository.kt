package br.com.moapetapp.data.repository

import br.com.moapetapp.domain.model.Vaccine
import kotlinx.coroutines.flow.Flow
import moapetapp.composeapp.generated.resources.Res

// Operações de acesso aos dados de vacina, sem revelar a fonte
interface VaccineRepository {

    // Observa as vacinas de um pet: emite nova lista a cada mudança no banco
    fun getVaccinesForPet(petId: String): Flow<List<Vaccine>>

    // Busca uma vacina específica por ID
    suspend fun getVaccineById(id: String): Result<Vaccine>

    // Adiciona uma nova vacina
    suspend fun addVaccine(vaccine: Vaccine): Result<Unit>

    // Atualiza uma vacina existente
    suspend fun updateVaccine(vaccine: Vaccine): Result<Unit>

    // Deleta uma vacina
    suspend fun deleteVaccine(id: String): Result<Unit>
}