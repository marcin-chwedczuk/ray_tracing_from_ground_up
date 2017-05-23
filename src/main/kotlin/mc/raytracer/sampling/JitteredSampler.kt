package mc.raytracer.sampling

import mc.raytracer.math.Point2D
import mc.raytracer.util.GlobalRandom
import mc.raytracer.util.sqrtInt

class JitteredSampler(
        numberOfSamples: Int = BaseSampler.DEFAULT_NUMBER_OF_SAMPLES,
        numberOfSets: Int = BaseSampler.DEFAULT_NUMBER_OF_SETS)
    : SquareSampler(numberOfSamples, numberOfSets) {

    init { generateSamples() }

    private fun generateSamples() {
        val n = numberOfSamples.sqrtInt()

        for (i in 1..numberOfSets) {
            // generate set of squareSamples
            for (x in 1..n) {
                for (y in 1..n) {
                    val sample = Point2D(
                            (x - GlobalRandom.nextDouble()) / n,
                            (y - GlobalRandom.nextDouble()) / n)

                    squareSamples.add(sample)
                }
            }
        }
    }
}
