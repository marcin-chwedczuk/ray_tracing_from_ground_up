package mc.raytracer.lighting

import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class ConstantColorAmbientLight(
        val color: RgbColor = RgbColor.white,
        val radianceScalingFactor: Double = 1.0
): AmbientLight {

    override fun radiance(shadingInfo: ShadingInfo): RgbColor {
        return color * radianceScalingFactor
    }

}
