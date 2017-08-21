package mc.raytracer.lighting

import mc.raytracer.math.Angle
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class SpotLight(
        val location: Point3D,
        val lookAt: Vector3D,
        val innerCutOff: Angle,
        val color: RgbColor,
        val radianceScalingFactor: Double = 1.0,
        val cutOffExponent: Double = 500.0
): LightWithShadowSupport {

    override var castsShadows: Boolean = true

    private val reversedNormalizedLookAt = -lookAt.norm()

    private val innerCutOffCos = innerCutOff.cos()

    override fun computeDirectionFromHitPointToLight(shadingInfo: ShadingInfo): Vector3D {
        return (location - shadingInfo.hitPoint).norm()
    }

    override fun computeLuminanceContributedByLight(shadingInfo: ShadingInfo): RgbColor {
        val toLight = computeDirectionFromHitPointToLight(shadingInfo)
        val toLightNormalCos = reversedNormalizedLookAt dot toLight

        if (toLightNormalCos > innerCutOffCos) {
            return color * radianceScalingFactor
        }

        val angleToSpotlightCone =
                Math.acos(toLightNormalCos) - innerCutOff.toRadians()

        val tmp = Math.cos(angleToSpotlightCone)
        if (tmp <= 0.0) return RgbColor.black

        return color * radianceScalingFactor * Math.pow(tmp, cutOffExponent)
    }

    override fun isHitPointInShadow(shadingInfo: ShadingInfo, shadowRay: Ray): Boolean {
        val maxDistance = shadowRay.origin.distanceTo(location)
        return shadingInfo.world.existsObjectInDirection(shadowRay, maxDistance)
    }
}