package br.com.moapetapp

import androidx.compose.ui.window.ComposeUIViewController
import br.com.moapetapp.di.initKoin

fun MainViewController() = ComposeUIViewController {
    App()
}