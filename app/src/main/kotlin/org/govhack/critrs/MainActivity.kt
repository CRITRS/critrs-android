package org.govhack.critrs

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.view_navigation.*
import org.govhack.critrs.onboarding.OnboardingActivity
import org.govhack.critrs.placeholder.EncyclopediaFragment
import org.govhack.critrs.placeholder.StoreFragment

class MainActivity : AppCompatActivity() {

    private var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (!OnboardingActivity.isOnboardingComplete(this)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        fab.setOnClickListener {
            startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
        }

        toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle!!)
        toggle!!.setToolbarNavigationClickListener { onSupportNavigateUp() }
        toggle!!.syncState()

        arrayOf(btn_nav_map, btn_nav_encyclopedia, btn_nav_store, btn_nav_logout).forEach {
            it.setOnClickListener { onNavigationItemSelected(it) }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.content, MapFragment())
                .commit()
        }
        supportFragmentManager.addOnBackStackChangedListener { updateHome() }
        updateHome()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return true
        } else {
            return super.onSupportNavigateUp()
        }
    }

    fun onNavigationItemSelected(item: View): Boolean {
        // Handle navigation view item clicks here.
        when (item.id) {
            R.id.btn_nav_map -> switchFragment(MapFragment::class.java)
            R.id.btn_nav_encyclopedia -> switchFragment(EncyclopediaFragment::class.java)
            R.id.btn_nav_store -> switchFragment(StoreFragment::class.java)
            R.id.btn_nav_logout -> finish()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun <T: Fragment> switchFragment(fragmentClass: Class<T>) {
        pushFragment(Fragment.instantiate(this, fragmentClass.name), false)
    }

    override fun onStart() {
        super.onStart()
    }

    fun updateHome() {
        supportActionBar?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
        val hasBackStack = supportFragmentManager.backStackEntryCount > 0
        drawer_layout.setDrawerLockMode(if (hasBackStack)
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        else
            DrawerLayout.LOCK_MODE_UNLOCKED)

        // Sync up display of icon
        if (hasBackStack) {
            toggle?.setDrawerIndicatorEnabled(false)
            toggle?.syncState()
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        } else {
            toggle?.setDrawerIndicatorEnabled(true)
            toggle?.syncState()
        }

        // Only show the floating action button on the home screen
//        val fabVisibility = if (contentFragment is MapFragment) View.VISIBLE else View.GONE
//        fab.visibility = fabVisibility
    }

    val contentFragment: Fragment?
        get() { return supportFragmentManager.findFragmentById(R.id.content) }
}
