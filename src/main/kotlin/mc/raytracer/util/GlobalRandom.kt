package mc.raytracer.util

import java.util.*

object GlobalRandom {
    private val rnd = Random()

    fun setSeed(seed: Long) {
        rnd.setSeed(seed)
    }

    fun nextInt()
        = rnd.nextInt(Int.MAX_VALUE)

    // upper bound is not included
    fun nextInt(upperBound: Int)
        = rnd.nextInt(upperBound)

    // upper bound is not included
    fun nextInt(lowerBound: Int, upperBound: Int)
        = lowerBound + rnd.nextInt(upperBound-lowerBound)

    fun nextDouble()
        = rnd.nextDouble()
}