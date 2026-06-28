package br.com.moapetapp.domain.usecase.vaccine

import br.com.moapetapp.data.repository.VaccineRepository
import br.com.moapetapp.domain.model.Vaccine
import com.benasher44.uuid.uuid4

class AddVaccineUseCase(
    private val repository: VaccineRepository,
) {
    /**
     * Adiciona uma vacina com validação de negócio.
     * Gera UUID se o id vier vazio.
     * @return Result<Vaccine> com a vacina salva (id preenchido) ou erro de validação.
     */
    suspend operator fun invoke(vaccine: Vaccine): Result<Vaccine> {
        // Valida as regras compartilhadas(nome,petId, datas)
        validateVaccine(vaccine)?.let { return Result.failure(it) }

        // Garente id (gera UUID se necessário)
        val vaccineWithId = if (vaccine.id.isBlank()) {
            vaccine.copy(id = uuid4().toString())
        } else {
            vaccine
        }

        return repository.addVaccine(vaccineWithId)
            .map { vaccineWithId }
    }
}