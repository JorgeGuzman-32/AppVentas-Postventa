package com.example.postventa.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    fun getAllSolicitudes(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun getSolicitudById(id: Long): SolicitudEntity?

    @Query("SELECT * FROM solicitudes WHERE clienteId = :clienteId ORDER BY id DESC")
    fun getSolicitudesByCliente(clienteId: String): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE tecnicoAsignadoId = :tecnicoId ORDER BY id DESC")
    fun getSolicitudesByTecnico(tecnicoId: String): Flow<List<SolicitudEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolicitud(solicitud: SolicitudEntity): Long

    @Update
    suspend fun updateSolicitud(solicitud: SolicitudEntity)

    @Query("DELETE FROM solicitudes WHERE id = :id")
    suspend fun deleteSolicitudById(id: Long)
}
