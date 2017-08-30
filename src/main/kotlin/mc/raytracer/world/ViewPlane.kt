package mc.raytracer.world

import mc.raytracer.sampling.MultiJitteredSampler
import mc.raytracer.sampling.RegularSampler
import mc.raytracer.sampling.SquareSampler
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor

class ViewPlane(
        horizontalResolution: Int,
        verticalResolution: Int,
        var pixelSize: Double = 1.0,
        var gamma: Double = 1.0,
        var showOutOfGamutErrors: Boolean = false
) {
    lateinit var sampler: SquareSampler

    var horizontalResolution: Int = horizontalResolution
        private set

    var verticalResolution: Int = verticalResolution
        private set

    val numberOfSamplesPerPixel: Int
            get() { return sampler.numberOfSamples }

    init {
        configureNumberOfSamplesPerPixel(1)
    }

    fun configureNumberOfSamplesPerPixel(num: Int) {
        System.out.println("SAMPLES NUMBER PER PIXEL: " + num)

        if (num > 0) {
            sampler = MultiJitteredSampler(num)
        }
        else {
            sampler = RegularSampler(1)
        }
    }

    fun changeResolutionPreservingViewPlaneWidth(newHorizontalResolution: Int,
                                                 newVerticalResolution: Int) {
        if (newHorizontalResolution < 1)
            throw IllegalArgumentException("newHorizontalResolution cannot be less than 1.")

        if (newVerticalResolution < 1)
            throw IllegalArgumentException("newVerticalResolution cannot be less than 1.")

        val oldWidth = pixelSize*horizontalResolution

        horizontalResolution = newHorizontalResolution
        verticalResolution = newVerticalResolution

        pixelSize = oldWidth / newHorizontalResolution
    }

    fun setViewPlaneWidth(newWidth: Double) {
        if (newWidth <= 0)
            throw IllegalArgumentException("newWidth cannot be less then ZERO.")

        this.pixelSize = newWidth / horizontalResolution
    }

    fun displayPixel(canvas: RawBitmap, vpRow: Int, vpCol: Int, color: RgbColor) {
        var colorToDisplay =
                if (showOutOfGamutErrors)
                    color.orWhenOutOfRange(RgbColor.pink)
                else
                    color.scaleMaxToOne()

        if (gamma != 1.0)
            colorToDisplay = colorToDisplay.powComponents(1.0/gamma)

        canvas.setPixel(vpRow, vpCol, colorToDisplay)
    }
}
