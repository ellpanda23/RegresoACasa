package com.example.regresoacasa.data

import android.util.Log
import com.example.regresoacasa.model.Route
import com.example.regresoacasa.model.RouteFeature
import com.example.regresoacasa.model.RouteGeometry
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

interface RouteRepository {
    suspend fun getRouteResponse(Start:String, end: String): Route
}

class NetworkRouteRepository(private val baseUrl : String, private val token : String) : RouteRepository {
    override suspend fun getRouteResponse(start: String, end: String): Route {
        try {
            Log.e("valores", start+"|"+end)
            val apiUrl=baseUrl+"v2/directions/driving-car?api_key="+token+"&start="+start+"&end="+end
            val connection = URL(apiUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8");
            val responseCode = connection.responseCode
            Log.e("Respuesta", responseCode.toString())
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                var inputLine: String?
                val response = StringBuilder()
                while (reader.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                reader.close()
                connection.disconnect()
                val route = Gson().fromJson(response.toString(), Route::class.java)
                return route
            } else {
                connection.disconnect()
                return Route(listOf(RouteFeature(RouteGeometry(emptyList(),""))))
            }
        }catch (e: IOException){
            return Route(listOf(RouteFeature(RouteGeometry(emptyList(),""))))
        }
    }
}