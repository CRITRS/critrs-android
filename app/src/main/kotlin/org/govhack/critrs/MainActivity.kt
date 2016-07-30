package org.govhack.critrs

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.view_navigation.*
import org.govhack.critrs.onboarding.OnboardingActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show() }

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        arrayOf(btn_nav_map, btn_nav_encyclopedia, btn_nav_store, btn_nav_logout).forEach {
            it.setOnClickListener { onNavigationItemSelected(it) }
        }

        if (!OnboardingActivity.isOnboardingComplete(this)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }
        else if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.content, MapFragment())
                .commit()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun onNavigationItemSelected(item: View): Boolean {
        // Handle navigation view item clicks here.
        when (item.id) {
            R.id.btn_nav_map -> switchFragment(MapFragment::class.java)
            R.id.btn_nav_encyclopedia -> switchFragment(Fragment::class.java)
            R.id.btn_nav_store -> switchFragment(Fragment::class.java)
            R.id.btn_nav_logout -> finish()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun <T: Fragment> switchFragment(fragmentClass: Class<T>) {
        // TODO
//        Fragment.instantiate(this, fragmentClass.name)
    }
}
