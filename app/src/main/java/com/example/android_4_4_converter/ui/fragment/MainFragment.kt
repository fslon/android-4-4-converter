package com.example.android_4_4_converter.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.android_4_4_converter.App
import com.example.android_4_4_converter.databinding.FragmentMainBinding
import com.example.android_4_4_converter.mvp.presenter.MainFragmentPresenter
import com.example.android_4_4_converter.mvp.view.UsersView
import com.example.android_4_4_converter.navigation.AndroidScreens
import com.example.android_4_4_converter.ui.activity.BackButtonListener
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainFragment : MvpAppCompatFragment(), UsersView, BackButtonListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val disposable = CompositeDisposable()

    private lateinit var inputFilePath: String

    val presenter: MainFragmentPresenter by moxyPresenter {
        MainFragmentPresenter(App.instance.router, AndroidScreens(), this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.buttonGetFile.setOnClickListener { presenter.onFileChooserButtonClicked() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Разрешение не предоставлено, запросите его
            ActivityCompat.requestPermissions( // получение разрешения на доступ к файлам
                requireActivity(),
                arrayOf(permission),
                REQUEST_CODE_FILE_CHOOSER
            )
        }
    }

    override fun showFileChooser() { // открытие окна выбора файлов
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER)
    }

    override fun showToast(text: String) {
        Completable.complete().observeOn(AndroidSchedulers.mainThread()).doOnComplete {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        }.subscribe()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // срабатывает при выборе файла в окне с файлами
        super.onActivityResult(requestCode, resultCode, data)

        presenter.fileHasBeenSelected(requestCode, resultCode, data, requireContext().cacheDir, REQUEST_CODE_FILE_CHOOSER)

//        if (requestCode == REQUEST_CODE_FILE_CHOOSER && resultCode == Activity.RESULT_OK) { // если это интент выбора файла и результат успешно получен
//            val selectedFileUri = data?.data // переменная с URI файла
//            selectedFileUri?.let { uri ->
//
//                val selectedFile = File(uri.path) // преобразование Uri в File
//
//                disposable.add(                      // Используем RxJava2 для асинхронного выполнения операций
//                    Observable.fromCallable { convertFileToPNG(selectedFile) }
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe { outputFile ->
//                            Toast.makeText(requireContext(), "Файл успешно преобразован", Toast.LENGTH_SHORT).show()
////                            Log.e("-----------------", "CONVERT IS DONE")
//
//                            saveFileToExplorer(outputFile) // сохранение файла-
//                        }
//                )
//            }
//        }

    }

    //    /document/primary:Pictures/picture-en3dnh2zi84sgt3t.jpg - приходит такая ссылка
//        /storage/emulated/0/Pictures/picture-en3dnh2zi84sgt3t.jpg - такая должна быть [не везде]

    private fun convertFileToPNG(inputFile: File): File { // конвертация файла JPEG -> PNG
//        Log.e("filename ", inputFile.toPath().fileName.toString())
//        Log.e("root ", inputFile.toPath().root.toString())
//        Log.e("name ", inputFile.toPath().name.toString())
//        Log.e("fileName ", inputFile.toPath().fileName.toString())
//        Log.e("nameWithoutExtension ", inputFile.toPath().nameWithoutExtension.toString())
//        Log.e("path ", inputFile.path)
//        Log.e("inputFile.parentFile.absolutePath ", inputFile.parentFile.absolutePath)
//        Log.e("path cut ", inputFile.path.substringAfter(":"))
        getFilePath(inputFile)

        Log.e("******* ", requireContext().cacheDir.toString())

        val outputFile = File(requireContext().cacheDir, "converted_image.png")
        val bitmap: Bitmap = BitmapFactory.decodeFile(inputFilePath)
        val outputStream = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return outputFile
    }

    private fun getFilePath(inputFile: File) { // получение абсолютного пути файла
        inputFilePath = "/storage/emulated/0/" + inputFile.path.substringAfter(":") // небольшой хардкод первой части пути файла
//        return "/storage/emulated/0/" + inputFile.path.substringAfter(":")
    }


    private fun saveFileToExplorer(inputFile: File) { // сохранение преобразованного файла (в формате PNG) в файлы устройства

        val fileObservable = Observable.just(inputFile) // TODO разобраться с этой строкой, по хорошему удалить
        val ioScheduler = Schedulers.io()
        val saveFileObservable = fileObservable
            .observeOn(ioScheduler)
            .flatMap { _ ->
                Observable.create<Unit> { emitter ->
                    try {
                        val outputStream = FileOutputStream(inputFilePath.substringBeforeLast(".") + ".png")

                        inputFile.inputStream().use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }

                        outputStream.close()
                        emitter.onNext(Unit)
                        emitter.onComplete()

                    } catch (e: IOException) {
                        emitter.onError(e)
                    }
                }
            }


        saveFileObservable.subscribe(
            {
                Log.e(",,,,,,,,,,", "ГОТОВО")
                // TODO показать toast что файл сохранен
            },
            { error ->
                Log.e(",,,,,,,,,,", "ОШИБКА")
            }
        )
    }


    override fun backPressed() = presenter.backPressed()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        disposable.clear()
    }


    companion object {
        const val REQUEST_CODE_FILE_CHOOSER =
            100 // используется для идентификации запроса выбора файла, и затем в методе "onActivityResult" проверяется, что результат соответствует этому запросу

        fun newInstance(): MainFragment = MainFragment()
    }


}
