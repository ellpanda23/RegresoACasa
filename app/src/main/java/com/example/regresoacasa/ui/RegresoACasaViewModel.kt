package com.example.regresoacasa.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.regresoacasa.data.RouteRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegresoACasaViewModel(private val routeRepository: RouteRepository) : ViewModel() {
    // DEFINE LAS COORDENADAS DE LA CASA
    val home = mutableStateOf(LatLng(20.10473, -101.17525))
    // DEFINE LAS COORDENADAS DE LA UBICACION ACTUAL (ESTA SE MODIFICA MAS ADELANTE)
    val ubication = mutableStateOf(LatLng(20.14047040422464, -101.15062440360745))
    // CREA UNA LISTA VACIA DONDE SE VAN A ALMACENAR LAS COORDENADAS DE LA POLI LINEA
    val RouteList = mutableStateOf<List<LatLng>>(emptyList())

    // FUNCION PARA ACTUALIZAR LA CASA
    fun updateHome(newLatLng: LatLng) {
        home.value = newLatLng
    }

    // FUNCION PARA ACTUALIZAR LA UBICACION ACTUAL (SE ACTUALIZA DESDE EL COMPOSABLE)
    fun updateUbication(newLatLng: LatLng) {
        ubication.value = newLatLng
    }

    // PETICION A OPEN ROUTE SERVICE PARA REGRESAR LAS COORDENADAS ENTRE AMBAS UBICACIONES (LA ACTUAL Y LA CASA)
    suspend fun getListCoordenates() {
        return withContext(Dispatchers.IO) {
            val start = ubication.value.longitude.toString()+","+ubication.value.latitude.toString()
            val end = home.value.longitude.toString()+","+home.value.latitude.toString()
            val coordinatesList = routeRepository.getRouteResponse(start, end).features
                .filter { it.geometry.type == "LineString" }
                .flatMap { it.geometry.coordinates }
            // Convertir la lista de coordenadas en una lista de LatLng
            RouteList.value = coordinatesList.map { LatLng(it[1], it[0]) }
        }
    }

}


// ESTA CLASE NOS AYUDA A QUE CUANDO SE CREE LA APLICACION LE MANDE POR DEFECTO EL VIEWMODEL A LA VISTA
class RegresoACasaViewModelFactory(
    private val routeRepository: RouteRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegresoACasaViewModel(routeRepository) as T
    }
}