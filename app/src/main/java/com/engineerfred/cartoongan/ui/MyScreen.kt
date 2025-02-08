package com.engineerfred.cartoongan.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.engineerfred.cartoongan.repo.AppRepository
import com.engineerfred.cartoongan.utils.ImageUtils.toBitmap
import com.engineerfred.cartoongan.utils.ImageUtils.toImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

@Composable
fun MyScreen(
    context: Context,
    modifier: Modifier = Modifier
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var cartoonImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var loading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = selectedImageUri){
        delay(2000)
        loading = false
    }

    val repository = remember {
        AppRepository(context)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
        uri?.let {
            uri.toBitmap(context, it)?.let { bitmap ->
                val outputArr = repository.runInference(bitmap)
                cartoonImageBitmap = outputArr.toImage(outputArr)
            }
            loading = true
        }
    }

    Column (
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Select an image to cartoonify", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        if ( loading ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
                Text("Generating results...", fontSize = 14.sp)
            }
        } else {
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.animation.AnimatedVisibility(
                modifier = Modifier.weight(1f),
                visible = cartoonImageBitmap != null
            ) {
                Image(
                    bitmap = cartoonImageBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp)

            )

            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Pick Image", fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}