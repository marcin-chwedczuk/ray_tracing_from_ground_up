package mc.raytracer.lighting

import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class DirectionalLight(
        direction: Vector3D,
        val color: RgbColor = RgbColor.white,
        val radianceScalingFactor: Double = 1.0
): LightWithShadowSupport {

    override val castsShadows: Boolean = true

    private val direction = direction.norm()
    private val reversedDirection = -direction.norm()

    override fun computeDirectionFromHitPointToLight(shadingInfo: ShadingInfo): Vector3D {
        return reversedDirection
    }

    override fun computeLuminanceContributedByLight(shadingInfo: ShadingInfo): RgbColor {
        return color*radianceScalingFactor
    }

    override fun isHitPointInShadow(shadingInfo: ShadingInfo, shadowRay: Ray): Boolean {
        return shadingInfo.world.existsObjectInDirection(shadowRay, maxDistance=Double.MAX_VALUE)
    }
}