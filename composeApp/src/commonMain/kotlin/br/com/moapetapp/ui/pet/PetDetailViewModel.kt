package br.com.moapetapp.ui.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.moapetapp.domain.model.Pet
import br.com.moapetapp.domain.usecase.pet.GetPetByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado da tela de detalhes do pet
 * @property pet o pet carregado (null enquanto carrega ou se falhar)
 * @property isLoading true enquanto carrega
 * @property errorMessage mensagem de erro, se houver
 */
data class PetDetailUiState(
    val pet: Pet? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class PetDetailViewModel(
    private val getPetByIdUseCase: GetPetByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PetDetailUiState())
    val uiState: StateFlow<PetDetailUiState> = _uiState.asStateFlow()

    // Carrega o pet pelo id. Chamado pela tela ao abrir
    fun loadPet(petId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            getPetByIdUseCase(petId)
                .onSuccess { pet ->
                    _uiState.value = _uiState.value.copy(pet = pet, isLoading = false)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false,
                        errorMessage = error.message?: "Erro ao carregar o pet."
                    )
                }
        }
    }
}
