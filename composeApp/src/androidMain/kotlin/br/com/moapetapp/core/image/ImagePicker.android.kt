package br.com.moapetapp.core.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberImagePicker(
    onImagePicked: (ByteArray) -> Unit,
): ImagePicker {
    // Context atual - necessário pra resolver o Uri retornado pela galeria
    val context = LocalContext.current

    // Launcher do contrato GetContent: abre e devolve o Uri? (null = cancelou)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Cancelamento => uri null = não dispara callback (respeita o contrato do commonMain)
        if (uri != null) {
            // Abre o uri como stream
            val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
                // Decodifica os bytes brutos em Bitmap na memória
                val bitmap = BitmapFactory.decodeStream(input)
                if (bitmap != null) {
                    // Recomprime o Bitmap em JPEG 80% para um buffer de bytes
                    ByteArrayOutputStream().use { output ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                        bitmap.recycle() // libera memória nativa do Bitmap já comprimido
                        output.toByteArray() // bytes finais, já no formato que o commonMain espera
                    }
                } else {
                    null
                }
            }
            // Só entrega se a leitura/compressão deu certo
            bytes?.let { onImagePicked(it) }
        }
    }

    // Devolve o ImagePicker: apenas amarra launch() ao launcher acima
    // remember(launcher) evita recriar o objeto a cada recomposição
    return remember(launcher) {
        object : ImagePicker {
            override fun launch() {
                // "image/*" filtra a galeria para mostrar só imagens
                launcher.launch("image/*")
            }
        }
    }
}
