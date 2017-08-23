package mc.raytracer.lighting

import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

/**
 * @param directionOfRays Direction of rays from light source to object surfaces.
 */
public class DirectionalLight(
        directionOfRays: Vector3D,
        val color: RgbColor = RgbColor.white,
        val radianceScalingFactor: Double = 1.0
): Light {

    private val reversedDirection = -directionOfRays.norm()
    public override val generatesShadows: Boolean = true

    override fun computeHitPointLightingAttributes(shadingInfo: ShadingInfo): HitPointLightingAttributes {
        return object : HitPointLightingAttributes {

            override val toLightDirection: Vector3D
                get() = reversedDirection

            override fun isHitPointInShadow(shadowRay: Ray): Boolean {
                return shadingInfo.world.existsCastingShadowObjectInDirection(shadowRay, maxDistance=Double.MAX_VALUE)
            }

            override fun radiance(): RgbColor {
                return color*radianceScalingFactor
            }

        }
    }
}