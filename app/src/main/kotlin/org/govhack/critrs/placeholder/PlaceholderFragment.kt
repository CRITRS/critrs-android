package org.govhack.critrs.placeholder

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_placeholder.*
import org.govhack.critrs.R

abstract class PlaceholderFragment(@StringRes val titleRes: Int, @DrawableRes val imageRes: Int): Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_placeholder, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image.setImageResource(imageRes)
    }

    override fun onResume() {
        super.onResume()
        activity.setTitle(titleRes)
    }
}