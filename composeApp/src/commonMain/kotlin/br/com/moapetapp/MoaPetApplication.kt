package br.com.moapetapp

import br.com.moapetapp.di.initKoin

// Inicializa o Koin quando o app inicia
object MoaPetApplication {
    fun initialize() {
        initKoin()
    }
}

