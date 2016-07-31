package org.govhack.critrs

import android.preference.PreferenceManager
import com.karumi.dexter.Dexter
import com.mapbox.mapboxsdk.MapboxAccountManager
import org.govhack.critrs.onboarding.OnboardingActivity
import timber.log.Timber

class Application: android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.ALWAYS_SHOW_ONBOARD) {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(OnboardingActivity.BOOL_COMPLETE, false)
                    .commit()
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber logging enabled")
        }
        MapboxAccountManager.start(this, BuildConfig.MAPBOX_TOKEN)
        Dexter.initialize(this)
    }
}