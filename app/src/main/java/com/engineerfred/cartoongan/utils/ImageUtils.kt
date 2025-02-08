package com.engineerfred.cartoongan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import java.nio.ByteBuffer
import java.nio.ByteOrder

//https://medium.com/walmartglobaltech/custom-tensorflow-lite-model-implementation-in-android-5c1c65bd9f97

object ImageUtils {

    private fun getInputImage(width: Int, height: Int): ByteBuffer {
        val inputImage =
            ByteBuffer.allocateDirect(1 * width * height * 3 * 4)// input image will be required input shape of tflite model
        inputImage.order(ByteOrder.nativeOrder())
        inputImage.rewind()
        return inputImage
    }


    fun convertBitmapToByteBuffer(bitmapIn: Bitmap, width: Int, height: Int): ByteBuffer {
        val bitmap = Bitmap.createScaledBitmap(bitmapIn, width, height, false) // convert bitmap into required size
        // these value can be different for each channel if they are not then you may have single value instead of an array
        val mean = arrayOf(127.5f, 127.5f, 127.5f)
        val standard = arrayOf(127.5f, 127.5f, 127.5f)
        val inputImage = getInputImage(width, height)
        val intValues = IntArray(width * height)

        bitmap.getPixels(intValues, 0, width, 0, 0, width, height)

        for (y in 0 until width) {
            for (x in 0 until height) {
                val px = bitmap.getPixel(x, y)
                // Get channel values from the pixel value.
                val r = Color.red(px)
                val g = Color.green(px)
                val b = Color.blue(px)
                // Normalize channel values to [-1.0, 1.0]. This requirement depends on the model.
                // For example, some models might require values to be normalized to the range
                // [0.0, 1.0] instead.
                val rf = (r - mean[0]) / standard[0]
                val gf = (g - mean[0]) / standard[0]
                val bf = (b - mean[0]) / standard[0]
                //putting in BRG order because this model demands input in this order
                inputImage.putFloat(bf)
                inputImage.putFloat(rf)
                inputImage.putFloat(gf)
            }
        }
        return inputImage
    }


    fun Uri.toBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            // Open the input stream from the Uri
            val inputStream = context.contentResolver.openInputStream(uri)
            // Decode the input stream into a Bitmap
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun Array<Array<Array<FloatArray>>>.toImage(inferenceResult: Array<Array<Array<FloatArray>>>): Bitmap {
        val output = inferenceResult[0]
        val bitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(224 * 224)
        var index = 0
        //Two nested loops iterate through the height (y) and width (x) of the output image
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                // Convert the model output back to pixel values
                val b = (output[y][x][0] + 1) * 127.5 //blue channel
                val r = (output[y][x][1] + 1) * 127.5 //red channel
                val g = (output[y][x][2] + 1) * 127.5 //green channel
                val a = 0xFF // Alpha channel (fully opaque)

                // Combine the channel values into a single pixel value
                pixels[index] = a shl 24 or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
                index++
            }
        }
        // Set the pixel values to the bitmap
        bitmap.setPixels(pixels, 0, 224, 0, 0, 224, 224)
        return bitmap
    }


}