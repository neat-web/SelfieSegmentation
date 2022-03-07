package com.example.selfiesegmentation.ui

import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.selfiesegmentation.R
import com.example.selfiesegmentation.databinding.MainFragmentBinding
import com.example.selfiesegmentation.util.getBitmapFromRes
import com.example.selfiesegmentation.util.getBitmapFromUri
import java.io.IOException

/**
 * MainFragment of the app
 */
class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: MainViewModel
    private lateinit var image: ImageView
    private lateinit var selectMode: RadioGroup
    private val displayWidth = Resources.getSystem().displayMetrics.widthPixels

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = MainFragmentBinding.inflate(inflater, container, false)

        // If we started the app for the first time, we load default images into the ViewModel
        if (!homeViewModel.isInitialized) {
            initializeDefaultImages()
            homeViewModel.isInitialized = true
        }

        // Observe the ViewModel's image
        image = binding.image
        homeViewModel.currentImage.observe(viewLifecycleOwner) {
            image.setImageBitmap(it)
        }

        // OnClickListeners for choosing an image
        binding.selectFrontButton.setOnClickListener {
            homeViewModel.choseFront = true
            chooseImage.launch("image/*")
        }
        binding.selectBgButton.setOnClickListener {
            homeViewModel.choseFront = false
            chooseImage.launch("image/*")
        }

        // Tell the ViewModel when a new mode is chosen
        selectMode = binding.selectMode
        binding.selectMode.setOnCheckedChangeListener { _, isChecked ->
            homeViewModel.modeSelected(isChecked)
        }

        // Observe the ViewModel's selected mode
        homeViewModel.selectedMode.observe(viewLifecycleOwner) {
            selectMode.check(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Load a bitmap using the URI and inform the ViewModel about it
     */
    private fun imageChosen(imageUri: Uri) {
        try {
            val imageBitmap = getBitmapFromUri(
                requireActivity().contentResolver,
                imageUri,
                displayWidth
            )
            homeViewModel.imageChosen(imageBitmap)
        } catch (e: IOException) {
            println("Error occurred while trying to load the selected image: $e")
        }
    }

    /**
     * Start intent for requesting an image from the library
     */
    private val chooseImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageChosen(uri)
            } else {
                println("Image could not be chosen")
            }
        }

    /**
     * Loads two sample images as if they were chosen from the user
     */
    private fun initializeDefaultImages() {
        val initialFront = getBitmapFromRes(resources, R.drawable.woman, displayWidth)
        homeViewModel.imageChosen(initialFront)

        homeViewModel.choseFront = false
        val initialBg = getBitmapFromRes(resources, R.drawable.beach, displayWidth)
        homeViewModel.imageChosen(initialBg)
    }
}