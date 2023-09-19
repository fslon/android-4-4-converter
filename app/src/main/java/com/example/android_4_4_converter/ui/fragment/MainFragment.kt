package com.example.android_4_4_converter.ui.fragment

import android.Manifest
import android.app.Activity
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
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension


class MainFragment : MvpAppCompatFragment(), UsersView, BackButtonListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val disposable = CompositeDisposable()

    private lateinit var inputFilePath: String

    val presenter: MainFragmentPresenter by moxyPresenter {
        MainFragmentPresenter(App.instance.router, AndroidScreens(), this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.buttonGetFile.setOnClickListener { presenter.onFileChooserButtonClicked() }

        return binding.root
    }

    override fun showFileChooser() {

        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            // Разрешение не предоставлено, запросите его
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), REQUEST_CODE_FILE_CHOOSER)

        } else {
            // Разрешение уже предоставлено, можно выполнять операции с файлами
        }


        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/jpeg"
        startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_CHOOSER && resultCode == Activity.RESULT_OK) {
            val selectedFileUri = data?.data

            selectedFileUri?.let { uri ->

                val selectedFile = File(uri.path) // Преобразование Uri в File
// Используем RxJava2 для асинхронного выполнения операций

                disposable.add(
                    Observable.fromCallable { convertFileToPNG(selectedFile) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { outputFile ->  // Здесь вы можете сохранить outputFile на место старого файла JPEG
                            Toast.makeText(requireContext(), "Файл успешно преобразован", Toast.LENGTH_SHORT).show()
                            Log.e("-----------------", "CONVERT IS DONE")

                            saveFileToExplorer(outputFile)


                        }
                )


            }
        }
    }

    //    /document/primary:Pictures/picture-en3dnh2zi84sgt3t.jpg
//        /storage/emulated/0/Pictures/picture-en3dnh2zi84sgt3t.jpg
    private fun convertFileToPNG(inputFile: File): File {
        Log.e("filename ", inputFile.toPath().fileName.toString())
        Log.e("root ", inputFile.toPath().root.toString())
        Log.e("name ", inputFile.toPath().name.toString())
        Log.e("fileName ", inputFile.toPath().fileName.toString())
        Log.e("nameWithoutExtension ", inputFile.toPath().nameWithoutExtension.toString())
        Log.e("path ", inputFile.path)
        Log.e("inputFile.parentFile.absolutePath ", inputFile.parentFile.absolutePath)
        Log.e("path cut ", inputFile.path.substringAfter(":"))
        getFilePath(inputFile)

        val outputFile = File(requireContext().cacheDir, "converted_image.png")
//        val bitmap: Bitmap = BitmapFactory.decodeFile(getFilePath(inputFile))
        val bitmap: Bitmap = BitmapFactory.decodeFile(inputFilePath)
        val outputStream = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return outputFile
    }

    private fun getFilePath(inputFile: File) { // получение абсолютного пути файла
        inputFilePath = "/storage/emulated/0/" + inputFile.path.substringAfter(":")
//        return "/storage/emulated/0/" + inputFile.path.substringAfter(":") // небольшой первой части пути файла
    }


    private fun saveFileToExplorer(inputFile: File) {


        val fileObservable = Observable.just(inputFile)
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
        private const val REQUEST_CODE_FILE_CHOOSER = 100
        fun newInstance(): MainFragment = MainFragment()
    }


}
