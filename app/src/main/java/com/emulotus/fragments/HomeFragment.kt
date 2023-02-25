package com.emulotus.fragments

import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.emulotus.R
import com.emulotus.RuntimeProperties
import com.emulotus.layers.EmuLayer
import com.google.android.material.appbar.MaterialToolbar

class HomeFragment : Fragment() {

    private val viewerListIcon = R.integer.viewer_mode_list
    private val viewerGridIcon = R.integer.viewer_mode_grid

    private val defViewerIcon = viewerGridIcon

    private lateinit var gridViewIcon: Drawable
    private lateinit var listViewIcon: Drawable
    private lateinit var sharedViewerMode: String
    private lateinit var navTopBar: MaterialToolbar
    private lateinit var topMenuViewer: MenuItem

    private val userPreferences: SharedPreferences? = RuntimeProperties.appPreferences()

    override fun onStart() {
        super.onStart()
        navTopBar = view?.findViewById(R.id.topAppBar)!!
        // Making the X button put the application on the background

        sharedViewerMode = getString(R.string.key_viewer_mode)

    }

    override fun onResume() {
        super.onResume()

        val grid = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_grid,
            RuntimeProperties.appTheme()!!
        )
        grid?.let { gridViewIcon = it }

        val list = ResourcesCompat.getDrawable(
            resources, R.drawable.ic_list,
            RuntimeProperties.appTheme()!!
        )
        list?.let { listViewIcon = it }

        topMenuViewer = navTopBar.menu.findItem(R.id.top_viewer)!!
        loadLastViewerMode(topMenuViewer)
        setupToolBar(navTopBar)
        updateToolBar(navTopBar)
    }

    // Changing the viewer icon value inside our preferences
    private fun changeDspViewer(viewerId: Int) {
        val userPreferences = RuntimeProperties.appPreferences()!!
        with (userPreferences.edit()) {
            putInt(sharedViewerMode, viewerId)
            apply()
        }
    }

    // Applying our display viewer icon at runtime (doo)
    private fun viewerApplyIcon(listViewer: MenuItem?) {
        when (userPreferences?.getInt(sharedViewerMode, -1)) {
            viewerGridIcon -> listViewer?.icon = gridViewIcon
            viewerListIcon -> listViewer?.icon = listViewIcon
            else -> return
        }
    }

    private fun swapDspViewer(itemView: MenuItem?) {
        /* Initializing the display viewer icon whether doesn't exist in our program preferences
        /  - Swapping between tho display modes [Grid, List] - */
        val actualDspViewer = userPreferences.let {
            it?.getInt(sharedViewerMode, defViewerIcon)
        }
        val choDspViewer = if (actualDspViewer == viewerGridIcon) {
            viewerListIcon
        } else {
            viewerGridIcon
        }

        changeDspViewer(choDspViewer)
        viewerApplyIcon(itemView)
    }

    private fun loadLastViewerMode(viewerMode: MenuItem) {
        // We don't have one yet!
        userPreferences?.let {
            if (!it.contains(sharedViewerMode)) return
        }

        viewerApplyIcon(viewerMode)
    }

    private fun setupToolBar(navTop: MaterialToolbar) {
        navTop.setOnMenuItemClickListener { selected ->
            var flowRet = true
            when (selected.itemId) {
                R.id.top_viewer -> swapDspViewer(selected)
                R.id.top_theme -> RuntimeProperties.switchAppTheme()
                else -> flowRet = false
            }
            flowRet
        }
    }

    private fun updateToolBar(nap: MaterialToolbar) {
        nap.subtitle = EmuLayer.getRecentlyStatus()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}