package mc.raytracer.sampling

import mc.raytracer.math.PI
import mc.raytracer.math.Point2D
import mc.raytracer.util.GlobalRandom
import java.lang.Math.cos
import java.lang.Math.sin
import java.util.*
import kotlin.collections.ArrayList

abstract class SquareSampler(numberOfSamples: Int = BaseSampler.DEFAULT_NUMBER_OF_SAMPLES,
                             numberOfSets: Int = BaseSampler.DEFAULT_NUMBER_OF_SETS)
    : BaseSampler(numberOfSamples, numberOfSets) {

    protected val squareSamples: ArrayList<Point2D> =
        ArrayList(numberOfSamples*numberOfSets)

    internal fun getSquareSamples(): List<Point2D> =
        Collections.unmodifiableList(squareSamples)

    fun nextSampleOnUnitSquare()
        = squareSamples[nextShuffledIndex()]

    protected fun shuffleXCoordinates() {
        for (p in 0 until numberOfSets) {
            val setOffset = p*numberOfSamples

            for (i in 0 until (numberOfSamples-1)) {
                val index1 = i
                val index2 = GlobalRandom.nextInt(i, numberOfSamples) + setOffset

                val (newPoint1, newPoint2) =
                        Point2D.exchangeXCoordinates(squareSamples[index1], squareSamples[index2])

                squareSamples[index1] = newPoint1
                squareSamples[index2] = newPoint2
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
                        Point2D.exchangeYCoordinates(squareSamples[index1], squareSamples[index2])

                squareSamples[index1] = newPoint1
                squareSamples[index2] = newPoint2
            }
        }
    }

}
