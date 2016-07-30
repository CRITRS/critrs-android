package org.govhack.critrs.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.govhack.critrs.R
import org.govhack.critrs.pushFragment

class LandingFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.fade)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }

    override fun onResume() {
        super.onResume()
        view?.postDelayed(object : Runnable {
            override fun run() {
                activity?.pushFragment(WelcomeFragment())
            }
        }, 2 * resources.getInteger(android.R.integer.config_longAnimTime).toLong())
    }
}
