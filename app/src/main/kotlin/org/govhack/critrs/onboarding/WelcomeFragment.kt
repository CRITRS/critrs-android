package org.govhack.critrs.onboarding

import android.os.Bundle
import org.govhack.critrs.R
import org.govhack.critrs.pushFragment


class WelcomeFragment: OnboardingInfoFragment(
        R.string.onboarding_welcome_title,
        R.string.onboarding_welcome_detail,
        R.drawable.ic_mountain) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allowEnterTransitionOverlap = true
    }

    override fun onNextClicked(): Boolean {
        activity.pushFragment(MapsPermissionFragment())
        return true
    }
}