package br.com.moapetapp.core.image

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile

actual fun provideImageStorage(): ImageStorage = IosImageStorage()

private class IosImageStorage : ImageStorage {

    // Diretório Documents do sandbox do app
    private fun documentsDir(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSDocumentDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true,
        )
        return paths.first() as String
    }

    private fun pathFor(fileName: String): String = "${documentsDir()}/$fileName"

    override suspend fun save(bytes: ByteArray, fileName: String) {
        withContext(Dispatchers.Default) {
            bytes.toNSData().writeToFile(pathFor(fileName), atomically = true)
        }
    }

    // Recalculado a cada leitura
    override fun absolutePathFor(fileName: String): String = pathFor(fileName)

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun delete(fileName: String) {
        withContext(Dispatchers.Default) {
            NSFileManager.defaultManager.removeItemAtPath(pathFor(fileName), error = null)
        }
    }
}

// ByteArray -> NSData (cópia de memória: o caminho inverso do toByteArray do picker)
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData()
    return usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
}