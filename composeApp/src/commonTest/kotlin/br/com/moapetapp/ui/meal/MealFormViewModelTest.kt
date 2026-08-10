package br.com.moapetapp.ui.meal

import br.com.moapetapp.domain.model.FoodType
import br.com.moapetapp.domain.model.Meal
import br.com.moapetapp.domain.usecase.meal.AddMealUseCase
import br.com.moapetapp.domain.usecase.meal.GetMealByIdUseCase
import br.com.moapetapp.domain.usecase.meal.ScheduleFoodReminderUseCase
import br.com.moapetapp.domain.usecase.meal.UpdateMealUseCase
import br.com.moapetapp.domain.usecase.vaccine.FakeNotificationScheduler // reusa o do EP3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Testes do MealFormViewModel focados no roteamento de erros por campo
 * (regressão do bug em que toda validação caía no campo Nome) e no caminho de
 * salvamento com sucesso/falha.
 *
 * O ViewModel é montado com use cases REAIS sobre um FakeMealRepository, então
 * exercitamos a cadeia inteira (VM -> use case -> repo) com o fake só na borda.
 */
class MealFormViewModelTest {

    // Compartilha o scheduler com o runTest para que advanceUntilIdle controle
    // as corrotinas lançadas em viewModelScope (Dispatchers.Main).
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: FakeMealRepository
    private lateinit var scheduler: FakeNotificationScheduler
    private lateinit var viewModel: MealFormViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMealRepository()
        scheduler = FakeNotificationScheduler()
        viewModel = MealFormViewModel(
            addMealUseCase = AddMealUseCase(repository),
            updateMealUseCase = UpdateMealUseCase(repository),
            getMealByIdUseCase = GetMealByIdUseCase(repository),
            scheduleFoodReminderUseCase = ScheduleFoodReminderUseCase(scheduler),
        )
        viewModel.initialize(petId = "pet-1", mealId = null)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Preenche o formulário com dados válidos (75 dias de duração)
    private fun fillValidForm() {
        viewModel.onEvent(MealFormEvent.FoodNameChanged("Ração Premium"))
        viewModel.onEvent(MealFormEvent.TotalAmountChanged("15000"))
        viewModel.onEvent(MealFormEvent.DailyAmountChanged("200"))
        viewModel.onEvent(MealFormEvent.PurchaseDateChanged(LocalDate(2026, 1, 1).toEpochDays()))
    }

    @Test
    fun `consumo diario maior que total roteia erro para amountError`() = runTest(testDispatcher) {
        viewModel.onEvent(MealFormEvent.FoodNameChanged("Ração Premium"))
        viewModel.onEvent(MealFormEvent.TotalAmountChanged("10"))
        viewModel.onEvent(MealFormEvent.DailyAmountChanged("20"))
        viewModel.onEvent(MealFormEvent.PurchaseDateChanged(LocalDate(2026, 1, 1).toEpochDays()))

        viewModel.onEvent(MealFormEvent.Save)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.amountError)      // erro cai no campo certo
        assertNull(state.nameError)           // e NÃO no Nome (o bug)
        assertNull(state.dateError)
        assertTrue(repository.addedMeals.isEmpty()) // não chegou a salvar
        assertTrue(!state.isSaved)
    }

    @Test
    fun `nome em branco roteia erro para nameError`() = runTest(testDispatcher) {
        viewModel.onEvent(MealFormEvent.TotalAmountChanged("15000"))
        viewModel.onEvent(MealFormEvent.DailyAmountChanged("200"))
        viewModel.onEvent(MealFormEvent.PurchaseDateChanged(LocalDate(2026, 1, 1).toEpochDays()))

        viewModel.onEvent(MealFormEvent.Save)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.nameError)
        assertNull(state.amountError)
        assertNull(state.dateError)
        assertTrue(repository.addedMeals.isEmpty())
    }

    @Test
    fun `data de compra ausente roteia erro para dateError`() = runTest(testDispatcher) {
        viewModel.onEvent(MealFormEvent.FoodNameChanged("Ração Premium"))
        viewModel.onEvent(MealFormEvent.TotalAmountChanged("15000"))
        viewModel.onEvent(MealFormEvent.DailyAmountChanged("200"))
        // sem data de compra

        viewModel.onEvent(MealFormEvent.Save)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.dateError)
        assertNull(state.nameError)
        assertNull(state.amountError)
        assertTrue(repository.addedMeals.isEmpty())
    }

    @Test
    fun `formulario valido salva e marca isSaved`() = runTest(testDispatcher) {
        fillValidForm()

        viewModel.onEvent(MealFormEvent.Save)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, repository.addedMeals.size)
        assertTrue(state.isSaved)
        assertNull(state.generalError)
        // o use case de lembrete foi acionado (cancela antes de reagendar,
        // independentemente de a data cair no futuro ou não)
        assertEquals(1, scheduler.cancelledIds.size)
    }

    @Test
    fun `falha de persistencia roteia erro para generalError`() = runTest(testDispatcher) {
        repository.failOnWrite = true
        fillValidForm()

        viewModel.onEvent(MealFormEvent.Save)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.generalError)     // erro de infra vai para o erro geral
        assertNull(state.nameError)           // e não polui os campos
        assertTrue(!state.isSaved)
        assertTrue(scheduler.cancelledIds.isEmpty()) // nunca chegou a agendar
    }

    @Test
    fun `modo edicao carrega os campos do pacote existente`() = runTest(testDispatcher) {
        val existing = Meal(
            id = "meal-1",
            petId = "pet-1",
            foodName = "Ração Antiga",
            foodType = FoodType.RAW,
            totalAmount = 30,
            dailyAmount = 2,
            purchaseDate = LocalDate(2026, 1, 1),
            reminderDaysBefore = 7,
            notes = "sabor frango",
        )
        repository.seed(existing)

        viewModel.initialize(petId = "pet-1", mealId = "meal-1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isEditMode)
        assertEquals("Ração Antiga", state.foodName)
        assertEquals(FoodType.RAW, state.foodType)
        assertEquals("30", state.totalAmount)
        assertEquals("2", state.dailyAmount)
        assertEquals("7", state.reminderDaysBefore)
        assertEquals("sabor frango", state.notes)
    }
}
