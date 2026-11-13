package com.example.postventa.ui.solicitud

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.postventa.data.local.SolicitudEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudFormScreen(
    solicitudExistente: SolicitudEntity? = null,
    viewModel: SolicitudViewModel,
    onGuardarClick: (SolicitudEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    val perfil by viewModel.perfilUsuario.collectAsState()
    val esEdicion = solicitudExistente != null
    val context = LocalContext.current

    var titulo by remember(solicitudExistente) { mutableStateOf(solicitudExistente?.titulo ?: "") }
    var descripcion by remember(solicitudExistente) { mutableStateOf(solicitudExistente?.descripcion ?: "") }
    var categoria by remember(solicitudExistente) { mutableStateOf(solicitudExistente?.categoria ?: "Desarrollo web") }
    var prioridad by remember(solicitudExistente) { mutableStateOf(solicitudExistente?.prioridad ?: "Media") }
    var servicio by remember(solicitudExistente) { mutableStateOf(solicitudExistente?.servicio ?: "") }
    var estado by remember(solicitudExistente) { mutableStateOf(solicitudExistente?.estado ?: "Recibida") }

    var listaFotos by remember(solicitudExistente) {
        mutableStateOf(
            solicitudExistente?.evidenciaUri?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && tempPhotoUri != null) {
            listaFotos = listaFotos + tempPhotoUri.toString()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            listaFotos = listaFotos + uris.map { it.toString() }
        }
    }

    fun tomarFoto() {
        val file = File(context.cacheDir, "foto_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        tempPhotoUri = uri
        cameraLauncher.launch(uri)
    }

    val categorias = listOf("Desarrollo web", "Mantenimiento web", "Incidente técnico", "Hosting", "Soporte de correo", "Marketing digital", "Devolución")
    val prioridades = listOf("Baja", "Media", "Alta", "Urgente")
    val estados = listOf("Recibida", "En revisión", "Pendiente de información", "Asignada", "En proceso", "Resuelta", "Cerrada", "Cancelada")

    var expandedCat by remember { mutableStateOf(false) }
    var expandedPrio by remember { mutableStateOf(false) }
    var expandedEst by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Solicitud #${solicitudExistente?.id}" else "Nueva Solicitud") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (esEdicion) "Modificar Reclamo ($perfil)" else "Nueva Solicitud de Servicio ($perfil)",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título del requerimiento") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción detallada") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Selector Categoría
            ExposedDropdownMenuBox(
                expanded = expandedCat,
                onExpandedChange = { expandedCat = !expandedCat }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCat,
                    onDismissRequest = { expandedCat = false }
                ) {
                    categorias.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                categoria = item
                                expandedCat = false
                            }
                        )
                    }
                }
            }

            // REQUISITO CLAVE: El cliente NO define ni ve la prioridad. Solo Administrador o Técnico la configuran.
            if (perfil != "Cliente") {
                ExposedDropdownMenuBox(
                    expanded = expandedPrio,
                    onExpandedChange = { expandedPrio = !expandedPrio }
                ) {
                    OutlinedTextField(
                        value = prioridad,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Prioridad (Solo Admin / Técnico)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrio) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPrio,
                        onDismissRequest = { expandedPrio = false }
                    ) {
                        prioridades.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    prioridad = item
                                    expandedPrio = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = servicio,
                onValueChange = { servicio = it },
                label = { Text("Modalidad / Servicio (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Selector Estado (Solo Administrador o Técnico pueden cambiar el estado de atención)
            if (esEdicion && perfil != "Cliente") {
                ExposedDropdownMenuBox(
                    expanded = expandedEst,
                    onExpandedChange = { expandedEst = !expandedEst }
                ) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado de la Solicitud") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEst) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEst,
                        onDismissRequest = { expandedEst = false }
                    ) {
                        estados.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    estado = item
                                    expandedEst = false
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = "Fotografías / Evidencias de Respaldo",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { tomarFoto() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Tomar Foto")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tomar Foto")
                }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = "Galería")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Galería")
                }
            }

            if (listaFotos.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(listaFotos) { photoUriStr ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = photoUriStr,
                                contentDescription = "Foto Evidencia",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .clickable {
                                        listaFotos = listaFotos.filter { it != photoUriStr }
                                    },
                                shape = CircleShape,
                                color = Color.Red.copy(alpha = 0.8f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar",
                                    tint = Color.White,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val fechaHoy = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    val cadenaUris = if (listaFotos.isNotEmpty()) listaFotos.joinToString(",") else null

                    if (solicitudExistente != null) {
                        val solicitudActualizada = solicitudExistente.copy(
                            titulo = titulo.ifBlank { "Sin título" },
                            descripcion = descripcion.ifBlank { "Sin descripción" },
                            categoria = categoria,
                            prioridad = if (perfil != "Cliente") prioridad else solicitudExistente.prioridad,
                            servicio = servicio,
                            estado = if (perfil != "Cliente") estado else solicitudExistente.estado,
                            evidenciaUri = cadenaUris,
                            fechaModificacion = fechaHoy
                        )
                        onGuardarClick(solicitudActualizada)
                    } else {
                        val codigo = "SOL-" + System.currentTimeMillis().toString().takeLast(6)
                        val nuevaSolicitud = SolicitudEntity(
                            codigoTracking = codigo,
                            titulo = titulo.ifBlank { "Sin título" },
                            descripcion = descripcion.ifBlank { "Sin descripción" },
                            categoria = categoria,
                            prioridad = "Media", // Por defecto al crear el cliente
                            servicio = servicio,
                            estado = "Recibida", // Al crear el cliente siempre entra como Recibida
                            clienteId = "CLI-001",
                            empresaFicticia = "Pyme Ejemplo Ltda.",
                            evidenciaUri = cadenaUris,
                            fechaCreacion = fechaHoy,
                            fechaModificacion = fechaHoy
                        )
                        onGuardarClick(nuevaSolicitud)
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esEdicion) "Guardar Cambios" else "Enviar Solicitud")
            }
        }
    }
}
