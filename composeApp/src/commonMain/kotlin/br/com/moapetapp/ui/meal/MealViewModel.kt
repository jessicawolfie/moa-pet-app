package br.com.moapetapp.ui.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.moapetapp.domain.model.Meal
import br.com.moapetapp.domain.usecase.meal.GetCurrentMealUseCase
import br.com.moapetapp.domain.usecase.meal.GetHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de tela de alimentação de um pet
 *
 * @property currentMeal o pacote ativo (null = nenhum cadastro ainda)
 * @property history pacotes anteriores (inclui o atual; a tela decide como separar)
 * @property isLoading true enquanto carrega
 */
data class MealUiState(
    val currentMeal: Meal? = null,
    val history: List<Meal> = emptyList(),
    val isLoading: Boolean = true
)

class MealViewModel(
    private val getCurrentMealUseCase: GetCurrentMealUseCase,
    private val getMealHistoryUseCase: GetHistoryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealUiState())
    val uiState: StateFlow<MealUiState> = _uiState.asStateFlow()

    /**
     * Observa o pacote atual e o histórico do pet. Chamado pela tela ao abrir.
     * Duas coletas paralelas - cada Flow atualiza sua parte do state.
     */
    fun observeMeal(petId: String) {
        // Pacote atual
        viewModelScope.launch {
            getCurrentMealUseCase(petId).collect { meal ->
                _uiState.value = _uiState.value.copy(currentMeal = meal, isLoading = false)
            }
        }
        // Histórico
        viewModelScope.launch {
            getMealHistoryUseCase(petId).collect { history ->
                _uiState.value = _uiState.value.copy(history = history)
            }
        }
    }
}