package com.example.postventa.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes ORDER BY fechaHora DESC")
    fun getAll(): Flow<List<SolicitudEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(solicitud: SolicitudEntity)
}