package br.com.moapetapp.ui.vaccine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.moapetapp.domain.model.Vaccine
import br.com.moapetapp.domain.usecase.vaccine.GetVaccineForPetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado da lista de vacinas de um pet
 * @property upcoming vacinas com próxima dose futura
 * @property applied vacinas sem próxima dose ou já aplicadas
 * @property isLoading true enquanto carrega
 */
data class VaccineListUiState(
    val upcoming: List<Vaccine> = emptyList(),
    val applied: List<Vaccine> = emptyList(),
    val isLoading: Boolean = true
)

class VaccineListViewModel(
    private val getVaccineForPetUseCase: GetVaccineForPetUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(VaccineListUiState())
    val uiState: StateFlow<VaccineListUiState> = _uiState.asStateFlow()

    /**
     * Observa as vacinas do pet. Chamado pela tela ao abrir
     * Como o use case retorna Flow, a lista atualiza sozinha a cada mudança no banco
     */
    fun observeVaccines(petId: String) {
        viewModelScope.launch {
            getVaccineForPetUseCase(petId).collect { vaccines ->
                // Separa em "próximas"(tem próxima dose) e "aplicadas"(sem próxima dose)
                val (upcoming, applied) = vaccines.partition { it.nextDoseDate != null }
                _uiState.value = VaccineListUiState(
                    upcoming = upcoming,
                    applied = applied,
                     isLoading = false,
                )
            }
        }
    }
}