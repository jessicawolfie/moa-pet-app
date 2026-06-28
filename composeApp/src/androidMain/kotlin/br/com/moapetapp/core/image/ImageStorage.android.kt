package br.com.moapetapp.core.image

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import org.koin.mp.KoinPlatform.getKoin
import java.nio.file.Files.exists

// actual da fábrica - espelha o getDatabaseBuilder(): puxa o context do koin
actual fun provideImageStorage(): ImageStorage =
    AndroidImageStorage(getKoin().get<Context>())

private class AndroidImageStorage(
    private val context: Context,
): ImageStorage {

    // Subpasta dedicada dentro do diretório privado
    private val imagesDir: File
        get() = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }

    override suspend fun save(bytes: ByteArray, fileName: String) {
        // Escrita em disco fora da main thread
        withContext(Dispatchers.IO) {
            File(imagesDir, fileName).writeBytes(bytes)
        }
    }

    // Recalculado a cada leitura
    override fun absolutePathFor(fileName: String): String =
        File(imagesDir, fileName).absolutePath

    override suspend fun delete(fileName: String) {
        withContext(Dispatchers.IO) {
            File(imagesDir, fileName).delete()
        }
    }
}

