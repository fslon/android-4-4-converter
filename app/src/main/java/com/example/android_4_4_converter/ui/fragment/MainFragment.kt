package com.example.android_4_4_converter.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.android_4_4_converter.App
import com.example.android_4_4_converter.databinding.FragmentMainBinding
import com.example.android_4_4_converter.mvp.presenter.MainFragmentPresenter
import com.example.android_4_4_converter.mvp.view.UsersView
import com.example.android_4_4_converter.navigation.AndroidScreens
import com.example.android_4_4_converter.ui.activity.BackButtonListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class MainFragment : MvpAppCompatFragment(), UsersView, BackButtonListener {
    private var vb: FragmentMainBinding? = null

    companion object {
        fun newInstance() = MainFragment()
    }

    val presenter: MainFragmentPresenter by moxyPresenter {
        MainFragmentPresenter(App.instance.router, AndroidScreens())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        FragmentMainBinding.inflate(inflater, container, false).also {
            vb = it
        }.root

    override fun onDestroyView() {
        super.onDestroyView()
        vb = null
    }

    override fun init() {

    }

    override fun updateList() {

    }

    override fun release() {

    }

    override fun backPressed() = presenter.backPressed()
}