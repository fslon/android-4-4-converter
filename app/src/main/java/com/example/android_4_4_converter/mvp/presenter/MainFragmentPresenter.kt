package com.example.android_4_4_converter.mvp.presenter

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.android_4_4_converter.mvp.model.FileChooserPresenter
import com.example.android_4_4_converter.mvp.view.UsersView
import com.example.android_4_4_converter.navigation.IScreens
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.io.File
import java.io.FileOutputStream


class MainFragmentPresenter(val router: Router, val screens: IScreens, private val view: UsersView) : MvpPresenter<UsersView>(),
    FileChooserPresenter {

    lateinit var filePath: String

    override fun onFileChooserButtonClicked() {
        view.showFileChooser()
    }

    override fun fileHasBeenSelected(data: Intent?, contentResolver: ContentResolver) {
        val selectedImageUri: Uri = data!!.data!!
        val imagePath = selectedImageUri.path
        val file = File(imagePath)
        convertImageToPng(file)
    }

    private fun convertImageToPng(file: File) {
        Observable.fromCallable {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun backPressed(): Boolean {
        router.exit()
        return true
    }


}

