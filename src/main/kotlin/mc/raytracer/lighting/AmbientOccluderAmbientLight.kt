package mc.raytracer.lighting

import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.sampling.HemisphereSampler
import mc.raytracer.util.LocalCoordinateSystem
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo
import java.lang.IllegalStateException

public class AmbientOccluderAmbientLight(
        val color: RgbColor = RgbColor.white,
        val sampler: HemisphereSampler,
        val radianceScalingFactor: Double = 1.0,
        val minRadience: Double = 0.25
): AmbientLight {

    override fun radiance(shadingInfo: ShadingInfo): RgbColor {
        val localCoords = LocalCoordinateSystem.fromNormal(shadingInfo.normalAtHitPoint)

        // see: http://www.rorydriscoll.com/2009/01/07/better-sampling/

        // number of samples may be set on view plane or here
        // we may boost it a little
        val sample = sampler.nextVectorOnUnitHemispehere()
        val rayDirection = (localCoords.v*sample.x + localCoords.w*sample.y + localCoords.u*sample.z).norm()
        val shadowRay = Ray.create(shadingInfo.hitPoint, rayDirection)

        if (shadingInfo.world.existsCastingShadowObjectInDirection(shadowRay)) {
            return color * radianceScalingFactor * minRadience
        } else {
            return color * radianceScalingFactor
        }
    }

}
