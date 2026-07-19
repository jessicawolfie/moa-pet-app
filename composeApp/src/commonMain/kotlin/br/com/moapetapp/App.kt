package br.com.moapetapp

import androidx.compose.runtime.Composable
import br.com.moapetapp.ui.MoaPetNavHost
import br.com.moapetapp.ui.theme.MoaTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    KoinContext {
        MoaTheme {
            MoaPetNavHost()
        }
    }
}
