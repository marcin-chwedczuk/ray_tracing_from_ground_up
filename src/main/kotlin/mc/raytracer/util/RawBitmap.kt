package mc.raytracer.util

import java.util.*

class RawBitmap(val width: Int, val heigh: Int) {
    // Pixels are stored in int array
    // in ARGB format.
    private val pixels: IntArray

    val size: Int
        get() = width*heigh

    val rawArgbData: IntArray
        get() = pixels

    init {
        if (width <= 0)
            throw IllegalArgumentException("Bitmap width must be a positive number.")

        if (heigh <= 0)
            throw IllegalArgumentException("Bitmap height must be a positive number.")

        pixels = IntArray(width*heigh)
    }

    fun setPixel(row: Int, col:Int, argb: Int) {
        if (row < 0 || row >= heigh)
            throw IndexOutOfBoundsException("row")

        if (col < 0 || col >= width)
            throw IndexOutOfBoundsException("col")

        pixels[row*width+col] = argb
    }

    fun clear(color: Int) {
        Arrays.fill(pixels, color)
    }
}
