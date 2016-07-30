package org.govhack.critrs

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

fun FragmentActivity.pushFragment(fragment: Fragment, allowBack: Boolean = true) {
    val transaction = supportFragmentManager.beginTransaction().replace(R.id.content, fragment)
    if (allowBack) {
        transaction.addToBackStack(null)
    }
    transaction.commitAllowingStateLoss()
}

fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
    Dexter.checkPermission(object : PermissionListener {
        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
            callback(true)
        }

        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
            token?.continuePermissionRequest()
        }

        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
            callback(false)
        }

    }, permission)
}