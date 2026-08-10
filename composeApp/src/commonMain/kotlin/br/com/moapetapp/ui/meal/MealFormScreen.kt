package br.com.moapetapp.ui.meal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.moapetapp.domain.model.FoodType
import br.com.moapetapp.ui.components.AppDateField
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealFormScreen(
    petId: String,
    mealId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: MealFormViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(petId, mealId) {
        viewModel.initialize(petId, mealId)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    LaunchedEffect(uiState.generalError) {
        uiState.generalError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Unidade dinâmica conforme o tipo escolhido
    val unit = uiState.foodType.unitLabel

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Editar Pacote" else "Novo Pacote") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = { viewModel.onEvent(MealFormEvent.Save) },
                    enabled = !uiState.isSavingMode,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    if (uiState.isSavingMode) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Salvar Pacote")
                    }
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Nome
            OutlinedTextField(
                value = uiState.foodName,
                onValueChange = { viewModel.onEvent(MealFormEvent.FoodNameChanged(it)) },
                label = { Text("Nome *") },
                placeholder = { Text("Ex: Ração Premium") },
                isError = uiState.nameError != null,
                supportingText = uiState.nameError?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Seletor de tipo (Ração / Natural) - SegmentedButton
            Text("Tipo", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                FoodType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = uiState.foodType == type,
                        onClick = { viewModel.onEvent(MealFormEvent.FoodTypeChanged(type)) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = FoodType.entries.size),
                    ) {
                        Text(type.displayName)
                    }
                }
            }

            // Quantidade total - unidade dinâmica
            OutlinedTextField(
                value = uiState.totalAmount,
                onValueChange = { viewModel.onEvent(MealFormEvent.TotalAmountChanged(it)) },
                label = { Text("Quantidade total($unit) *") },
                placeholder = { Text(if (unit == "g") "Ex: 15000" else "Ex: 15") },
                isError = uiState.amountError != null,
                supportingText = uiState.amountError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Consumo diário - unidade dinâmica
            OutlinedTextField(
                value = uiState.dailyAmount,
                onValueChange = { viewModel.onEvent(MealFormEvent.DailyAmountChanged(it)) },
                label = { Text("Consumo diário($unit) *") },
                placeholder = { Text(if (unit == "g") "Ex: 300" else "Ex: 1") },
                isError = uiState.amountError != null,
                supportingText = uiState.amountError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Data de compra
            AppDateField(
                label = "Data de compra *",
                epochDays = uiState.purchaseDateEpochDays,
                onDateSelected = { viewModel.onEvent(MealFormEvent.PurchaseDateChanged(it)) },
                isError = uiState.dateError != null,
                supportingText = uiState.dateError,
            )

            // Antecedência do lembrete
            OutlinedTextField(
                value = uiState.reminderDaysBefore,
                onValueChange = { viewModel.onEvent(MealFormEvent.ReminderDaysChanged(it)) },
                label = { Text("Avisar quantos dias antes de acabar") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Notas
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onEvent(MealFormEvent.NotesChanged(it)) },
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )

            // Erro geral
            uiState.generalError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
