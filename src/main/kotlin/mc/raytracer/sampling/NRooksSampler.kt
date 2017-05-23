package mc.raytracer.sampling

import mc.raytracer.math.Point2D
import mc.raytracer.util.GlobalRandom

class NRooksSampler(numberOfSamples: Int = BaseSampler.DEFAULT_NUMBER_OF_SAMPLES,
                    numberOfSets: Int = BaseSampler.DEFAULT_NUMBER_OF_SETS)
        : SquareSampler(numberOfSamples, numberOfSets) {

    init { generateSamples() }

    private fun generateSamples() {
        for (i in 1..numberOfSets) {
            for (j in 1..numberOfSamples) {
                // generate points at diagonal
                val sample = Point2D(
                        (j - GlobalRandom.nextDouble()) / numberOfSamples,
                        (j - GlobalRandom.nextDouble()) / numberOfSamples)

                squareSamples.add(sample)
            }
        }

        shuffleXCoordinates()
        shuffleYCoordinates()
    }
}
