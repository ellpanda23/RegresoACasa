package com.example.regresoacasa.ui

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// VISTA PARA SOLICITAR LOS PERMISOS FALTANTES
@SuppressLint("MissingPermission")
@Composable
fun CurrentLocationScreen(regresoACasaViewModel: RegresoACasaViewModel) {
    val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    PermissionBox(
        permissions = permissions,
        requiredPermissions = listOf(permissions.first()),
        onGranted = {
            CurrentLocationContent(
                it.contains(Manifest.permission.ACCESS_FINE_LOCATION),
                regresoACasaViewModel
            )
        },
    )
}


// VISTA DONDE SE MUESTRA EL MAPA, SE DIBUJA LA POLI LINEA
@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun CurrentLocationContent(usePreciseLocation: Boolean, regresoACasaViewModel: RegresoACasaViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ESTA VARIABLE DE TIPO LOCATION CLIENT NOS AYUDA A OBTENER LA UBICACION DEL DISPOSITIVO EN TIEMPO REAL
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.12814415504907, -101.18454727494472), 14f)
    }

    // ESTA ES UNA VARIABLE DE TIPO MARCADOR QUE NOS AYUDA A MOSTRAR LA UBICACION ACTUAL
    val markerState = rememberMarkerState(position = regresoACasaViewModel.ubication.value)

    // ESTA ES LA FUNCION QUE NOS AYUDA A TRAER LA UBICACION ACTUAL
    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            val priority = if (usePreciseLocation) {
                Priority.PRIORITY_HIGH_ACCURACY
            } else {
                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }
            // ESTA VARIABLE ALMACENA LA UBICACION ACTUAL QUE SE TRAE DE LOCATIONCLIENT CON LA FUNCION GETCURRENTLOCATION()
            val result = locationClient.getCurrentLocation(
                priority,
                CancellationTokenSource().token,
            ).await()
            // SI NOS REGGRESA LA UBICACION ACTUAL LA MAPEA Y CREA UN MARCADOR CON LA UBICACION OBTENIDA
            result?.let { fetchedLocation ->
                val lanLong = LatLng(fetchedLocation.latitude,fetchedLocation.longitude)
                regresoACasaViewModel.updateUbication(lanLong)
                markerState.position = lanLong
            }
            regresoACasaViewModel.getListCoordenates()
        }

    }


    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxSize()){
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            ) {
                AdvancedMarker(
                    // ESTE ES LE MARCADOR QUE NOS MUESTRA DONDE ES LA CASA
                    state = MarkerState(position = regresoACasaViewModel.home.value),
                    title = "CASA",
                )
                AdvancedMarker(
                    // ESTE ES EL MARCADOR QUE NO DICE LA UBICACION DEL DISPOSITIVO
                    state = markerState,
                    title = "UBICACION DEL DISPOSITIVO",
                )
                // ESTA ES LA POLI LINEA QUE HACE ENTRE AMBAS UBICACIONES UNA VEZ QUE YA SE HIZO LA
                // PETICION A OPEN ROUTE SERVICE Y SE DIBUJA EN EL MAPA
                Polyline(points = regresoACasaViewModel.RouteList.value , color= Color.Red)
            }
        }
    }
}