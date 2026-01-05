package com.shreyapoojary.smartfaceattendance.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class FaceNetModel(context: Context) {

    private val interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(context))
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("facenet.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun getEmbedding(bitmap: Bitmap): FloatArray {
        val input = preprocess(bitmap)
        val output = Array(1) { FloatArray(128) }
        interpreter.run(input, output)
        return output[0]
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val resized = Bitmap.createScaledBitmap(bitmap, 160, 160, true)
        val buffer = ByteBuffer.allocateDirect(1 * 160 * 160 * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())

        for (y in 0 until 160) {
            for (x in 0 until 160) {
                val pixel = resized.getPixel(x, y)
                buffer.putFloat(((pixel shr 16) and 0xFF) / 255f)
                buffer.putFloat(((pixel shr 8) and 0xFF) / 255f)
                buffer.putFloat((pixel and 0xFF) / 255f)
            }
        }
        buffer.rewind()
        return buffer
    }
}
