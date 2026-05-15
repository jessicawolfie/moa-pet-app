package br.com.moapetapp.data.repository

import br.com.moapetapp.domain.model.Pet
import kotlinx.coroutines.flow.Flow
import moapetapp.composeapp.generated.resources.Res
import org.koin.core.scope.ScopeID

// Repositório de Pets
// Define operações de acesso aos dados de pets sem revelar a fonte
interface PetRepository {

    // Observa todos os pets cadastrados
    // Flow emite nova lista sempre que há mudança no banco
    fun getAllPets(): Flow<List<Pet>>

    // Busca um pet específico por ID
    suspend fun getPetById(id: String): Result<Pet>

    // Adiciona um novo pet
    suspend fun addPet(pet: Pet): Result<Unit>

    // Atualiza um pet existente
    suspend fun updatePet(pet: Pet): Result<Unit>

    // Deleta um pet
    suspend fun deletePet(petID: String): Result<Unit>

    // Conta quantos pets estão cadastrados
    suspend fun getPetCount(): Int
}