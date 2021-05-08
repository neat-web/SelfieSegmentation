package com.example.selfiesegmentation.ui

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfiesegmentation.R
import com.example.selfiesegmentation.util.ProcessedListener
import com.example.selfiesegmentation.util.SegmentHelper
import com.example.selfiesegmentation.util.resizeBitmap

/**
 * ViewModel of the fragment
 */
class MainViewModel : ViewModel(), ProcessedListener {
    private val _currentImage = MutableLiveData<Bitmap>()
    val currentImage: LiveData<Bitmap> = _currentImage

    private val _selectedMode = MutableLiveData<Int>()
    val selectedMode: LiveData<Int> = _selectedMode

    var choseFront = true
    var isInitialized = false

    private var _normalImage: Bitmap
    private var _maskImage: Bitmap
    private var _maskBgImage: Bitmap
    private var _bgImage: Bitmap

    private var _segmentHelper: SegmentHelper = SegmentHelper(this)

    init {
        // Initialize dummy bitmap so we don't need to make the variables nullable
        _normalImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        _bgImage = _normalImage
        _maskImage = _normalImage
        _maskBgImage = _normalImage

        // At startup normal mode is selected
        _selectedMode.apply { value = R.id.show_normal }
    }

    /**
     * Processes a chosen image
     */
    fun imageChosen(bmp: Bitmap) {
        if (choseFront) {
            _normalImage = bmp
            _bgImage = resizeBitmap(_bgImage, _normalImage.width, _normalImage.height)
            _segmentHelper.processImage(_normalImage)
        } else {
            _bgImage = resizeBitmap(bmp, _normalImage.width, _normalImage.height)
            _maskBgImage = _segmentHelper.generateMaskBgImage(_normalImage, _bgImage)
            setCurrentImage()
        }
    }

    /**
     * Updates the observed bitmap depending on the current mode
     */
    private fun setCurrentImage() {
        _currentImage.apply {
            value = when (selectedMode.value) {
                R.id.show_normal -> {
                    println("normal selected")
                    _normalImage
                }
                R.id.show_mask -> {
                    println("mask selected")
                    _maskImage
                }
                R.id.show_custombg -> {
                    println("custombg selected")
                    _maskBgImage
                }
                else -> {
                    println("id ${selectedMode.value} is not a valid mode")
                    _normalImage
                }
            }
        }
    }

    /**
     * Sets the selected mode
     */
    fun modeSelected(id: Int) {
        _selectedMode.apply { value = id }
        setCurrentImage()
    }

    /**
     *  Updates the images once an image was processed
     */
    override fun imageProcessed() {
        _maskImage = _segmentHelper.generateMaskImage(_normalImage)
        _maskBgImage = _segmentHelper.generateMaskBgImage(_normalImage, _bgImage)
        setCurrentImage()
    }
}
