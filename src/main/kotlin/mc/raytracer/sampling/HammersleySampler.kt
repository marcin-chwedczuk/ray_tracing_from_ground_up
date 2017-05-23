package mc.raytracer.sampling

import mc.raytracer.math.Point2D

class HammersleySampler(numberOfSamples: Int = BaseSampler.DEFAULT_NUMBER_OF_SAMPLES,
                        numberOfSets: Int = BaseSampler.DEFAULT_NUMBER_OF_SETS)
    : SquareSampler(numberOfSamples, numberOfSets) {

    init { generateSamples() }

    private fun generateSamples() {
        for (i in 1..numberOfSets) {
            for (j in 1..numberOfSamples) {
                val sample = Point2D(
                        j.toDouble()/numberOfSamples,
                        phi(j))

                squareSamples.add(sample)
            }
        }
    }

    private fun phi(num: Int): Double {
        var j = num
        var f = 0.5
        var x = 0.0

        while (j != 0) {
            x += f * (j and 1).toDouble()
            j = j shr 1
            f *= 0.5
        }

        return x
    }
}
