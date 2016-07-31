package org.govhack.critrs

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.Toast
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.fragment_map.*
import org.govhack.critrs.ar.UnityPlayerNativeActivity
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
    private var failedEncounters: Int = if (BuildConfig.DEBUG) 100 else 0
    private var checkingEncounter: Boolean = false
    var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.alpha = 0.01f
        mapView.getMapAsync(fun(map) {
            map.minZoom = 16.0
            map.maxZoom = 18.0
            map.setOnMyLocationChangeListener { it?.let {
                val location = LatLng(it.latitude, it.longitude)
                if (initialLocationSet) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(location), panTime)
                }
                else {
                    val target = CameraPosition.Builder()
                        .target(location)
                        .tilt(45.0)
                        .zoom(map.minZoom)
                        .build()
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(target))
                    mapView.alpha = 1.0f
                    initialLocationSet = true
                }
                updateNearby(location)
            }}
            map.myLocationViewSettings.accuracyAlpha = 0
            map.myLocationViewSettings.setForegroundDrawable(context.getDrawable(R.drawable.ic_marker), null)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.map, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.action_berry -> {
                Toast.makeText(context, R.string.berry, Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

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
        val location = currentLocation
        if (checkingEncounter || location == null) { return }
        Timber.d("Checking for encounter at ${location}")
        checkingEncounter = true
        api.overland(location, failedEncounters) { success: Boolean, response: Response<OverlandStatus>?, error: Throwable? ->
            if (success) response?.body()?.let {
                if (it.encounter && it.animal != null) {
                    failedEncounters = 0
                    goToEncounter(it.animal)
                }
                else {
                    checkingEncounter = false;
                    failedEncounters++
                    Timber.d("Got nothing ${failedEncounters} time/s")
                }
            }
        }
    }

    fun goToEncounter(animal: Animal) {
        val message = getString(R.string.encounter_message, animal.category)
        animal.category
        AlertDialog.Builder(context)
                .setTitle(animal.display_name)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.encounter_confirm) { dialog: DialogInterface, i: Int ->
                    // TODO: Pass animal image url in intent data
                    startActivity(Intent(context, UnityPlayerNativeActivity::class.java)
                            .setData(Uri.parse("http://mens.ly/files/koala.jpg")))
                }
                .setOnDismissListener {
                    checkingEncounter = false
                }
                .show()
    }
}