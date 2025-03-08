package com.engineerfred.cartoongan.ui

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(
    context: Context,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel()
) {

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.cartoonify(context, uri)
    }

    val selectedImageUri = viewModel.selectedImageUri
    val cartoonImageBitmap = viewModel.cartoonImageBitmap
    val loading = viewModel.loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    "Cartoonify",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = TextStyle(
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = MaterialTheme.colorScheme.secondary,
                            blurRadius = 4f,
                            offset = androidx.compose.ui.geometry.Offset(2f, 2f)
                        ),
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.SansSerif,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            ), start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        )
                    )
                ) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 40.dp, end = 20.dp),
                onClick = { imagePickerLauncher.launch("image/*") },
                contentColor = Color.White,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Pick Image")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
                    .shadow(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if ( selectedImageUri != null ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Text(
                        text = "Select any image by clicking the + button below",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        style = TextStyle(
                            textMotion = TextMotion.Animated
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                when {
                    loading.not() && cartoonImageBitmap == null -> {
                        Text(
                            text = "Cartoon version of the selected image will appear here",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                            style = TextStyle(
                                textMotion = TextMotion.Animated
                            ),
                        )
                    }
                    loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Generating cartoon version...", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    cartoonImageBitmap != null -> {
                        Image(
                            bitmap = cartoonImageBitmap.asImageBitmap(),
                            contentDescription = "Cartoonified Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(16.dp)
                                )
                                .shadow(8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Text(
                text = "engfred88@gmail.com",
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,

            )
        }
    }
}
