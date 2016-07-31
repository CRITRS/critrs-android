package org.govhack.critrs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.fragment_map.*
import retrofit2.Response
import timber.log.Timber
import java.util.*
import kotlin.concurrent.timerTask

class MapFragment: Fragment() {
    companion object {
        private val panTime = 2000
        private val nearbyThrottleInterval = 10.seconds.toNanos()
        private val checkEncounterDelay = BuildConfig.CHECK_ENCOUNTER_SECONDS.seconds
    }
    private var initialLocationSet: Boolean = false
    private var currentLocation: LatLng? = null
    private var nearbyRefreshing: Boolean = false
    private var nextUpdateNearby: Long = 0
    private var failedEncounters: Int = 0
    var timer: Timer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(fun(map) {
            map.minZoom = 10.0
            map.maxZoom = 17.0
            map.setOnMyLocationChangeListener { it?.let {
                val location = LatLng(it.latitude, it.longitude)
                if (initialLocationSet) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(location), panTime)
                }
                else {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f), panTime)
                    initialLocationSet = true
                }
                updateNearby(location)
            }}
            map.myLocationViewSettings.accuracyAlpha = 0
            map.myLocationViewSettings.setForegroundDrawable(context.getDrawable(R.drawable.ic_user), null)
            map.isMyLocationEnabled = true
        })
    }

    //region lifecycle boilerplate
    override fun onDestroyView() {
        mapView.onDestroy()
        super.onDestroyView()
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    override fun onResume() {
        super.onResume()
        mapView.onResume()
        activity.setTitle(R.string.title_map)
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            checkEncounter()
        }, 0, checkEncounterDelay)
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
    //endregion

    fun updateNearby(location: LatLng) {
        if (nearbyRefreshing || System.nanoTime() < nextUpdateNearby ||
            location.equals(currentLocation)) {
            return // Throttle server requests
        }
        Timber.d("Requesting animals")
        nearbyRefreshing = true
        currentLocation = location
        api.nearby(location) { success: Boolean, response: Response<NearbyStatus>?, error: Throwable? ->
            if (success) response?.body()?.let {
                // TODO: Display on UI
                Timber.d("${it.animals.size} animals and ${it.landmarks.size} landmarks")
            }
            else {
                Timber.d(error, "Error")
            }
            nearbyRefreshing = false
            nextUpdateNearby = System.nanoTime() + nearbyThrottleInterval
        }
    }

    fun checkEncounter() {
        Timber.d("Checking for encounter at ${currentLocation}")
        currentLocation?.let {
            api.overland(it, failedEncounters) { success: Boolean, response: Response<OverlandStatus>?, error: Throwable? ->
                if (success) response?.body()?.let {
                    if (it.encounter && it.animal != null) {
                        // TODO: Go to encounter screen
                        val message = getString(R.string.placeholder_encounter_toast, it.animal.display_name.toUpperCase())
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        failedEncounters = 0
                    }
                    else {
                        failedEncounters++
                        Timber.d("Got nothing ${failedEncounters} time/s")
                    }
                }
            }
        }
    }
}