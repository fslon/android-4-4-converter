package com.example.android_4_4_converter.navigation

import com.example.android_4_4_converter.ui.fragment.MainFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

class AndroidScreens : IScreens {
    override fun mainFragment() = FragmentScreen { MainFragment.newInstance() }
}
