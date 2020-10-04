/*
 * Copyright 2020 Sourav Das
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sourav.bettere.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.sourav.bettere.R
import com.sourav.bettere.db.entity.ChargingLog
import com.sourav.bettere.fragments.FragmentDefault
import com.sourav.bettere.fragments.FragmentGraph
import com.sourav.bettere.fragments.FragmentSettings
import com.sourav.bettere.utils.Constants
import com.sourav.bettere.utils.RoomHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(){
    private lateinit var frag: Fragment
    private var roomHelper: RoomHelper = RoomHelper.getInstance(this)
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
        graphFrag.setOnLongClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                printDatabase()
            }
            true
        }
        initView()
    }

    private fun printDatabase() {
        printLog("PRINTING")
        var list: List<ChargingLog>? = null
        val operation = GlobalScope.async {
            list = roomHelper.getChargingLog()
        }

        if (list!!.isNotEmpty()) {
            for (log: ChargingLog in list!!) {
                val msg: String = "LOG: "
                    .plus(log.timestamp)
                    .plus(" ")
                    .plus(log.cycle)
                    .plus(" ")
                    .plus(log.percentage)
                    .plus(" ")
                    .plus(log.current)
                    .plus(" ")
                    .plus(log.voltage)
                    .plus(" ")
                    .plus(log.temp)

                printLog(msg)
            }
        }
    }

    private fun printLog(msg: String){
        Log.d(Constants.DATABASE, msg)
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