package br.com.moapetapp.core.image

// Persistência de imagens no diretório privado do app
// Contrato comum: cada plataforma fornece sua implementação

interface ImageStorage {
    /**
     * Grava os bytes no diretório priovado
     * @param fileName nome simples do arquivo
     * susped porque escrita em disco é I/O = o chamador roda fora da main thread
     */
    suspend fun save(bytes: ByteArray, fileName: String)

    /**
     * Reoslve o nome do arquivo para o caminho absoluto atual
     * Chamado só na hora de ler/exibir
     */
    fun absolutePathFor(fileName: String): String

    // Apaga o arquivo (troca de foto ou exclusão do pet
    suspend fun delete(fileName: String)
}

/**
 * Fábrica fornecida por cada plataforma. Mesmo padrão do getDatabaseBuilder():
 * o actual pega as dependências de plataforma na hora
 */
expect fun provideImageStorage(): ImageStorage