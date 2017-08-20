package mc.raytracer.util

import mc.raytracer.math.Point3D
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

    fun nextPoint(xMin: Int, xMax: Int,
                  yMin: Int, yMax: Int,
                  zMin: Int, zMax: Int)
        = nextPoint(xMin.toDouble(), xMax.toDouble(),
                    yMin.toDouble(), yMax.toDouble(),
                    zMin.toDouble(), zMax.toDouble())

    fun nextPoint(xMin: Double, xMax: Double,
                  yMin: Double, yMax: Double,
                  zMin: Double, zMax: Double)
        = Point3D(xMin + rnd.nextDouble() * (xMax - xMin),
                  yMin + rnd.nextDouble() * (yMax - yMin),
                  zMin + rnd.nextDouble() * (zMax - zMin))

    fun shuffle(list: List<*>) {
        Collections.shuffle(list, rnd)
    }
}