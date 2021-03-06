package com.android_academy.covid_19.ui.activity

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.fragment.main.MainFragment
import com.android_academy.covid_19.ui.fragment.main.MainNavigationTarget
import com.android_academy.covid_19.ui.fragment.main.MainNavigationTarget.GoogleLoginView
import com.android_academy.covid_19.ui.fragment.main.MainViewModel
import com.android_academy.covid_19.ui.fragment.main.MainViewModelImpl
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel<MainViewModelImpl>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        initViews(savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        viewModel.apply {
            navigation.observe(this@MainActivity, onNavigationChanged())
        }
    }

    private fun onNavigationChanged(): Observer<in MainNavigationTarget> = Observer {
        when (it) {
            GoogleLoginView -> openLoginWebView()
        }
    }

    /* This is an attempts to use CustomTabs to sign in to Google*/
    private fun openLoginWebView() {
        val builder = CustomTabsIntent.Builder()
        // builder.setSession(session)
        builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
        // Application exit animation, Chrome enter animation.
        builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
        // vice versa
        builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right)

        val pendingIntent =
            PendingIntent.getActivity(this, 1001, Intent(this, MainActivity::class.java), 0)
        builder.setActionButton(
            resources.getDrawable(R.drawable.ic_launcher_foreground).toBitmap(),
            "hmmm",
            pendingIntent
        )

        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(
            this,
            // Uri.parse("https://www.google.com/maps/timeline/kml?authuser=1&pb=!1m8!1m3!1i2020!2i2!3i14!2m3!1i2020!2i2!3i14")
            Uri.parse("https://www.google.com")
        )

        CustomTabsClient.bindCustomTabsService(
            this,
            "com.android.chrome",
            object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(
                    name: ComponentName,
                    client: CustomTabsClient
                ) {
                    Log.d("XXX", "onCustomTabsServiceConnected")
                }

                override fun onServiceDisconnected(p0: ComponentName?) {
                    Log.d("XXX", "onServiceDisconnected")
                }
            })
    }

    private fun initViews(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}
