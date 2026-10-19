package com.example.postventa.ui.solicitud

data class SolicitudUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val servicio: String = "",
    val evidencias: List<String> = emptyList(),
    val estado: String = "Recibida",
    val tituloError: String? = null,
    val descripcionError: String? = null,
    val categoriaError: String? = null,
    val servicioError: String? = null,
    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
)