package br.com.moapetapp.ui.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.moapetapp.core.image.ImageStorage
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: String,
    onNavigateBack: () -> Unit,
    onVaccinesClick: (petId: String) -> Unit,
    onEditClick: (petId: String) -> Unit,
    viewModel: PetDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val imageStorage = koinInject<ImageStorage>()

    // Carrega o pet ao abrir a tela
    LaunchedEffect(petId) {
        viewModel.loadPet(petId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalhes do Pet") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(petId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar pet")
                    }
                },
            )
        },
    ) { padding ->
        val pet = uiState.pet
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            pet == null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(text = "Pet não encontrado")
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Header: foto + nome
                    val photoModel = pet.photoFileName?.let {
                        "file://" + imageStorage.absolutePathFor(it)
                    }
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (photoModel != null) {
                            AsyncImage(
                                model = photoModel,
                                contentDescription = "Foto do ${pet.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Text(pet.name.take(1).uppercase(), style = MaterialTheme.typography.headlineLarge)
                        }
                    }

                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    // Informações básicas
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Informações básicas", style = MaterialTheme.typography.titleMedium)
                        InfoRow("Especie", pet.species.displayName())
                        pet.breed?.let { InfoRow("Raça", it) }
                        InfoRow("Idade", pet.ageFormatted)
                        pet.weightKg?.let { InfoRow("Peso", "${it}kg") }
                    }

                    // Saúde: cards clicáveis
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Saúde", style = MaterialTheme.typography.titleMedium)

                        // Vacinas - único ativo por enquanto
                        HealthCard(
                            title = "Vacinas",
                            subtitle = "Ver histórico e próximas doses",
                            onClick = { onVaccinesClick(petId) },
                        )
                        // Medicações e consultas: visíveis mas intativos ainda
                        HealthCard(title = "Medicações", subtitle = "Em breve", onClick = null)
                        HealthCard(title = "Consultas", subtitle = "Em breve", onClick = null)
                    }
                }
            }
        }
    }
}

// Linha rótulo -> valor das informações básicas
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

// Card de seção de saúde. onClick null = inativo (mostra em breve)
@Composable
private fun HealthCard(title: String, subtitle: String, onClick: (() -> Unit)?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (onClick != null) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }
    }
}
