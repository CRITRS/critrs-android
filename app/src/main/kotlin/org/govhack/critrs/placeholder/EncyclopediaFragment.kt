package org.govhack.critrs.placeholder

import android.os.Bundle
import android.view.View
import org.govhack.critrs.R
import org.govhack.critrs.pushFragment

class EncyclopediaFragment: PlaceholderFragment(R.string.title_encyclopedia, R.drawable.placeholder_encyclopedia) {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.setOnClickListener {
            activity.pushFragment(EncyclopediaEntryFragment())
        }
    }
}