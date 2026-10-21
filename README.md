# App de Gestión de Postventa y Reclamos (DSY1105)

Aplicación móvil desarrollada en Android Studio con Jetpack Compose, arquitectura MVVM y persistencia local mediante Room Database para la Evaluación Parcial 2.

## 👥 Integrantes del Equipo
* **Estudiante 1:** Jorge Guzmán - GitHub: `JorgeGuzman-32`
* **Estudiante 2:** Francisco Órdenes - GitHub: `Cisco-fbn`

---

## 📋 Problemática Resuelta
Optimización de la captura, trazabilidad y almacenamiento local offline de solicitudes de postventa, garantías y reclamos para retail.

---

## 🛠️ Características Técnicas Implementadas

* **Interfaz y Navegación (IE 2.1.1):** Flujo con `NavHost` (`LoginScreen` ➔ `MenuScreen` ➔ `ListaSolicitudesScreen` ➔ `SolicitudFormScreen` ➔ `SolicitudDetalleScreen`) bajo Material Design 3.
* **Formularios Reactivos (IE 2.1.2):** Validación en campos y feedback visual con `isError`.
* **Arquitectura MVVM (IE 2.2.1):** Lógica en `SolicitudViewModel.kt` y estado inmutable con `StateFlow`.
* **Animaciones (IE 2.2.2):** Transiciones y visibilidad animada en listas y errores.
* **Persistencia Local (IE 2.3.1):** Base de datos SQLite con Room y manejo de evidencias múltiples.
* **Recursos Nativos (IE 2.4.1):** Integración de cámara/galería y vibración háptica.

---

## 📊 Planificación y Colaboración (IE 2.3.2)
* **Tablero Kanban:** [Enlace a Trello](https://trello.com)
* **Control de Versiones:** Historial de commits técnicos distribuidos.

---

## 🚀 Instrucciones Rápidas
1. Clonar: `git clone https://github.com`
2. Abrir en Android Studio, sincronizar Gradle y ejecutar en dispositivo o emulador API 26+.
