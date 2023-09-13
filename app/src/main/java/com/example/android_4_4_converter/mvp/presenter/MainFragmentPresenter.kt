package com.example.android_4_4_converter.mvp.presenter

import com.example.android_4_4_converter.mvp.model.FileChooserPresenter
import com.example.android_4_4_converter.mvp.view.UsersView
import com.example.android_4_4_converter.navigation.IScreens
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter


class MainFragmentPresenter(val router: Router, val screens: IScreens, private val view: UsersView) : MvpPresenter<UsersView>(),
    FileChooserPresenter {

    override fun onFileChooserButtonClicked() {
        view.showFileChooser()
    }


    fun backPressed(): Boolean {
        router.exit()
        return true
    }


}

