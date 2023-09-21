package com.example.android_4_4_converter.mvp.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.android_4_4_converter.mvp.presenter.MainFragmentPresenter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FilesConverter {

    private var presenter: MainFragmentPresenter? = null

    private val disposable = CompositeDisposable()

    private lateinit var inputFilePath: String

    private lateinit var cacheDir: File // зачем то нужно для этапа конвертации jpeg -> png

    fun setPresenter(presenter: MainFragmentPresenter) {
        this.presenter = presenter
    }


    fun convertFile(selectedFileUri: Uri?, cacheDir: File) {
        this.cacheDir = cacheDir

        selectedFileUri?.let { uri ->
            val selectedFile = File(uri.path) // преобразование Uri в File

            getFilePath(selectedFile) // получение пути файла

            disposable.add(
                convertFileToPNG().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ outputFile -> // Обработка отформатированного файла
                        presenter?.showInfo("Файл успешно преобразован")

                        saveFileToExplorer(outputFile) // сохранение файла

                    }, { error -> // Обработка ошибки
                        error.printStackTrace()
                    })
            )


        }
    }


    private fun convertFileToPNG(): Single<File> { // конвертация файла JPEG -> PNG
        return Single.fromCallable {
            val outputFile = File(cacheDir, "converted_image.png")
            val bitmap: Bitmap = BitmapFactory.decodeFile(inputFilePath)
            val outputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return@fromCallable outputFile
        }

    }

    private fun getFilePath(inputFile: File) { // получение абсолютного пути файла
        inputFilePath = "/storage/emulated/0/" + inputFile.path.substringAfter(":") // небольшой хардкод первой части пути файла
//        return "/storage/emulated/0/" + inputFile.path.substringAfter(":")
    }


    private fun saveFileToExplorer(inputFile: File) { // сохранение преобразованного файла (в формате PNG) в файлы устройства

        Completable.complete().observeOn(Schedulers.io()).doOnComplete {

            try {
                val outputStream = FileOutputStream(inputFilePath.substringBeforeLast(".") + ".png")

                inputFile.inputStream().use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                outputStream.close()

            } catch (e: IOException) {
                Throwable(e)
            }

        }.subscribe({
//            Log.e(",,,,,,,,,,", "ГОТОВО")
            presenter?.showInfo("Файл сохранен")
        },
            { error ->
//                Log.e(",,,,,,,,,,", "ОШИБКА")
                presenter?.showInfo("Ошибка сохранения файла!")
            })

    }


}