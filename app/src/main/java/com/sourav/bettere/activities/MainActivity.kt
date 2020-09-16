package com.sourav.bettere.activities

import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

        initView()
        loadFragment()
    }

    private fun loadFragment() {
        val fragMan = supportFragmentManager
        fragMan.beginTransaction().add(R.id.fragment,frag,"Default").commit()
    }

    private fun initView() {
        frag = FragmentDefault.newInstance()
        setBottomBarColor(defaultFrag)
    }

    fun switchFragments(view: View) {
        when (view.id){
            R.id.defaultFrag -> {
                frag = FragmentDefault.newInstance()
                setBottomBarColor(defaultFrag)
            }
            R.id.graphFrag -> {
                frag = FragmentGraph.newInstance()
                setBottomBarColor(graphFrag)
            }
            R.id.settingsFrag -> {
                frag = FragmentSettings.newInstance()
                setBottomBarColor(settingsFrag)
            }
            else -> FragmentDefault.newInstance()
        }
        loadFragment()
    }

    private fun setBottomBarColor(imageView: ImageView) {
        defaultFrag.colorFilter = null
        graphFrag.colorFilter = null
        settingsFrag.colorFilter = null
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent))
    }


}