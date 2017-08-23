package mc.raytracer.lighting

import mc.raytracer.math.Angle
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class SpotLight(
        val location: Point3D,
        val raysDirection: Vector3D,
        val beamAngle: Angle,
        val color: RgbColor,
        val radianceScalingFactor: Double = 1.0,
        val cutOffExponent: Double = 500.0
): Light {

    private val raysDirectionReversed = -raysDirection.norm()
    private val cosOfBeamAngle = beamAngle.cos()

    public override var generatesShadows: Boolean = true

    override fun computeHitPointLightingAttributes(shadingInfo: ShadingInfo): HitPointLightingAttributes {
        return object : HitPointLightingAttributes {

            override val toLightDirection: Vector3D by lazy {
                (location - shadingInfo.hitPoint).norm()
            }

            override fun isHitPointInShadow(shadowRay: Ray): Boolean {
                val maxDistance = shadowRay.origin.distanceTo(location)
                return shadingInfo.world.existsCastingShadowObjectInDirection(shadowRay, maxDistance)
            }

            override fun radiance(): RgbColor {
                val toLightNormalCos = raysDirectionReversed dot toLightDirection

                if (toLightNormalCos > cosOfBeamAngle) {
                    return color * radianceScalingFactor
                }

                val angleToSpotlightCone =
                        Math.acos(toLightNormalCos) - beamAngle.toRadians()

                val tmp = Math.cos(angleToSpotlightCone)
                if (tmp <= 0.0) return RgbColor.black

                return color * radianceScalingFactor * Math.pow(tmp, cutOffExponent)

            }
        }
    }
}