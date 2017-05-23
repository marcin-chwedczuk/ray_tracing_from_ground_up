package mc.raytracer.sampling

import mc.raytracer.math.Point2D
import mc.raytracer.util.GlobalRandom
import java.util.*

open class BaseSampler(
        val numberOfSamples: Int = BaseSampler.DEFAULT_NUMBER_OF_SAMPLES,
        val numberOfSets: Int = BaseSampler.DEFAULT_NUMBER_OF_SETS) {

    private val shuffledIndices: IntArray = generateShuffledIndices()
    private var count: Int = 0
    private var jump: Int = 0

    private fun generateShuffledIndices(): IntArray {
        val singleSetIndices = (0 until numberOfSamples).toMutableList()
        val indices = ArrayList<Int>(numberOfSamples*numberOfSets)

        for (i in 1..numberOfSets) {
            Collections.shuffle(singleSetIndices)
            indices.addAll(singleSetIndices)
        }

        return indices.toIntArray()
    }

    protected fun nextShuffledIndex(): Int {
        if ((count % numberOfSamples) == 0) {
            jump = (GlobalRandom.nextInt() % numberOfSets) * numberOfSamples
        }

        return jump + shuffledIndices[jump + (count++ % numberOfSamples)]
    }

    companion object {
        const val DEFAULT_NUMBER_OF_SAMPLES = 1
        const val DEFAULT_NUMBER_OF_SETS = 83
    }
}
