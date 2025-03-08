package com.engineerfred.cartoongan.ui


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineerfred.cartoongan.repo.AppRepository
import com.engineerfred.cartoongan.utils.ImageUtils.toBitmap
import com.engineerfred.cartoongan.utils.ImageUtils.toImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set
    var cartoonImageBitmap by mutableStateOf<Bitmap?>(null)
        private set
    var loading by mutableStateOf(false)
        private set

    fun cartoonify(context: Context, uri: Uri?) {
        uri?.let {
            selectedImageUri = uri
            viewModelScope.launch {
                try {
                    it.toBitmap(context)?.let { bitmap ->
                        val outputArr = repository.runInference(bitmap)
                        cartoonImageBitmap = outputArr.toImage(outputArr)
                    }
                } catch (e: Exception) {
                    // Handle error
                } finally {
                    loading = true
                    delay(2000)
                    loading = false
                }
            }
        }
    }

}