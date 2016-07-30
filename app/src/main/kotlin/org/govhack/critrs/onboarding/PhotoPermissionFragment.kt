package org.govhack.critrs.onboarding

import android.Manifest
import org.govhack.critrs.R
import org.govhack.critrs.requestPermission


class PhotoPermissionFragment : OnboardingInfoFragment(
        R.string.onboarding_photo_title,
        R.string.onboarding_photo_detail, 0) {
    override fun onNextClicked(): Boolean {
        requestPermission(Manifest.permission.CAMERA) {
            if (it) {
                (activity as? OnboardingActivity)?.done()
            }
        }
        return true;
    }
}