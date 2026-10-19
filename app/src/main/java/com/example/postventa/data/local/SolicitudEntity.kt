package com.example.postventa.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val servicio: String,
    val evidenciaUri: String?,
    val estado: String = "Recibida",
    val fechaHora: Long = System.currentTimeMillis()
)