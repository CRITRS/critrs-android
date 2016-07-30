package org.govhack.critrs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment: Fragment() {
    var initialLocationSet: Boolean = false

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
                if (initialLocationSet) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
                }
                else {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15.0f))
                    initialLocationSet = true
                }
            }}
            map.isMyLocationEnabled = true
        })
    }

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
}