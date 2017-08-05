package mc.raytracer.lighting

import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class AmbientLight(
        val color: RgbColor = RgbColor.white,
        val radianceScalingFactor: Double = 1.0
    ): Light {

    override fun computeDirectionFromHitPointToLight(shadingInfo: ShadingInfo): Vector3D {
        return Vector3D.zero
    }

    override fun computeLuminanceContributedByLight(shadingInfo: ShadingInfo): RgbColor {
        return color*radianceScalingFactor
    }

}
