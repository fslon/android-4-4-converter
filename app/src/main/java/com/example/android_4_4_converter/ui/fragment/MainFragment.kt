package com.example.android_4_4_converter.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter


class MainFragment : MvpAppCompatFragment(), UsersView, BackButtonListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val disposable = CompositeDisposable()

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
        getPermission()
    }

    private fun getPermission() {

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
        disposable.add(
            Completable.complete().observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
            }.subscribe()
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // срабатывает при выборе файла в окне с файлами
        super.onActivityResult(requestCode, resultCode, data)

        presenter.fileHasBeenSelected(requestCode, resultCode, data, requireContext().cacheDir, REQUEST_CODE_FILE_CHOOSER)

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
