package org.govhack.critrs.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.govhack.critrs.MainActivity
import org.govhack.critrs.R

class OnboardingActivity : AppCompatActivity() {
    companion object {
        internal val BOOL_COMPLETE = "onboarding_complete"
        fun isOnboardingComplete(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(BOOL_COMPLETE, false)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.content, LandingFragment())
                .commit()
        }
    }

    fun done() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean(BOOL_COMPLETE, true)
                .commit()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
