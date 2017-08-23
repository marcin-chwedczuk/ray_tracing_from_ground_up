package mc.raytracer.lighting

import mc.raytracer.material.EmissiveMaterial
import mc.raytracer.math.*
import mc.raytracer.sampling.HemisphereSampler
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

/**
 * BasicLight from virtual sphere that surrounds scene (aka sky).
 */
public class EnvironmentLight(
        val material: EmissiveMaterial,
        val sampler: HemisphereSampler): Light {

    public override var generatesShadows: Boolean = true

    override fun computeHitPointLightingAttributes(shadingInfo: ShadingInfo): HitPointLightingAttributes {
        // construct local coordinate system
        val w = Vector3D(shadingInfo.normalAtHitPoint)
        val v = (w cross Vector3D(0.0072, 1.0, 0.0034)).norm()
        val u = v cross w

        val sample = sampler.nextVectorOnUnitHemispehere()
        val toLight = (v * sample.x + w * sample.y + u * sample.z).norm()

        return object : HitPointLightingAttributes {
            override val toLightDirection: Vector3D
                get() = toLight

            override fun isHitPointInShadow(shadowRay: Ray): Boolean {
                return shadingInfo.world.existsCastingShadowObjectInDirection(shadowRay)
            }

            override fun radiance(): RgbColor {
                return material.getRadiance()
            }

            override fun samplePointGeometricFactor(): Double {
                return 1.0
            }

            override fun samplePointPdf(): Double {
                val ndotd = toLight dot shadingInfo.normalAtHitPoint
                return ndotd / PI
            }
        }
    }
}