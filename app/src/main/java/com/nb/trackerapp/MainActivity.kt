package com.nb.trackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ProcessLifecycleOwner
import com.nb.trackerapp.base.AppIntents
import com.nb.trackerapp.base.AppLifeCycleObserver
import com.nb.trackerapp.views.fragments.LocationFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // attaching fragment
        AppIntents.addFragment(R.id.main_frame,LocationFragment(),supportFragmentManager)

        // initializing app state
        val appLifeCycleObserver = AppLifeCycleObserver()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifeCycleObserver)
    }
}