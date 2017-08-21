package mc.raytracer.lighting

import mc.raytracer.geometry.GeometricObject.Companion.K_EPSILON
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class PointLight(
        val location: Point3D,
        val color: RgbColor = RgbColor.white,
        val radianceScalingFactor: Double = 1.0,
        val attenuationScalingFactor: Double = 0.0
    ): LightWithShadowSupport {

    override var castsShadows: Boolean = true

    override fun computeDirectionFromHitPointToLight(shadingInfo: ShadingInfo): Vector3D {
        return (location - shadingInfo.hitPoint).norm()
    }

    override fun computeLuminanceContributedByLight(shadingInfo: ShadingInfo): RgbColor {
        val attenuation = when {
            attenuationScalingFactor < K_EPSILON -> 1.0
            else -> Math.pow(location.distanceTo(shadingInfo.hitPoint), attenuationScalingFactor)
        }

        return color*radianceScalingFactor / attenuation
    }

    override fun isHitPointInShadow(shadingInfo: ShadingInfo, shadowRay: Ray): Boolean {
        val maxDistance = shadowRay.origin.distanceTo(location)
        return shadingInfo.world.existsObjectInDirection(shadowRay, maxDistance)
    }
}