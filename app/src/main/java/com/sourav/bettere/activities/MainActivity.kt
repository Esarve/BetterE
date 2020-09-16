package com.sourav.bettere.activities

import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sourav.bettere.R
import com.sourav.bettere.broadcasts.BatteryBroadcast
import com.sourav.bettere.fragments.FragmentDefault
import com.sourav.bettere.fragments.FragmentGraph
import com.sourav.bettere.fragments.FragmentSettings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity(){
    private lateinit var frag: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FragmentDefault.newInstance())
        adapter.addFragment(FragmentGraph.newInstance())
        adapter.addFragment(FragmentSettings.newInstance())
        viewPager.adapter = adapter

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> setBottomBarColor(defaultFrag)
                    1 -> setBottomBarColor(graphFrag)
                    2 -> setBottomBarColor(settingsFrag)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        initView()
    }

    private fun initView() {
        frag = FragmentDefault.newInstance()
        setBottomBarColor(defaultFrag)
    }

    fun switchFragments(view: View) {
        when (view.id){
            R.id.defaultFrag -> {
                viewPager.currentItem = 0
                setBottomBarColor(defaultFrag)
            }
            R.id.graphFrag -> {
                viewPager.currentItem = 1
                setBottomBarColor(graphFrag)
            }
            R.id.settingsFrag -> {
                viewPager.currentItem = 2
                setBottomBarColor(settingsFrag)
            }
            else -> FragmentDefault.newInstance()
        }
    }

    private fun setBottomBarColor(imageView: ImageView) {
        defaultFrag.colorFilter = null
        graphFrag.colorFilter = null
        settingsFrag.colorFilter = null
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent))
    }

    class ViewPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragmentlist: MutableList<Fragment> = ArrayList()

        override fun getCount(): Int {
            return fragmentlist.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentlist[position]
        }

        fun addFragment(fragment: Fragment){
            fragmentlist.add(fragment)
        }


    }
}