package br.com.moapetapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Color-scheme claro do Moa - mapeia a paleta pros papéis semânticos do Material3
private val MoaLightColors = lightColorScheme(
    primary = MoaPurple,
    onPrimary = MoaSurface,
    primaryContainer = MoaPurpleContainer,
    onPrimaryContainer = MoaDarkText,

    secondary = MoaGreen,
    onSecondary = MoaSurface,
    secondaryContainer = MoaGreenContainer,
    onSecondaryContainer = MoaDarkText,

    tertiary = MoaTerracotta,
    onTertiary = MoaSurface,
    tertiaryContainer = MoaTerracottaContainer,
    onTertiaryContainer = MoaDarkText,

    background = MoaNeutral,
    onBackground = MoaDarkText,
    surface = MoaSurface,
    onSurface = MoaDarkText,
    surfaceVariant = MoaNeutral,
    onSurfaceVariant = Color(0xFF49454F),
)

// Cantos arredondados
private val MoaShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun MoaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MoaLightColors,
        shapes = MoaShapes,
        content = content
    )
}