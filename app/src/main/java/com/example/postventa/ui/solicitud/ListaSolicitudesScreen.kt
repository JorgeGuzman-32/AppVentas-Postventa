package com.example.postventa.ui.solicitud

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.postventa.data.local.SolicitudEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaSolicitudesScreen(
    viewModel: SolicitudViewModel,
    onNuevaSolicitudClick: () -> Unit,
    onSolicitudClick: (Int) -> Unit
) {
    val lista: List<SolicitudEntity> by viewModel.listaSolicitudes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reclamos y Postventa") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.resetGuardado()
                onNuevaSolicitudClick()
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Registrar Reclamo")
            }
        }
    ) { padding ->
        if (lista.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No hay solicitudes registradas.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = lista,
                    key = { solicitud: SolicitudEntity -> solicitud.id }
                ) { item: SolicitudEntity ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        SolicitudItemCard(
                            item = item,
                            onClick = { onSolicitudClick(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SolicitudItemCard(
    item: SolicitudEntity,
    onClick: () -> Unit
) {
    val formateador = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val fechaTexto = remember(item.fechaHora) { formateador.format(Date(item.fechaHora)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${item.id} - ${item.titulo}",
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = item.estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Categoría: ${item.categoria}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Modalidad: ${item.servicio}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Fecha: $fechaTexto",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}