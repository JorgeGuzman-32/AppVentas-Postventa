package com.example.postventa.ui.solicitud

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream

fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "evidencia_${System.currentTimeMillis()}.jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
    outputStream.flush()
    outputStream.close()
    return Uri.fromFile(file)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudFormScreen(
    viewModel: SolicitudViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var mostrarDialogoOrigen by remember { mutableStateOf(false) }

    LaunchedEffect(state.guardadoExitosamente) {
        if (state.guardadoExitosamente) {
            viewModel.resetGuardado()
            onNavigateBack()
        }
    }

    // 1. Selector de Galería Múltiple (permite seleccionar una o más imágenes a la vez)
    val launcherGaleriaMultiple = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.agregarEvidencias(uris.map { it.toString() })
        }
    }

    // 2. Captura de Cámara (se suma a las ya capturadas)
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val uri = bitmapToUri(context, it)
            viewModel.agregarEvidencias(listOf(uri.toString()))
        }
    }

    if (mostrarDialogoOrigen) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoOrigen = false },
            title = { Text("Adjuntar Evidencia") },
            text = { Text("¿Deseas capturar una foto con la cámara o seleccionar uno o varios archivos desde la galería?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoOrigen = false
                    launcherCamara.launch(null)
                }) {
                    Text("Cámara")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoOrigen = false
                    launcherGaleriaMultiple.launch("image/*")
                }) {
                    Text("Galería")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Reclamo Postventa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Título
            OutlinedTextField(
                value = state.titulo,
                onValueChange = { viewModel.onTituloChanged(it) },
                label = { Text("Título del Reclamo") },
                isError = state.tituloError != null,
                trailingIcon = {
                    if (state.tituloError != null) {
                        Icon(Icons.Default.Info, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                supportingText = {
                    AnimatedVisibility(visible = state.tituloError != null, enter = fadeIn() + expandVertically()) {
                        Text(text = state.tituloError ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Descripción
            OutlinedTextField(
                value = state.descripcion,
                onValueChange = { viewModel.onDescripcionChanged(it) },
                label = { Text("Descripción del Defecto") },
                isError = state.descripcionError != null,
                trailingIcon = {
                    if (state.descripcionError != null) {
                        Icon(Icons.Default.Info, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                supportingText = {
                    AnimatedVisibility(visible = state.descripcionError != null, enter = fadeIn() + expandVertically()) {
                        Text(text = state.descripcionError ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // Categoría
            OutlinedTextField(
                value = state.categoria,
                onValueChange = { viewModel.onCategoriaChanged(it) },
                label = { Text("Categoría (Garantía, Devolución, Cambio)") },
                isError = state.categoriaError != null,
                trailingIcon = {
                    if (state.categoriaError != null) {
                        Icon(Icons.Default.Info, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                supportingText = {
                    AnimatedVisibility(visible = state.categoriaError != null, enter = fadeIn() + expandVertically()) {
                        Text(text = state.categoriaError ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Servicio
            OutlinedTextField(
                value = state.servicio,
                onValueChange = { viewModel.onServicioChanged(it) },
                label = { Text("Servicio (Retiro a domicilio, En tienda)") },
                isError = state.servicioError != null,
                trailingIcon = {
                    if (state.servicioError != null) {
                        Icon(Icons.Default.Info, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                supportingText = {
                    AnimatedVisibility(visible = state.servicioError != null, enter = fadeIn() + expandVertically()) {
                        Text(text = state.servicioError ?: "", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Sección de Evidencias Fotográficas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Evidencias (${state.evidencias.size}):",
                    style = MaterialTheme.typography.titleSmall
                )
                Button(onClick = { mostrarDialogoOrigen = true }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Agregar Evidencia")
                }
            }

            // Carrusel horizontal de miniaturas con botón para quitar cada foto
            if (state.evidencias.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.evidencias) { uri ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Miniatura evidencia",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.removerEvidencia(uri) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.guardarSolicitud(context) },
                enabled = !state.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White)
                } else {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Solicitud")
                }
            }
        }
    }
}