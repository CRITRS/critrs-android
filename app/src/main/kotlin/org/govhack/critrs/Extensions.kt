package org.govhack.critrs

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

fun FragmentActivity.pushFragment(fragment: Fragment, allowBack: Boolean = true) {
    val transaction = supportFragmentManager.beginTransaction().replace(R.id.content, fragment)
    if (allowBack) {
        transaction.addToBackStack(null)
    }
    transaction.commit()
}