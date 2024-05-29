package com.example.regresoacasa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.regresoacasa.ui.CurrentLocationScreen
import com.example.regresoacasa.ui.RegresoACasaViewModel
import com.example.regresoacasa.ui.RegresoACasaViewModelFactory
import com.example.regresoacasa.ui.theme.RegresoACasaTheme


class MainActivity : ComponentActivity() {
    private lateinit var regresoACasaViewModel: RegresoACasaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as AppAplication
        val container = application.getAppContainer()
        regresoACasaViewModel = RegresoACasaViewModelFactory(container.routeRepository).create(RegresoACasaViewModel::class.java)

        setContent {
            RegresoACasaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CurrentLocationScreen(regresoACasaViewModel)
                }
            }
        }
    }
}

