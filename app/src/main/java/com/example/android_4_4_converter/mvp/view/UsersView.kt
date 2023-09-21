package com.example.android_4_4_converter.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class) // presenter -> fragment
interface UsersView : MvpView {
    fun showFileChooser()
    fun showToast(text: String)

}