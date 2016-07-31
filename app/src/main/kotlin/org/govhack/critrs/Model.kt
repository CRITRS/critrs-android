package org.govhack.critrs

class Animal(
        val id: String,
        val display_name: String,
        val scientific_name: String,
        val status: String,
        val category: String,
        val media_url: String,
        val image_url: String?,
        val bio: String)

class Landmark(
        val id: String,
        val name: String,
        val description: String,
        val media_url: String,
        val image_name: String,
        val latitude: String,
        val longitude: String,
        val category: String)


class OverlandRequest(
        val lng: Double,
        val lat: Double,
        val countsSinceLastEncounter: Int)

class EncounterRequest(val animal_id: String)

class NearbyStatus(
        val animals: List<Animal>,
        val landmarks: List<Landmark>)

class OverlandStatus(
        val encounter: Boolean,
        val inPark: Boolean,
        val animal: Animal?)