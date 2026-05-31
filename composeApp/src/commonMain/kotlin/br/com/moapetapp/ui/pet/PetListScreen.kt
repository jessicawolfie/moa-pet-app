package br.com.moapetapp.ui.pet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.moapetapp.presentation.pet.PetListEvent
import br.com.moapetapp.presentation.pet.PetListViewModel
import br.com.moapetapp.ui.components.EmptyPetsState
import br.com.moapetapp.ui.components.PetCard
import org.koin.compose.viewmodel.koinViewModel

/**
 * Tela de lista de pets
 *
 * @param onPetClick Callback ao tocar em um pet (navegar para detalhes)
 * @param onAddPetClick Callback ao tocar no botão adicionar
 * @param viewModel injetado via koin
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    onPetClick: (petId: String) -> Unit,
    onAddPetClick: () -> Unit,
    viewModel: PetListViewModel = koinViewModel()
) {
    // Observa o estado do viewModel respeitando lifecycle
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // SnackbarHost para exibir erros
    val snackBarHostState = remember { SnackbarHostState() }

    // Exibe snackbar quando há erro
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackBarHostState.showSnackbar(message)
            viewModel.onEvent(PetListEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meus Pets") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPetClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar pet"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            when {
                // Estado: carregando
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }

                // Estado: vazio (sem pets)
                uiState.isEmpty -> {
                    EmptyPetsState(onAddClick = onAddPetClick)
                }

                // Estado: com pets (grid)
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.pets,
                            key = { pet -> pet.id } // key para performance
                        ) {
                            pet ->
                            PetCard(
                                pet = pet,
                                onClick = { onPetClick(pet.id)}
                            )
                        }
                    }
                }
            }
        }
    }
}