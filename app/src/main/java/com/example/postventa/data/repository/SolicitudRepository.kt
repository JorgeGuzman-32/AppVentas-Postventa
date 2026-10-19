package com.example.postventa.data.repository

import com.example.postventa.data.local.SolicitudDao
import com.example.postventa.data.local.SolicitudEntity
import kotlinx.coroutines.flow.Flow

class SolicitudRepository(private val dao: SolicitudDao) {
    val todasLasSolicitudes: Flow<List<SolicitudEntity>> = dao.getAll()

    suspend fun guardarSolicitud(solicitud: SolicitudEntity) {
        dao.insertOrUpdate(solicitud)
    }
}