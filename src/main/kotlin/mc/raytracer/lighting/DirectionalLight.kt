package mc.raytracer.lighting

import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class DirectionalLight(
        direction: Vector3D,
        val color: RgbColor = RgbColor.white,
        val radianceScalingFactor: Double = 1.0
    ): Light {

    val direction = direction.norm()
    val reversedDirection = -direction.norm()

    override fun computeDirectionFromHitPointToLight(shadingInfo: ShadingInfo): Vector3D {
        return reversedDirection
    }

    override fun computeLuminanceContributedByLight(shadingInfo: ShadingInfo): RgbColor {
        return color*radianceScalingFactor
    }

}