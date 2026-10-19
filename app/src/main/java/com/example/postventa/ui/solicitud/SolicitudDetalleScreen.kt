package com.example.postventa.ui.solicitud

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.postventa.data.local.SolicitudEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitudId: Int,
    viewModel: SolicitudViewModel,
    onNavigateBack: () -> Unit
) {
    val lista by viewModel.listaSolicitudes.collectAsState()
    val solicitud: SolicitudEntity? = remember(lista, solicitudId) {
        lista.find { it.id == solicitudId }
    }
    val context = LocalContext.current

    // Lista de URIs desempaquetadas desde la base de datos
    val listaEvidencias = remember(solicitud?.evidenciaUri) {
        solicitud?.evidenciaUri?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    var imagenParaFullscreen by remember { mutableStateOf<String?>(null) }
    val formateador = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) }

    // Diálogo ampliado a pantalla completa para la foto seleccionada
    if (imagenParaFullscreen != null) {
        Dialog(
            onDismissRequest = { imagenParaFullscreen = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable { imagenParaFullscreen = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imagenParaFullscreen,
                    contentDescription = "Evidencia Ampliada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Reclamo #${solicitudId}") },
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
        if (solicitud == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontró la información del reclamo.", color = Color.Gray)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta de Identificación y Estado
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Código de Registro: #${solicitud.id}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = solicitud.estado,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                // Información Detallada del Reclamo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Título del Reclamo", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text(solicitud.titulo, style = MaterialTheme.typography.titleLarge)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text("Descripción del Problema", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text(solicitud.descripcion, style = MaterialTheme.typography.bodyLarge)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Categoría", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(solicitud.categoria, style = MaterialTheme.typography.bodyMedium)
                            }
                            Column {
                                Text("Modalidad / Servicio", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(solicitud.servicio, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text("Fecha y Hora de Ingreso", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text(formateador.format(Date(solicitud.fechaHora)), style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Sección de Evidencias Múltiples
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Archivos de Evidencia (${listaEvidencias.size})",
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (listaEvidencias.isEmpty()) {
                            Text(
                                text = "No se adjuntaron fotografías ni evidencias para este reclamo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                text = "Toca cualquier miniatura para verla en pantalla completa o abrirla externamente:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )

                            // Carrusel de todas las evidencias adjuntas
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(listaEvidencias) { uriStr ->
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.LightGray)
                                            .clickable { imagenParaFullscreen = uriStr }
                                    ) {
                                        AsyncImage(
                                            model = uriStr,
                                            contentDescription = "Evidencia",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }

                            // Botón para abrir las evidencias con el visor del sistema operativo
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val primeraUri = Uri.parse(listaEvidencias.first())
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(primeraUri, "image/*")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Abrir evidencia con..."))
                                    } catch (_: Exception) {
                                        imagenParaFullscreen = listaEvidencias.first()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Abrir en visor del sistema")
                            }
                        }
                    }
                }
            }
        }
    }
}