package org.govhack.critrs.onboarding

import org.govhack.critrs.R
import org.govhack.critrs.pushFragment


class MapsFragment: OnboardingInfoFragment(
        R.string.onboarding_maps_title,
        R.string.onboarding_maps_detail, 0) {
    override fun onNextClicked(): Boolean {
        activity.pushFragment(PhotoFragment())
        return true
    }
}