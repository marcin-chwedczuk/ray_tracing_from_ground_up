package mc.raytracer.world

import mc.raytracer.sampling.MultiJitteredSampler
import mc.raytracer.sampling.RegularSampler
import mc.raytracer.sampling.SquareSampler

class ViewPlane(
        val horizontalResolution: Int,
        val verticalResolution: Int,
        var pixelSize: Double = 1.0,
        var gamma: Double = 1.0,
        var showOutOfGamutErrors: Boolean = false
) {
    lateinit var sampler: SquareSampler
        private set

    val numerOfSamplesPerPixel: Int
            get() { return sampler.numberOfSamples }

    val gammaInv
        get() = 1.0/gamma

    init {
        setNumberOfSamples(1)
    }

    fun useSampler(sampler: SquareSampler) {
        this.sampler = sampler
    }

    fun setNumberOfSamples(num: Int) {
        if (num > 0) {
            sampler = MultiJitteredSampler(num)
        }
        else {
            sampler = RegularSampler(1)
        }
    }
}
