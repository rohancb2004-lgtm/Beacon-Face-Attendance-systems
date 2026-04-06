package com.example.beaconfaceattendance

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FaceNetHelper(context: Context) {

    private val interpreter: Interpreter

    init {
        val asset = context.assets.open("mobile_face_net.tflite")
        val bytes = asset.readBytes()

        val buffer = ByteBuffer.allocateDirect(bytes.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(bytes)

        interpreter = Interpreter(buffer)
    }

    fun getEmbedding(bitmap: Bitmap): FloatArray {

        val input = ByteBuffer.allocateDirect(1 * 112 * 112 * 3 * 4)
        input.order(ByteOrder.nativeOrder())

        val resized = Bitmap.createScaledBitmap(bitmap, 112, 112, true)

        for (y in 0 until 112) {
            for (x in 0 until 112) {

                val pixel = resized.getPixel(x, y)

                input.putFloat(((pixel shr 16 and 0xFF) - 128f) / 128f)
                input.putFloat(((pixel shr 8 and 0xFF) - 128f) / 128f)
                input.putFloat(((pixel and 0xFF) - 128f) / 128f)
            }
        }

        val output = Array(1) { FloatArray(192) }
        interpreter.run(input, output)

        return output[0]
    }
}