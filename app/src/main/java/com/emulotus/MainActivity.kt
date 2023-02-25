package com.emulotus

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat

import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.emulotus.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar

import com.google.android.material.color.DynamicColors
import com.google.android.material.navigation.NavigationView
import java.lang.System.loadLibrary

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navBarActor: ActionBarDrawerToggle
    private lateinit var navCtrl: NavController
    private lateinit var userPreferences: SharedPreferences
    private var onHomeFlag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Applying whether available, dynamic colors theming to our application
        DynamicColors.applyToActivityIfAvailable(this)
        RuntimeProperties.appThemeSwitcher = { switchShortcutTheme() }
        RuntimeProperties.appThemeFetcher = { theme }

        // Android personal preferences manager only allow only one instance of the SharedPreferences
        // object exist at time on our system!
        userPreferences = getPreferences(Context.MODE_PRIVATE)
        RuntimeProperties.appPreferences = { userPreferences }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCurrentTheme()
    }

    override fun onStart() {
        super.onStart()

        navCtrl = findNavController(R.id.nav_host)
        val navBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        navBar.let {
            it?.setNavigationOnClickListener {
                moveTaskToBack(true)
            }
        }

        val drawerPaint = findViewById<DrawerLayout>(R.id.main_drawer)
        val drawerViewer = findViewById<NavigationView>(R.id.drawer_view)
        val navBottomBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        navBarActor = ActionBarDrawerToggle(
            this, drawerPaint, navBottomBar,
            R.string.open_desc, R.string.close_desc
        )

        drawerPaint.apply { addDrawerListener(navBarActor) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navBarActor.syncState()

        configureWindows()
        installControllers(drawerViewer, drawerPaint)
        installUserHandlers(navCtrl)
    }

    private fun setupCurrentTheme() {
        val sharedThemeMode = getString(R.string.key_theme_mode)
        userPreferences.let {
            if (it.contains(sharedThemeMode)) return
        }

    }

    private fun configureWindows() {
        window.let {
            window.insetsController?.let {
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                it.hide(WindowInsets.Type.navigationBars())
            }
            val nav = findViewById<MaterialToolbar>(R.id.topAppBar)
            nav?.let {
                window.statusBarColor = it.solidColor
            }
        }
    }

    private fun installControllers(
        navDrawer: NavigationView,
        navRender: DrawerLayout
    ) {

        navDrawer.apply {
            val fragmentPossibles = mapOf(
                R.id.drawer_home to R.id.nav_app_home,
                R.id.drawer_settings to R.id.nav_app_settings,
                R.id.drawer_gallery to R.id.nav_user_gallery,
                R.id.drawer_statistics to R.id.nav_user_statistics,
                R.id.drawer_log to R.id.nav_user_log
            )

            setNavigationItemSelectedListener { option ->
                var shouldChangeAct = true
                when (option.itemId) {
                    R.id.drawer_exit -> finish()
                    R.id.drawer_home -> {
                        if (onHomeFlag)
                            shouldChangeAct = false
                        onHomeFlag = true
                    }
                    else -> onHomeFlag = false
                }
                if (shouldChangeAct) {
                    navCtrl.navigate(fragmentPossibles[option.itemId]!!)
                }
                navRender.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private fun installUserHandlers(navControl: NavController) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (onHomeFlag) finish()
                else onHomeFlag = true
                navControl.popBackStack(R.id.nav_app_home, false)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (navBarActor.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun switchShortcutTheme(): Boolean {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES ->
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            Configuration.UI_MODE_NIGHT_NO -> {
                delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            }
            else -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }
        return true
    }

    companion object {
        init {
            loadLibrary("emucore")
        }
    }
}