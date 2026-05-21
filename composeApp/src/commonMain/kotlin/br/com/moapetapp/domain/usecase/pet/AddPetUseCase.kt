package br.com.moapetapp.domain.usecase.pet

import br.com.moapetapp.domain.model.Pet
import br.com.moapetapp.data.repository.PetRepository
import com.benasher44.uuid.uuid4

class AddPetUseCase(
    private val repository: PetRepository
) {
    // Adicona um pet com validações
    // Gera UUID automaticamente se não fornecido

    // @param pet Pet a adicionar (id pode estar vazio)
    // @return Result<Pet> com o pet salvo (incluindo ID gerado) ou erro de validação
    suspend operator fun invoke(pet: Pet): Result<Pet> {
        // Validação de regras de negócio

        // Nome é obrigatório e não pode ser vazio
        if (pet.name.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Nome do pet é obrigatório.")
            )
        }

        // Nome deve ter no mínimo 2 caracteres
        if (pet.name.trim().length < 2) {
            return Result.failure(
                IllegalArgumentException("Nome do pet deve ter pelo menos 2 caracteres.")
            )
        }

        // Se peso foi informado, deve ser positivo
        pet.weightKg?.let { weight ->
            if (weight <= 0) {
                return Result.failure(
                    IllegalArgumentException("Peso deve ser maior que zero.")
                )
            }
        }

        // Garante que o pet tem um ID (gera UUID se necessário)
        val petWithId = if (pet.id.isBlank()) {
            // Gera UUID v4 automaticamente
            pet.copy(id = uuid4().toString())
        } else {
            pet
        }

        // Salva no repositório
        return repository.addPet(petWithId)
            .map { petWithId } // Se sucesso, retorna o pet com ID
    }
}