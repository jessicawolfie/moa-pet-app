package br.com.moapetapp.ui.vaccine

import br.com.moapetapp.core.notification.rememberNotificationPermissionRequester
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.moapetapp.ui.components.AppDateField
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineFormScreen(
    petId: String,
    vaccineId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: VaccineFormViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Inicializa (guarda petId + carrega vacina se for edição)
    LaunchedEffect(petId, vaccineId) {
        viewModel.initialize(petId, vaccineId)
    }

    // Volta ao salvar com sucesso
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    val permissionRequester = rememberNotificationPermissionRequester { _ ->
        // Concedendo ou negando, prossegue com o save.
        viewModel.onEvent(VaccineFormEvent.Save)
    }

    Scaffold(
        topBar = {
         TopAppBar(
             title = { Text(if (uiState.isEditMode) "Editar vacina" else "Nova vacina") },
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
                    onClick = {
                        // Com próxima dose -> pede permissão antes (pro lembrete funcionar).
                        // Sem próxima dose -> não há lembrete -> salva direto.
                        if (uiState.nextDoseDateEpochDays != null) {
                            permissionRequester.request()
                        } else {
                            viewModel.onEvent(VaccineFormEvent.Save)
                        }
                    },
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Salvar Vacina")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nome (obrigatório)
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.onEvent(VaccineFormEvent.NameChanged(it)) },
                label = { Text("Nome *") },
                placeholder = { Text("Ex: V10") },
                isError = uiState.nameError != null,
                supportingText = { uiState.nameError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Data de aplicação (obrigatória)
            AppDateField(
                label = "Data de aplicação *",
                epochDays = uiState.appliedDateEpochDays,
                onDateSelected = { viewModel.onEvent(VaccineFormEvent.AppliedDateChanged(it)) },
                isError = uiState.dateError != null,
                supportingText = uiState.dateError,
            )

            // Próxima dose (opcional)
            AppDateField(
                label = "Próxima dose",
                epochDays = uiState.nextDoseDateEpochDays,
                onDateSelected = { viewModel.onEvent(VaccineFormEvent.NextDoseDateChanged(it)) },
            )

            // Antecedência do lembrete
            OutlinedTextField(
                value = uiState.reminderDaysBefore,
                onValueChange = { viewModel.onEvent(VaccineFormEvent.ReminderDaysBeforeChanged(it)) },
                label = { Text("Avisar quantos dias antes da próxima vacina") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Veterinário(opcional)
            OutlinedTextField(
                value = uiState.veterinarian,
                onValueChange = { viewModel.onEvent(VaccineFormEvent.VeterinarianChanged(it)) },
                label = { Text("Veterinário") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Notas(opcional)
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.onEvent(VaccineFormEvent.NotesChanged(it)) },
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
            )
        }
    }
}