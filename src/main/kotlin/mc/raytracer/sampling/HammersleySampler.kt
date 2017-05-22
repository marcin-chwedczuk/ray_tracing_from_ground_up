package mc.raytracer.sampling

import mc.raytracer.math.Point2D

class HammersleySampler(numberOfSamples: Int,
                        numberOfSets: Int = Sampler.DEFAULT_NUMBER_OF_SETS)
    : Sampler(numberOfSamples, numberOfSets) {

    override fun generateSamples() {
        for (i in 1..numberOfSets) {
            for (j in 1..numberOfSamples) {
                val sample = Point2D(
                        j.toDouble()/numberOfSamples,
                        phi(j))

                samples.add(sample)
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
