package br.com.moapetapp.domain.usecase.pet

import br.com.moapetapp.domain.model.Pet
import br.com.moapetapp.data.repository.PetRepository

// Use Case para atualizar um pet existente
// @property repository Repositório de pets
class UpdatePetUseCase(
    private val repository: PetRepository
) {
    // Atualiza um pet com validações
    //@param pet Pet com dados atualizados (deve ter ID válido)
    //@return Result<Unit> indicando sucesso ou erro
    suspend operator fun invoke(pet: Pet): Result<Unit> {
        // Validações
        // ID é obrigatório para atualização
        if (pet.id.isBlank()) {
            return Result.failure(
                IllegalArgumentException("ID do pet é obrigatório para validação")
            )
        }

        // Nome obrigatório
        if (pet.name.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Nome do pet é obrigatório")
            )
        }

        // Nome mínimo 2 caracteres
        if (pet.name.trim().length < 2) {
            return Result.failure(
                IllegalArgumentException("Nome do pet deve ter pelo menos 2 caracteres")
            )
        }

        // Peso positivo se informado
        pet.weightKg?.let { weight ->
            if (weight <= 0) {
                return Result.failure(
                    IllegalArgumentException("Peso deve ser maior que zero")
                )
            }
        }

        // Atualiza no repositório
        return repository.updatePet(pet)
    }
}