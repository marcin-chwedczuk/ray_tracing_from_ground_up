package mc.raytracer.material

import mc.raytracer.material.brdf.GlossySpecularBRDF
import mc.raytracer.material.brdf.LambertianBRDF
import mc.raytracer.util.*
import mc.raytracer.util.ShadingInfo

public class PhongMaterial(
    color: RgbColor,
    ambientCoefficient: Double = 0.10,
    diffuseCoefficient: Double = 1.0,
    specularCoefficient: Double = 1.0,
    specularExponent: Double = 300.0
): Material {

    private var ambientBrdf = LambertianBRDF(color, ambientCoefficient)
    private var diffuseBrdf = LambertianBRDF(color, diffuseCoefficient)
    private var specularBrdf = GlossySpecularBRDF(
            specularCoefficient = specularCoefficient, specularExponent = specularExponent)

    override fun shade(info: ShadingInfo): RgbColor {
        val world = info.world

        val wo = -info.ray.direction
        var L = world.ambientLight.computeLuminanceContributedByLight(info)
                .multiplyComponentwise(ambientBrdf.rho(info, wo))

        for(light in world.lights) {
            val wi = light.computeDirectionFromHitPointToLight(info)
            val ndotwi = info.normalAtHitPoint.dot(wi)

            if (ndotwi > 0.0) {
                val brdf =
                        diffuseBrdf.evaluate(info, wo, wi) +
                        specularBrdf.evaluate(info, wo, wi)

                L += brdf.multiplyComponentwise(
                        light.computeLuminanceContributedByLight(info)) * ndotwi
            }
        }

        return L
    }
}
