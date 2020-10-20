/*
 * Copyright 2020 Sourav Das
 */

package com.sourav.bettere.activities

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sourav.bettere.R
import com.sourav.bettere.fragments.FragmentDefault
import com.sourav.bettere.fragments.FragmentGraph
import com.sourav.bettere.fragments.FragmentSettings
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){
    private lateinit var frag: Fragment
    private var orientation: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FragmentDefault.newInstance())
        adapter.addFragment(FragmentGraph.newInstance())
        adapter.addFragment(FragmentSettings.newInstance())
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> enableNavIcon(defaultFrag, titleHome)
                    1 -> enableNavIcon(graphFrag, titleHistory)
                    2 -> enableNavIcon(settingsFrag, titleSettings)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        initView()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val myIntent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            myIntent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            myIntent.data = Uri.parse(
                "package:" +
                        packageName
            )
        }
        startActivity(myIntent)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientation = newConfig.orientation
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> removeTitles()
        }
    }

    private fun initView() {
        frag = FragmentDefault.newInstance()
        enableNavIcon(defaultFrag, titleHome)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.layoutMainBG);
    }

    fun switchFragments(view: View) {
        when (view.id) {
            R.id.homeParent -> {
                viewPager.currentItem = 0
                enableNavIcon(defaultFrag, titleHome)
            }
            R.id.historyParent -> {
                viewPager.currentItem = 1
                enableNavIcon(graphFrag, titleHistory)
            }
            R.id.settingsParent -> {
                viewPager.currentItem = 2
                enableNavIcon(settingsFrag, titleSettings)
            }
            else -> FragmentDefault.newInstance()
        }
    }

    private fun enableNavIcon(imageView: ImageView, textView: TextView) {
        defaultFrag.colorFilter = null
        graphFrag.colorFilter = null
        settingsFrag.colorFilter = null
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent))

        removeTitles()
        textView.visibility = View.VISIBLE
    }

    private fun removeTitles() {
        titleHome.visibility = View.GONE
        titleHistory.visibility = View.GONE
        titleSettings.visibility = View.GONE
    }

    class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(
        manager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        private val fragmentlist: MutableList<Fragment> = ArrayList()

        override fun getCount(): Int {
            return fragmentlist.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentlist[position]
        }

        fun addFragment(fragment: Fragment) {
            fragmentlist.add(fragment)
        }


    }
}