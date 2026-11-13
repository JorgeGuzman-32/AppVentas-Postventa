package com.example.postventa.ui.solicitud

import com.example.postventa.data.local.SolicitudEntity

data class SolicitudUiState(
    val titulo: String = "",
    val tituloError: String? = null,
    val descripcion: String = "",
    val descripcionError: String? = null,
    val categoria: String = "Desarrollo web",
    val categoriaError: String? = null,
    val servicio: String = "",
    val servicioError: String? = null,
    val evidencias: List<String> = emptyList(),
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false,
    val solicitudes: List<SolicitudEntity> = emptyList(),
    val solicitudSeleccionada: SolicitudEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
