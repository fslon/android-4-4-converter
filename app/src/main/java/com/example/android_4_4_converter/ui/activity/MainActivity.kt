package com.example.android_4_4_converter.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android_4_4_converter.App
import com.example.android_4_4_converter.R
import com.example.android_4_4_converter.databinding.ActivityMainBinding
import com.example.android_4_4_converter.mvp.presenter.MainPresenter
import com.example.android_4_4_converter.mvp.view.MainView
import com.example.android_4_4_converter.navigation.AndroidScreens
import com.github.terrakok.cicerone.androidx.AppNavigator
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class MainActivity : MvpAppCompatActivity(), MainView {
    private lateinit var binding: ActivityMainBinding

    val navigator = AppNavigator(this, R.id.container)
    private val presenter by moxyPresenter {
        MainPresenter(App.instance.router, AndroidScreens())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        App.instance.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        App.instance.navigatorHolder.removeNavigator()
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            if (it is BackButtonListener && it.backPressed()) {
                return
            }
        }
        presenter.backClicked()
    }
}