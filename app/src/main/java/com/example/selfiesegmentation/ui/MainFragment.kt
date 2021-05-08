package com.example.selfiesegmentation.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.selfiesegmentation.R
import com.example.selfiesegmentation.databinding.MainFragmentBinding
import com.example.selfiesegmentation.util.getBitmapFromRes
import com.example.selfiesegmentation.util.getBitmapFromUri
import java.io.IOException

/**
 * Intent code for requesting an image from the library
 */
const val REQUEST_CHOOSE_IMAGE = 1002

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
        homeViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        _binding = MainFragmentBinding.inflate(inflater, container, false)

        // If we started the app for the first time, we load default images into the ViewModel
        if (!homeViewModel.isInitialized) {
            initializeDefaultImages()
            homeViewModel.isInitialized = true
        }

        // Observe the ViewModel's image
        image = binding.image
        homeViewModel.currentImage.observe(viewLifecycleOwner, {
            image.setImageBitmap(it)
        })

        // OnClickListeners for choosing an image
        binding.selectFrontButton.setOnClickListener {
            homeViewModel.choseFront = true
            chooseImage()
        }
        binding.selectBgButton.setOnClickListener {
            homeViewModel.choseFront = false
            chooseImage()
        }

        // Tell the ViewModel when a new mode is chosen
        selectMode = binding.selectMode
        binding.selectMode.setOnCheckedChangeListener { _, isChecked ->
            homeViewModel.modeSelected(isChecked)
        }

        // Observe the ViewModel's selected mode
        homeViewModel.selectedMode.observe(viewLifecycleOwner, {
            selectMode.check(it)
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        // Check if our intent for requesting an image from the library was successful
        if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data!!.data!!
            imageChosen(imageUri)
        } else {
            println("Image could not be chosen: $requestCode, $resultCode")
        }
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
            println("Error occured while trying to load the selected image: $e")
        }
    }

    /**
     * Start intent for requesting an image from the library
     */
    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_CHOOSE_IMAGE
        )
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