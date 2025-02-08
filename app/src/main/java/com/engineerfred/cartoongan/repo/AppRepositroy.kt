package com.engineerfred.cartoongan.repo

import android.content.Context
import android.graphics.Bitmap
import com.engineerfred.cartoongan.utils.ImageUtils
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

class AppRepository(private val context: Context ) {

    companion object {
        private const val TFLITE_MODEL_NAME = "model.tflite"
    }

    private var interpreter: Interpreter? = null

    init {
        createInterpreter()
    }

    private fun createInterpreter() {
        val options = Interpreter.Options()
        interpreter = Interpreter(FileUtil.loadMappedFile(context, TFLITE_MODEL_NAME), options)
    }

    fun runInference(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val outputArr = Array(1) {
            Array(224) {
                Array(224) {
                    FloatArray(3)
                }
            }
        }
        val byteBuffer = ImageUtils.convertBitmapToByteBuffer(bitmap, 224, 224)
        interpreter?.run(byteBuffer, outputArr)
        return outputArr
    }

}