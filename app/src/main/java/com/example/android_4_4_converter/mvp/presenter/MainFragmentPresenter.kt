package com.example.android_4_4_converter.mvp.presenter

import android.app.Activity
import android.content.Intent
import com.example.android_4_4_converter.mvp.model.FilesConverter
import com.example.android_4_4_converter.mvp.view.UsersView
import com.example.android_4_4_converter.navigation.IScreens
import com.github.terrakok.cicerone.Router
import moxy.MvpPresenter
import java.io.File


class MainFragmentPresenter(val router: Router, val screens: IScreens, private val view: UsersView) : MvpPresenter<UsersView>(),
    FileChooserPresenter {

    private val model: FilesConverter = FilesConverter()

    private lateinit var cacheDir: File // зачем то нужно для этапа конвертации jpeg -> png

    override fun onFileChooserButtonClicked() {
        model.setPresenter(this)

        view.showFileChooser()
    }

    override fun fileHasBeenSelected(requestCode: Int, resultCode: Int, data: Intent?, cacheDir: File, requestCodeFileChooser: Int) {

        this.cacheDir = cacheDir

        if (requestCode == requestCodeFileChooser && resultCode == Activity.RESULT_OK) { // если это интент выбора файла и результат успешно получен
            val selectedFileUri = data?.data // переменная с URI файла

            model.convertFile(selectedFileUri, cacheDir)
        }

    }

    fun showInfo(text: String) {
        view.showToast(text)
    }

    fun backPressed(): Boolean {
        router.exit()
        return true
    }


}

