package mc.raytracer.sampling

import mc.raytracer.math.Point2D
import mc.raytracer.util.GlobalRandom
import mc.raytracer.util.sqrtInt

class RegularSampler(numberOfSamples: Int)
    : Sampler(numberOfSamples, 1) {

    override fun generateSamples() {
        val n = numberOfSamples.sqrtInt()

        for (i in 1..numberOfSets) {
            // generate set of samples
            for (x in 1..n) {
                for (y in 1..n) {
                    val sample = Point2D(
                            (x - 0.5) / n,
                            (y - 0.5) / n)

                    samples.add(sample)
                }
            }
        }
    }
}
