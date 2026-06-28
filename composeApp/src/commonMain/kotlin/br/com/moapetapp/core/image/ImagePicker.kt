package br.com.moapetapp.core.image

import androidx.compose.runtime.Composable

// Contrato comum para seleção de imagem
interface ImagePicker {
    fun launch()
}

// Fábrica @Composable que cria um [ImagePicker] já amarrado ao ciclo de vida da plataforma
//@param onImagePicked callback chamado apenas em caso de sucesso, com os bytes da imagem já comprimida em JPEG
@Composable
expect fun rememberImagePicker(
    onImagePicked: (ByteArray) -> Unit
): ImagePicker