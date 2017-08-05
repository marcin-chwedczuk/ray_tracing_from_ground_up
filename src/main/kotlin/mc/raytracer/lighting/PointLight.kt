package mc.raytracer.lighting

import mc.raytracer.math.Point3D
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class PointLight(
        val location: Point3D,
        val color: RgbColor = RgbColor.white,
        val radianceScalingFactor: Double = 1.0
    ): Light {

    override fun computeDirectionFromHitPointToLight(shadingInfo: ShadingInfo): Vector3D {
        return (location - shadingInfo.localHitPoint ).norm()
    }

    override fun computeLuminanceContributedByLight(shadingInfo: ShadingInfo): RgbColor {
        // no attenuation (dividing by r^2) here because it causes unrealistic effects
        return color*radianceScalingFactor
    }

}