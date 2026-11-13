package com.example.postventa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val codigoTracking: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val prioridad: String = "Pendiente",
    val estado: String = "Recibida",
    val clienteId: String = "CLI-001",
    val empresaFicticia: String = "Pyme Ejemplo Ltda.",
    val servicio: String = "",
    val tecnicoAsignadoId: String? = null,
    val evidenciaUri: String? = null,
    val comentarioTecnico: String? = null,
    val fechaHora: Long = System.currentTimeMillis(),
    val fechaCreacion: String = "",
    val fechaModificacion: String = ""
)
