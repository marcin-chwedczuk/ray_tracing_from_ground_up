package mc.raytracer.sampling

import mc.raytracer.math.Point2D
import mc.raytracer.util.GlobalRandom
import java.util.*
import kotlin.collections.ArrayList

abstract class Sampler(val numberOfSamples: Int = 1,
                       val numberOfSets: Int = Sampler.DEFAULT_NUMBER_OF_SETS) {

    protected val samples: ArrayList<Point2D> = ArrayList(numberOfSamples*numberOfSets)
    protected val shuffledIndices: IntArray = generateSuffledIndices()

    protected var count = 0
    protected var jump = 0

    abstract fun generateSamples()

    fun generateSuffledIndices(): IntArray {
        val singleSetIndices = (1..numberOfSamples).toMutableList()
        val indices = ArrayList<Int>(numberOfSamples*numberOfSets)

        for (i in 1..numberOfSets) {
            Collections.shuffle(singleSetIndices)
            indices.addAll(singleSetIndices)
        }

        return indices.toIntArray()
    }

    fun shuffleSamples() {
        throw NotImplementedError()
    }

    fun sampleUnitSquare(): Point2D {
        if ((count % numberOfSamples) == 0) {
            jump = (GlobalRandom.nextInt() % numberOfSets) * numberOfSamples
        }
        return samples[jump + (count++ % numberOfSamples)]
    }

    protected fun shuffleXCoordinates() {
        for (p in 0 until numberOfSets) {
            val setOffset = p*numberOfSamples

            for (i in 0 until (numberOfSamples-1)) {
                val index1 = i
                val index2 = GlobalRandom.nextInt(i, numberOfSamples) + setOffset

                val (newPoint1, newPoint2) =
                        Point2D.exchangeXCoordinates(samples[index1], samples[index2])

                samples[index1] = newPoint1
                samples[index2] = newPoint2
            }
        }
    }

    protected fun shuffleYCoordinates() {
        for (p in 0 until numberOfSets) {
            val setOffset = p*numberOfSamples

            for (i in 0 until (numberOfSamples-1)) {
                val index1 = i
                val index2 = GlobalRandom.nextInt(i, numberOfSamples) + setOffset

                val (newPoint1, newPoint2) =
                        Point2D.exchangeYCoordinates(samples[index1], samples[index2])

                samples[index1] = newPoint1
                samples[index2] = newPoint2
            }
        }
    }

    companion object {
        const val DEFAULT_NUMBER_OF_SETS = 83
    }
}
