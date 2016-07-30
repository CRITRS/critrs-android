package org.govhack.critrs.onboarding

import android.Manifest
import org.govhack.critrs.R
import org.govhack.critrs.pushFragment
import org.govhack.critrs.requestPermission


class MapsPermissionFragment : OnboardingInfoFragment(
        R.string.onboarding_maps_title,
        R.string.onboarding_maps_detail,
        R.drawable.ic_owl) {
    override fun onNextClicked(): Boolean {
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
            if (it) {
                activity?.pushFragment(PhotoPermissionFragment())
            }
        }
        return true
    }
}