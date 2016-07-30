package org.govhack.critrs.onboarding

import org.govhack.critrs.R


class PhotoFragment: OnboardingInfoFragment(
        R.string.onboarding_photo_title,
        R.string.onboarding_photo_detail, 0) {
    override fun onNextClicked(): Boolean {
        (activity as? OnboardingActivity)?.done()
        return true;
    }
}