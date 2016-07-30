package org.govhack.critrs

import android.preference.PreferenceManager
import org.govhack.critrs.onboarding.OnboardingActivity

class Application: android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(OnboardingActivity.BOOL_COMPLETE, false)
                    .commit()
        }
    }
}