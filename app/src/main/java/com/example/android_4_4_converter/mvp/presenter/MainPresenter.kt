package com.example.android_4_4_converter.mvp.presenter

import com.example.android_4_4_converter.mvp.view.MainView
import com.example.android_4_4_converter.navigation.IScreens
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter


class MainPresenter(val router: Router, val screens: IScreens) : MvpPresenter<MainView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        router.replaceScreen(screens.mainFragment())
    }
    fun backClicked() {
        router.exit()
    }
}