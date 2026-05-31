package br.com.moapetapp.presentation.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.moapetapp.domain.usecase.pet.DeletePetUseCase
import br.com.moapetapp.domain.usecase.pet.GetAllPetsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * ViewModel da tela de lista de peets
 *
 * @property getAllPetsUseVase Use Case para observar pets
 * @property deletePetUseCase Use Case para deletar pet
 */
class PetListViewModel(
    private val getAllPetsUseCase: GetAllPetsUseCase,
    private val deletePetUseCase: DeletePetUseCase
) : ViewModel() {

    // Estado privado (mutável) e público (somente leitura)

    // _uiState é privado e muitável
    private val _uiState = MutableStateFlow(PetListUiState())

    // uiState é público e somente leitura
    val uiState: StateFlow<PetListUiState> = _uiState.asStateFlow()


    // Inicialização
    init {
        observePets()
    }

    // Observa o Flow de pets continuamente
    // Sempre que o banco muda  (insert/update/delete), a lista atualiza sozinha
    private fun observePets() {
        viewModelScope.launch {
            getAllPetsUseCase()
                .onStart {
                    // Antes de começar a coletar, mostra loading
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
                .catch { exception ->
                    // Se der erro no Flow, atualiza estado com mensagem
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar pets: ${exception.message}"
                    )
                }
                .collect { pets ->
                    // A cada emissão do Flow , atualiza a lista
                    _uiState.value = _uiState.value.copy(
                        pets = pets,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }

    // Processamento de eventos da UI

    // Ponto único de entrada para eventos da UI
    // @param event Evento disparado pela UI
    fun onEvent(event: PetListEvent) {
        when (event) {
            is PetListEvent.LoadPets -> observePets()
            is PetListEvent.DeletePet -> deletePet(event.petId)
            is PetListEvent.ClearError -> clearError()
        }
    }

    // Deleta um pet e trata um resultado
    private fun deletePet(petId: String) {
        viewModelScope.launch {
            deletePetUseCase(petId)
                .onFailure { exception ->
                    // Se falhar, mostra erro
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Erro ao deletar: ${exception.message}"
                    )
                }
            // Se sucesso, não faz nada
            // Automaticamente emite a lista atualizada sem o pet deletado
        }
    }

    // Limpa a mensagem de erro após exibir
    private fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}