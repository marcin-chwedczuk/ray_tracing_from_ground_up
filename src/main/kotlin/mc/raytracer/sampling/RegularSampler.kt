package mc.raytracer.sampling

import mc.raytracer.math.Point2D
import mc.raytracer.util.sqrtInt

class RegularSampler(numberOfSamples: Int)
    : SquareSampler(numberOfSamples, 1) {

    init { generateSamples() }

    private fun generateSamples() {
        val n = numberOfSamples.sqrtInt()

        for (i in 1..numberOfSets) {
            // generate set of squareSamples
            for (x in 1..n) {
                for (y in 1..n) {
                    val sample = Point2D(
                            (x - 0.5) / n,
                            (y - 0.5) / n)

                    squareSamples.add(sample)
                }
            }
        }
    }
}
