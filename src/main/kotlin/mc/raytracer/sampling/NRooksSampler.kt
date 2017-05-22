package mc.raytracer.sampling

import mc.raytracer.math.Point2D
import mc.raytracer.util.GlobalRandom

class NRooksSampler(numberOfSamples: Int, numberOfSets: Int = Sampler.DEFAULT_NUMBER_OF_SETS)
        : Sampler(numberOfSamples, numberOfSets) {

    override fun generateSamples() {
        for (i in 1..numberOfSets) {
            for (j in 1..numberOfSamples) {
                // generate points at diagonal
                val sample = Point2D(
                        (j - GlobalRandom.nextDouble()) / numberOfSamples,
                        (j - GlobalRandom.nextDouble()) / numberOfSamples)

                samples.add(sample)
            }
        }

        shuffleXCoordinates()
        shuffleYCoordinates()
    }
}
