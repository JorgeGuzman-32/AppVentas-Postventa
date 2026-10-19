package com.example.postventa.ui.solicitud

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.postventa.data.local.SolicitudEntity
import com.example.postventa.data.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SolicitudViewModel(private val repository: SolicitudRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    val listaSolicitudes: StateFlow<List<SolicitudEntity>> =
        repository.todasLasSolicitudes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onTituloChanged(valor: String) {
        _uiState.update { it.copy(titulo = valor, tituloError = null) }
    }

    fun onDescripcionChanged(valor: String) {
        _uiState.update { it.copy(descripcion = valor, descripcionError = null) }
    }

    fun onCategoriaChanged(valor: String) {
        _uiState.update { it.copy(categoria = valor, categoriaError = null) }
    }

    fun onServicioChanged(valor: String) {
        _uiState.update { it.copy(servicio = valor, servicioError = null) }
    }

    // Agrega una o varias fotos a la lista existente de evidencias
    fun agregarEvidencias(nuevasUris: List<String>) {
        _uiState.update { actual ->
            val listaActualizada = actual.evidencias + nuevasUris
            actual.copy(evidencias = listaActualizada.distinct())
        }
    }

    // Permite quitar una evidencia de la lista antes de guardar
    fun removerEvidencia(uri: String) {
        _uiState.update { actual ->
            actual.copy(evidencias = actual.evidencias.filter { it != uri })
        }
    }

    private fun dispararVibracion(context: Context, esError: Boolean) {
        val duracion = if (esError) 250L else 70L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(duracion, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            @Suppress("DEPRECATION")
            vibrator?.vibrate(duracion)
        }
    }

    fun guardarSolicitud(context: Context) {
        val actual = _uiState.value
        val tErr = if (actual.titulo.isBlank()) "El título es obligatorio" else null
        val dErr = if (actual.descripcion.isBlank()) "La descripción del fallo es obligatoria" else null
        val cErr = if (actual.categoria.isBlank()) "Seleccione una categoría" else null
        val sErr = if (actual.servicio.isBlank()) "Seleccione un servicio" else null

        if (tErr != null || dErr != null || cErr != null || sErr != null) {
            dispararVibracion(context, esError = true)
            _uiState.update {
                it.copy(
                    tituloError = tErr,
                    descripcionError = dErr,
                    categoriaError = cErr,
                    servicioError = sErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }

            // Empacamos la lista de URIs en un String delimitado por comas para Room
            val cadenaUris = if (actual.evidencias.isNotEmpty()) {
                actual.evidencias.joinToString(separator = ",")
            } else {
                null
            }

            val nueva = SolicitudEntity(
                titulo = actual.titulo.trim(),
                descripcion = actual.descripcion.trim(),
                categoria = actual.categoria.trim(),
                servicio = actual.servicio.trim(),
                evidenciaUri = cadenaUris
            )
            repository.guardarSolicitud(nueva)
            dispararVibracion(context, esError = false)
            _uiState.update { SolicitudUiState(guardadoExitosamente = true) }
        }
    }

    fun resetGuardado() {
        _uiState.update { SolicitudUiState(guardadoExitosamente = false) }
    }
}

class SolicitudViewModelFactory(private val repository: SolicitudRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolicitudViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SolicitudViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel no reconocido")
    }
}