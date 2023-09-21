package com.example.android_4_4_converter.mvp.model

import android.content.Intent
import java.io.File

interface FileChooserPresenter { // fragment -> presenter
    fun onFileChooserButtonClicked()
    fun fileHasBeenSelected(requestCode: Int, resultCode: Int, data: Intent?, cacheDir: File, requestCodeFileChooser: Int)

}
