package mc.raytracer.material

import mc.raytracer.material.brdf.GlossySpecularBRDF
import mc.raytracer.material.brdf.LambertianBRDF
import mc.raytracer.math.Ray
import mc.raytracer.util.*
import mc.raytracer.util.ShadingInfo

public class PhongMaterial(
    color: RgbColor,
    ambientCoefficient: Double = 0.10,
    diffuseCoefficient: Double = 1.0,
    specularCoefficient: Double = 1.0,
    specularExponent: Double = 300.0
): Material() {

    private var ambientBrdf = LambertianBRDF(color, ambientCoefficient)
    private var diffuseBrdf = LambertianBRDF(color, diffuseCoefficient)
    private var specularBrdf = GlossySpecularBRDF(
            specularCoefficient = specularCoefficient, specularExponent = specularExponent)

    override fun shade(info: ShadingInfo): RgbColor {
        val world = info.world

        val wo = -info.ray.direction
        var L = world.ambientLight.radiance(info) * ambientBrdf.rho(info, wo)

        for(light in world.lights) {
            val lightningAttributes = light.computeHitPointLightingAttributes(info)

            val wi = lightningAttributes.toLightDirection
            val ndotwi = info.normalAtHitPoint.dot(wi)

            if (ndotwi > 0.0) {
                var hitPointInShadow = false

                if (light.generatesShadows) {
                    val shadowRay = Ray(info.hitPoint, wi)
                    hitPointInShadow = lightningAttributes.isHitPointInShadow(shadowRay)
                }

                if (!hitPointInShadow) {
                    val brdf =
                            diffuseBrdf.evaluate(info, wo, wi) +
                            specularBrdf.evaluate(info, wo, wi)

                    val dL = brdf * lightningAttributes.radiance() * ndotwi
                    L += dL
                }
            }
        }

        return L
    }

    override fun areaLightShade(info: ShadingInfo): RgbColor {
        val world = info.world

        val wo = -info.ray.direction
        var L = world.ambientLight.radiance(info) * ambientBrdf.rho(info, wo)

        for(light in world.lights) {
            val lightingAttributes = light.computeHitPointLightingAttributes(info)

            val wi = lightingAttributes.toLightDirection
            val ndotwi = info.normalAtHitPoint.dot(wi)

            if (ndotwi > 0.0) {
                var hitPointInShadow = false

                if (light.generatesShadows) {
                    val shadowRay = Ray(info.hitPoint, wi)
                    hitPointInShadow = lightingAttributes.isHitPointInShadow(shadowRay)
                }

                if (!hitPointInShadow) {
                    val brdf =
                            diffuseBrdf.evaluate(info, wo, wi) +
                            specularBrdf.evaluate(info, wo, wi)

                    val dL = lightingAttributes.radiance() *
                            brdf *
                            ndotwi *
                            lightingAttributes.samplePointGeometricFactor() /
                            lightingAttributes.samplePointPdf()
                    L += dL
                }
            }
        }

        return L
    }

}
