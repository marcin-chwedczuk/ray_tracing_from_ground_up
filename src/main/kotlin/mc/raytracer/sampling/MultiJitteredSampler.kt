package mc.raytracer.sampling

import mc.raytracer.math.Point2D
import mc.raytracer.util.GlobalRandom
import mc.raytracer.util.sqrtInt

class MultiJitteredSampler(numberOfSamples: Int = BaseSampler.DEFAULT_NUMBER_OF_SAMPLES,
                           numberOfSets: Int = BaseSampler.DEFAULT_NUMBER_OF_SETS)
    : SquareSampler(numberOfSamples, numberOfSets) {

    init { generateSamples() }

    private fun generateSamples() {
        val n = numberOfSamples.sqrtInt()

        for (i in 1..(numberOfSamples * numberOfSets))
            squareSamples.add(Point2D.zero)

        val subcellWidth = 1.0 / numberOfSamples

        // distribute points in the initial patterns
        for (p in 0 until numberOfSets) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val x = (i * n + j) * subcellWidth + subcellWidth * GlobalRandom.nextDouble()
                    val y = (j * n + i) * subcellWidth + subcellWidth * GlobalRandom.nextDouble()

                    squareSamples[i * n + j + p * numberOfSamples] = Point2D(x, y)
                }
            }
        }

        // shuffle x coordinates
        for (p in 0 until numberOfSets) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val k = GlobalRandom.nextInt(j, n)

                    val index1 = i * n + j + p * numberOfSamples
                    val index2 = i * n + k + p * numberOfSamples

                    val (newPoint1, newPoint2) =
                            Point2D.exchangeXCoordinates(squareSamples[index1], squareSamples[index2])

                    squareSamples[index1] = newPoint1
                    squareSamples[index2] = newPoint2
                }
            }
        }

        // shuffle y coordinates
        for (p in 0 until numberOfSets) {
            for (i in 0 until n) {
                for (j in 0 until n) {
                    val k = GlobalRandom.nextInt(j, n)

                    val index1 = j * n + i + p * numberOfSamples
                    val index2 = k * n + i + p * numberOfSamples

                    val (newPoint1, newPoint2) =
                            Point2D.exchangeYCoordinates(squareSamples[index1], squareSamples[index2])

                    squareSamples[index1] = newPoint1
                    squareSamples[index2] = newPoint2
                }
            }
        }
    }
}
