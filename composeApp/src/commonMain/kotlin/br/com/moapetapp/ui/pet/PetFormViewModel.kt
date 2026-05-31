package br.com.moapetapp.ui.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.moapetapp.domain.model.Pet
import br.com.moapetapp.domain.usecase.pet.AddPetUseCase
import br.com.moapetapp.domain.usecase.pet.GetPetByIdUseCase
import br.com.moapetapp.domain.usecase.pet.UpdatePetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * ViewModel do formulário de pet
 *
 * @property addPetUseCase Use Case pra adiconar
 * @property updatePetUseCase Use Case para atualizar
 * @property getPetByUseCase Use Case para carregar um pet em edição
  */
class PetFormViewModel(
    private val addPetUseCase: AddPetUseCase,
    private val updatePetUseCase: UpdatePetUseCase,
    private val getPetByIdUseCase: GetPetByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow((PetFormUiState()))
    val uiState: StateFlow<PetFormUiState> = _uiState.asStateFlow()

    // Guarda o ID do pet em edição (null = criando novo)
    private var editingPetId: String? = null

    /**
     * Carrega um pet existente para edição
     * Chamado pela tela quando há petId na rota
     *
     * @param petId UUID do pet a editar
     */
    fun loadPet(petId: String) {
        editingPetId = petId
        viewModelScope.launch {
            getPetByIdUseCase(petId).onSuccess { pet ->
                _uiState.value = _uiState.value.copy(
                    name = pet.name,
                    species = pet.species,
                    breed = pet.breed ?: "",
                    birthDateEpochDays = pet.birthDate?.toEpochDays(),
                    weight = pet.weightKg.toString() ?: "",
                    photoPath = pet.photoPath,
                    isEditMode = true
                )
            }
        }
    }

    // Processa eventos da UI
    fun onEvent(event: PetFormEvent) {
        when (event) {
            is PetFormEvent.NameChanged -> {
                _uiState.value = _uiState.value.copy(
                    name = event.name,
                    nameError = null // limpa o erro ao digitar
                )
            }
            is PetFormEvent.SpeciesChanged -> {
                _uiState.value = _uiState.value.copy(species = event.species)
            }
            is PetFormEvent.BreedChanged -> {
                _uiState.value = _uiState.value.copy(breed = event.breed)
            }
            is PetFormEvent.BirthDateChanged -> {
                _uiState.value = _uiState.value.copy(birthDateEpochDays = event.epochDays)
            }
            is PetFormEvent.WeightChanged -> {
                _uiState.value = _uiState.value.copy(
                    weight = event.weight,
                    weightError = null
                )
            }
            is PetFormEvent.Save -> savePet()
        }
    }

    // Valida e salva o pet
    private fun savePet() {
        val state = _uiState.value

        // Validação
        var hasError = false

        if (state.name.isBlank()) {
            _uiState.value = _uiState.value.copy(nameError = "Nome é obrigatório")
            hasError = true
        }

        // Valida peso se preenchido
        val weightValue = state.weight.toDoubleOrNull()
        if (state.weight.isNotBlank() && weightValue == null) {
            _uiState.value = _uiState.value.copy(weightError = "Peso inválido")
            hasError = true
        }

        if (hasError) return

        // Monta o Pet de domínio
        val pet = Pet(
            id = editingPetId ?: "", // vazio = AddPetUseCase gera UUID
            name = state.name.trim(),
            species = state.species,
            breed =  state.breed.trim().ifBlank { null },
            birthDate = state.birthDateEpochDays?.let { LocalDate.fromEpochDays(it)},
            weightKg = weightValue,
            photoPath = state.photoPath
        )

        // Salva (add ou update conforme o modo)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val result = if (state.isEditMode) {
                updatePetUseCase(pet)
            } else {
                addPetUseCase(pet).map {  } // converte Result<Pet> para Result<Unit>
            }

            result.onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    nameError = exception.message
                )
            }
        }
    }
}