package mc.raytracer.material

import mc.raytracer.material.brdf.LambertianBRDF
import mc.raytracer.util.*
import mc.raytracer.util.ShadingInfo

public class MatteMaterial(
    color: RgbColor,
    ambientCoefficient: Double = 0.10,
    diffuseCoefficient: Double = 1.0
): Material {

    private var ambientBrdf = LambertianBRDF(color, ambientCoefficient)
    private var diffuseBrdf = LambertianBRDF(color, diffuseCoefficient)

    override fun shade(info: ShadingInfo): RgbColor {
        val world = info.world

        val wo = -info.ray.direction
        var L = world.ambientLight.computeLuminanceContributedByLight(info)
                .multiplyComponentwise(ambientBrdf.rho(info, wo))

        for(light in world.lights) {
            val wi = light.computeDirectionFromHitPointToLight(info)
            val ndotwi = info.normalAtHitPoint.dot(wi)

            if (ndotwi > 0.0)
                L += diffuseBrdf.evaluate(info, wo, wi).multiplyComponentwise(
                        light.computeLuminanceContributedByLight(info)) * ndotwi
        }

        return L
    }
}
