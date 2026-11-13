package com.example.postventa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.postventa.data.local.AppDatabase
import com.example.postventa.data.repository.SolicitudRepository
import com.example.postventa.navigation.AppNavigation
import com.example.postventa.ui.solicitud.SolicitudViewModel
import com.example.postventa.ui.solicitud.SolicitudViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        val repository = SolicitudRepository(database.solicitudDao())
        val factory = SolicitudViewModelFactory(repository)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewModel: SolicitudViewModel = viewModel(factory = factory)
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}