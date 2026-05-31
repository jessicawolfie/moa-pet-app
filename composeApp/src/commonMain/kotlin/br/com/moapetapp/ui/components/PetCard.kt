package br.com.moapetapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.room.util.TableInfo
import br.com.moapetapp.domain.model.Pet

/**
 * Card que exibe informações resumidas de um pet
 *
 * @param pet Pet a exibir
 * @param onClick Callback ao tocar no card (abrir detalhes)
 * @param modifier Modifier opcional para customização
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetCard(
    pet: Pet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto do pet ou placeholder com inicial
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    // Primeira letra do nome em maiúscula
                    text = pet.name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nome do pet
            Text(
                text = pet.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Espécie com ícone de pata
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🐾",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pet.species.displayName(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badge de idade (verde se filhote e cinza se adulto)
            if (pet.ageInMonths != null){
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (pet.isPuppy) {
                        MaterialTheme.colorScheme.tertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    }
                ) {
                    Text(
                        text = pet.ageFormatted,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = if (pet.isPuppy) {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                }
            }
        }
    }
}