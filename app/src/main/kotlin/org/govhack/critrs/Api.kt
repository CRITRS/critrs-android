package org.govhack.critrs

import com.mapbox.mapboxsdk.geometry.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @GET("/animals")
    fun getAnimals(): Call<List<Animal>>

    @GET("/nearby/{lng}/{lat}")
    fun getNearby(@Path("lng") lng: Double, @Path("lat") lat: Double): Call<NearbyStatus>

    @POST("/overland")
    fun getOverland(@Body request: OverlandRequest): Call<OverlandStatus>

    @POST("/encounter")
    fun getEncounter(@Body request: EncounterRequest): Call<Animal>
}

fun <T> flattenCallback(callback: (Boolean, Response<T>?, Throwable?) -> Unit): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>?) {
            callback(response?.isSuccessful ?: false, response, null)
        }
        override fun onFailure(call: Call<T>?, t: Throwable?) {
            callback(false, null, t)
        }
    }
}

fun Api.animals(callback: (Boolean, Response<List<Animal>>?, Throwable?) -> Unit) {
    getAnimals().enqueue(flattenCallback(callback))
}

fun Api.nearby(location: LatLng, callback: (Boolean, Response<NearbyStatus>?, Throwable?) -> Unit) {
    getNearby(location.longitude, location.latitude).enqueue(flattenCallback(callback))
}

fun Api.getOverland(lng: Double, lat: Double, countsSinceLastEncounter: Int): Call<OverlandStatus> {
    return getOverland(OverlandRequest(lng, lat, countsSinceLastEncounter))
}

fun Api.overland(location: LatLng, countsSinceLastEncounter: Int,
                 callback: (Boolean, Response<OverlandStatus>?, Throwable?) -> Unit) {
    getOverland(location.longitude, location.latitude, countsSinceLastEncounter).enqueue(flattenCallback(callback))
}

fun Api.getEncounter(animal_id: String): Call<Animal> {
    return getEncounter(EncounterRequest(animal_id))
}

fun Api.encounter(animal_id: String, callback: (Boolean, Response<Animal>?, Throwable?) -> Unit) {
    getEncounter(animal_id).enqueue(flattenCallback(callback))
}

val api = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(Api::class.java)
