package org.govhack.critrs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.fragment_map.*
import retrofit2.Response
import timber.log.Timber

class MapFragment: Fragment() {
    companion object {
        private val panTime = 2000
        private val nearbyThrottleInterval = 10.seconds.toNanos()
    }
    private var initialLocationSet: Boolean = false
    private var currentLocation: LatLng? = null
    private var nearbyRefreshing: Boolean = false
    private var nextUpdateNearby: Long = 0

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
    }

    override fun onPause() {
        super.onPause()
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
        if (nearbyRefreshing || System.nanoTime() < nextUpdateNearby) {
            return // Throttle server requests
        }
        Timber.d("Requesting animals")
        nearbyRefreshing = true
        currentLocation = location
        api.nearby(location) { success: Boolean, response: Response<NearbyStatus>?, error: Throwable? ->
            if (success) response?.body()?.let {
                // TODO: Display on UI
                Timber.d("%d animals and %d landmarks", it.animals.size, it.landmarks.size)
            }
            else {
                Timber.d(error, "Error")
            }
            nearbyRefreshing = false
            nextUpdateNearby = System.nanoTime() + nearbyThrottleInterval
        }
    }
}