package com.example.android_4_4_converter.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.android_4_4_converter.App
import com.example.android_4_4_converter.databinding.FragmentMainBinding
import com.example.android_4_4_converter.mvp.presenter.MainFragmentPresenter
import com.example.android_4_4_converter.mvp.view.UsersView
import com.example.android_4_4_converter.navigation.AndroidScreens
import com.example.android_4_4_converter.ui.activity.BackButtonListener
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class MainFragment : MvpAppCompatFragment(), UsersView, BackButtonListener {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    val presenter: MainFragmentPresenter by moxyPresenter {
        MainFragmentPresenter(App.instance.router, AndroidScreens(), this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.buttonGetFile.setOnClickListener { presenter.onFileChooserButtonClicked() }

        return binding.root
    }

    override fun showFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_FILE_CHOOSER)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FILE_CHOOSER && resultCode == Activity.RESULT_OK) {
            // Обрабатывай выбранный файл здесь
        }
    }

    override fun backPressed() = presenter.backPressed()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private const val REQUEST_FILE_CHOOSER = 1
        fun newInstance(): MainFragment = MainFragment()
    }


}
