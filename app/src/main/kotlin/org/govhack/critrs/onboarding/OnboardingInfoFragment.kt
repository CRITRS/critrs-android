package org.govhack.critrs.onboarding

import android.graphics.Typeface
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.*
import kotlinx.android.synthetic.main.fragment_onboarding.*
import org.govhack.critrs.R

abstract class OnboardingInfoFragment(
        @StringRes val titleRes: Int,
        @StringRes val detailRes: Int,
        @DrawableRes val iconRes: Int) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.slide_right)
        exitTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.slide_left)
        allowEnterTransitionOverlap = false
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.setText(titleRes)
        title.typeface = Typeface.createFromAsset(resources.assets, "fonts/Montserrat-Bold.otf")
        detail.setText(detailRes)
        image.setImageResource(iconRes)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.next, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_next -> onNextClicked()
            else -> super.onOptionsItemSelected(item)
        }
    }

    open fun onNextClicked(): Boolean {
        return false
    }
}