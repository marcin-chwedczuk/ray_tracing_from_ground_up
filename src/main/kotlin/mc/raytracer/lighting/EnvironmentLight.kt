package mc.raytracer.lighting

import mc.raytracer.material.EmissiveMaterial
import mc.raytracer.math.*
import mc.raytracer.sampling.HemisphereSampler
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

/**
 * Light from virtual sphere that surrounds scene (aka sky).
 */
public class EnvironmentLight(
        val material: EmissiveMaterial,
        val sampler: HemisphereSampler): LightWithShadowSupport {

    override var castsShadows: Boolean = true

    private var toLight: Vector3D = Vector3D.zero

    override fun computeDirectionFromHitPointToLight(shadingInfo: ShadingInfo): Vector3D {
        // construct local coordinate system
        val w = Vector3D(shadingInfo.normalAtHitPoint)
        val v = (w cross Vector3D(0.0072, 1.0, 0.0034)).norm()
        val u = v cross w

        val sample = sampler.nextVectorOnUnitHemispehere()

        toLight = (v * sample.x + w * sample.y + u * sample.z).norm()
        return toLight
    }

    override fun isHitPointInShadow(shadingInfo: ShadingInfo, shadowRay: Ray): Boolean {
        return shadingInfo.world.existsCastingShadowObjectInDirection(shadowRay, maxDistance=Double.MAX_VALUE)
    }

    override fun computeLuminanceContributedByLight(shadingInfo: ShadingInfo): RgbColor {
        return material.getRadiance()
    }

    override fun geometricFactor(info: ShadingInfo): Double {
        return 1.0
    }

    override fun monteCarloPdf(info: ShadingInfo): Double {
        val ndotd = toLight dot info.normalAtHitPoint
        return ndotd / PI
    }

}