package com.example.regresoacasa.data

interface AppContainer {
    val routeRepository: RouteRepository
}

class DefaultAppContainer() : AppContainer {
    private val baseUrl = "https://api.openrouteservice.org/"

    private val token = "5b3ce3597851110001cf624873ae0e2e3b184e4e96011b2ef3be6da5"

    override val routeRepository: RouteRepository by lazy {
        NetworkRouteRepository(baseUrl, token)
    }

}
