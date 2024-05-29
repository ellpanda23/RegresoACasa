package com.example.regresoacasa.model

data class Route(
    val features: List<RouteFeature>
)

data class RouteFeature(
    val geometry: RouteGeometry
)

data class RouteGeometry(
    val coordinates: List<List<Double>>,
    val type: String
)