package com.example.android_4_4_converter.mvp.model

import android.content.ContentResolver
import android.content.Intent

interface FileChooserPresenter {
    fun onFileChooserButtonClicked()
    fun fileHasBeenSelected(data: Intent?, contentResolver: ContentResolver)

}
