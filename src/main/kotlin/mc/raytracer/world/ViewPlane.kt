package mc.raytracer.world

class ViewPlane(
        val horizontalResolution: Int,
        val verticalResolution: Int,
        var pixelSize: Double = 1.0,
        var numberOfSamplesPerPixel: Int = 1,
        var gamma: Double = 1.0,
        var showOutOfGamutErrors: Boolean = false
) {
    val gammaInv
        get() = 1.0/gamma
}
