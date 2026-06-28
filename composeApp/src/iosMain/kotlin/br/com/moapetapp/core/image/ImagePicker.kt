package br.com.moapetapp.core.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy

// 1) Ponte NSData -> ByteArray (cópia bruta da memória entre Foundation e Kotlin)
@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()  //tamanho em bytes
    val result = ByteArray(size)   //buffer kotlin de destino
    if (size > 0) {
        // usePinned "fixa" o array na memória pra copiarmos via ponteiro com segurança
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), this.bytes, this.length)
        }
    }
    return result
}

// 2) Delegate do PHPicker - herda NSObject e implementa o protocolo ObjC
private class PHPickerDelegate(
    private val onImagePicked: (ByteArray) -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    // Chamado quando o usuário termina (escolhe ou cancela)
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)   // fecha o picker sempre

        // Vazio = cancelou => sai sem chamar callback (respeita o contrato do commonMain)
        val result = didFinishPicking.firstOrNull() as? PHPickerResult ?: return
        val provider = result.itemProvider

        // Carrega os dados brutos (qualquer formato)
        provider.loadDataRepresentationForTypeIdentifier("public.image") { data, _ ->
            val nsData = data ?: return@loadDataRepresentationForTypeIdentifier

            // Decodifica e reecomprime pra JPEG 80% (mesma saída que o android)
            val image = UIImage(data = nsData)
            val jpeg = UIImageJPEGRepresentation(image, 0.8) ?: return@loadDataRepresentationForTypeIdentifier
            val bytes = jpeg.toByteArray()

            // O completion roda em background -> volta pra main thread pra atualizar a UI com segurança
            dispatch_async(dispatch_get_main_queue()) {
                onImagePicked(bytes)
            }
        }
    }
}

// 3) Helper: view controller no topo da pilha
private fun topViewController(): UIViewController? {
    var top = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (top?.presentedViewController != null) {
        top = top.presentedViewController
    }
    return top
}

// 4) actual da fábrica @Composable
@Composable
actual fun rememberImagePicker(
    onImagePicked: (ByteArray) -> Unit,
): ImagePicker {
    // Mantém o delegate estável, mas sempre chamando o callback mais recente
    val currentOnPicked = rememberUpdatedState(onImagePicked)
    
    // Delegate criado uma vez e retido aqui
    val delegate = remember {
        PHPickerDelegate { bytes -> currentOnPicked.value(bytes) }
    }
    
    return remember(delegate) {
        object : ImagePicker {
            override fun launch() {
                val config = PHPickerConfiguration().apply { 
                    selectionLimit = 1                        // uma imagem só
                    filter = PHPickerFilter.imagesFilter()   // só imagens
                }
                val picker = PHPickerViewController(configuration = config)
                picker.delegate = delegate
                topViewController()?.presentViewController(picker, animated = true, completion = null)
            }
        } 
    }
}