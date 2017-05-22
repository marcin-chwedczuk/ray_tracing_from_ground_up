package mc.raytracer.sampling

import mc.raytracer.math.Point2D
import mc.raytracer.util.GlobalRandom
import mc.raytracer.util.sqrtInt

class JitteredSampler(numberOfSamples: Int, numberOfSets: Int = Sampler.DEFAULT_NUMBER_OF_SETS)
    : Sampler(numberOfSamples, numberOfSets) {

    override fun generateSamples() {
        val n = numberOfSamples.sqrtInt()

        for (i in 1..numberOfSets) {
            // generate set of samples
            for (x in 1..n) {
                for (y in 1..n) {
                    val sample = Point2D(
                            (x - GlobalRandom.nextDouble()) / n,
                            (y - GlobalRandom.nextDouble()) / n)

                    samples.add(sample)
                }
            }
        }
    }
}
