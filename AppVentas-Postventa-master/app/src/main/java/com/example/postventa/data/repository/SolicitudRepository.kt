package com.example.postventa.data.repository

import com.example.postventa.data.local.SolicitudDao
import com.example.postventa.data.local.SolicitudEntity
import kotlinx.coroutines.flow.Flow

class SolicitudRepository(private val solicitudDao: SolicitudDao) {
    val todasLasSolicitudes: Flow<List<SolicitudEntity>> = solicitudDao.getAllSolicitudes()

    fun getAllSolicitudes(): Flow<List<SolicitudEntity>> = solicitudDao.getAllSolicitudes()

    fun getSolicitudesByCliente(clienteId: String): Flow<List<SolicitudEntity>> =
        solicitudDao.getSolicitudesByCliente(clienteId)

    fun getSolicitudesByTecnico(tecnicoId: String): Flow<List<SolicitudEntity>> =
        solicitudDao.getSolicitudesByTecnico(tecnicoId)

    suspend fun getSolicitudById(id: Long): SolicitudEntity? = solicitudDao.getSolicitudById(id)

    suspend fun insertSolicitud(solicitud: SolicitudEntity): Long = solicitudDao.insertSolicitud(solicitud)

    suspend fun guardarSolicitud(solicitud: SolicitudEntity): Long = solicitudDao.insertSolicitud(solicitud)

    suspend fun updateSolicitud(solicitud: SolicitudEntity) = solicitudDao.updateSolicitud(solicitud)
}
