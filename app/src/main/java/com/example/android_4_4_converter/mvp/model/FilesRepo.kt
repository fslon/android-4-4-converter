package com.example.android_4_4_converter.mvp.model

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

class FilesRepo {

////    fun getFileFromStorage() {
//        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                // открываем поток на чтение по полученному URI
//                val intent = result.data
//                val myFile = getContentResolver().openInputStream(intent?.data!!)
//                if (myFile != null) {
//
//                    // читаем данные
//                    val content = myFile.bufferedReader().readText()
//                    // демонстрируем имя файла и объем прочитанных данных
//                    Toast.makeText(this, "File %s, Length %d bytes".format(intent?.data!!.path, content.length), Toast.LENGTH_LONG).show()
//                }
//            }
////        }
//
//        fun onActionClick(view: View) {
//            // настраиваем фильтры intent
//            val intent = Intent()
//                .setType("*/*")
//                .setAction(Intent.ACTION_GET_CONTENT)
//
//            // запускаем контракт
//            startForResult.launch(intent)
//        }
//
//    }


//}



}