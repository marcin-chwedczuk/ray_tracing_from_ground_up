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
    ): Light {

    override var generatesShadows: Boolean = true

    override fun computeHitPointLightingAttributes(shadingInfo: ShadingInfo): HitPointLightingAttributes {
        return object : HitPointLightingAttributes {

            override val toLightDirection: Vector3D
                get() = (location - shadingInfo.hitPoint).norm()

            override fun isHitPointInShadow(shadowRay: Ray): Boolean {
                val maxDistance = shadowRay.origin.distanceTo(location)
                return shadingInfo.world.existsCastingShadowObjectInDirection(shadowRay, maxDistance)
            }

            override fun radiance(): RgbColor {
                val attenuation = when {
                    attenuationScalingFactor < K_EPSILON -> 1.0
                    else -> Math.pow(location.distanceTo(shadingInfo.hitPoint), attenuationScalingFactor)
                }

                return color*radianceScalingFactor / attenuation
            }

        }
    }
}