package mc.raytracer.lighting

import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.sampling.HemisphereSampler
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class AmbientOccluder(
        val color: RgbColor = RgbColor.white,
        val sampler: HemisphereSampler,
        val radianceScalingFactor: Double = 1.0,
        val minRadience: Double = 0.25
): LightWithShadowSupport {
    override val castsShadows: Boolean
        get() = false

    override fun isHitPointInShadow(shadingInfo: ShadingInfo, shadowRay: Ray): Boolean {
        TODO("not implemented")
    }

    override fun computeDirectionFromHitPointToLight(shadingInfo: ShadingInfo): Vector3D {
        TODO("not implemented")
    }

    override fun computeLuminanceContributedByLight(shadingInfo: ShadingInfo): RgbColor {
        // construct local coordinate system
        val w = Vector3D(shadingInfo.normalAtHitPoint)
        val v = (w cross Vector3D(0.0072, 1.0, 0.0034)).norm()
        val u = v cross w

        var L = 0.0

        // see: http://www.rorydriscoll.com/2009/01/07/better-sampling/

        // number of samples may be set on view plane or here
        // we may boost it a little
        val SAMPLE_NO = 1 // 64
        for (sampleNo in 1..SAMPLE_NO) {
            val sample = sampler.nextVectorOnUnitHemispehere()
            val rayDirection = (v * sample.x + w * sample.y + u * sample.z).norm()
            val shadowRay = Ray(shadingInfo.hitPoint, rayDirection)

            if (shadingInfo.world.existsCastingShadowObjectInDirection(shadowRay)) {
                L += radianceScalingFactor * minRadience
            } else {
                L += radianceScalingFactor
            }
        }

        return color * (L /  SAMPLE_NO)
    }

}
