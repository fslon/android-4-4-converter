package com.example.android_4_4_converter.mvp.presenter

import com.example.android_4_4_converter.mvp.view.UsersView
import com.example.android_4_4_converter.navigation.IScreens
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter


class MainFragmentPresenter(val router: Router, val screens: IScreens) : MvpPresenter<UsersView>() {





    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.init()
        loadData()
//        usersListPresenter.itemClickListener = { itemView ->
//            val user = usersListPresenter.users[itemView.pos]
//
//            router.navigateTo(screens.profileUser(user)) // переход на экран пользователя c помощью router.navigateTo
//        }
    }

    fun loadData() {
//        val users = usersRepo.getUsers().subscribe(
//            { s ->
//                usersListPresenter.users.add(s)
//            },
//            { e ->
//                println("onError: ${e.message}")
//            }
//        )
//        viewState.updateList()
    }

    fun backPressed(): Boolean {
        router.exit()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewState.release()
    }
}

