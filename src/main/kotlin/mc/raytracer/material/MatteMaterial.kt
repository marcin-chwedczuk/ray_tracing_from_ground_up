package mc.raytracer.material

import mc.raytracer.material.brdf.LambertianBRDF
import mc.raytracer.math.Ray
import mc.raytracer.util.*
import mc.raytracer.util.ShadingInfo

public class MatteMaterial(
    color: RgbColor,
    ambientCoefficient: Double = 0.10,
    diffuseCoefficient: Double = 1.0
): Material() {

    private var ambientBrdf = LambertianBRDF(color, ambientCoefficient)
    private var diffuseBrdf = LambertianBRDF(color, diffuseCoefficient)

    override fun shade(info: ShadingInfo): RgbColor {
        val world = info.world

        val wo = -info.ray.unitDirection
        var L = world.ambientLight.radiance(info) * ambientBrdf.rho(info, wo)

        for(light in world.lights) {
            val lightningAttributes = light.computeHitPointLightingAttributes(info)

            val wi = lightningAttributes.toLightDirection
            val ndotwi = info.normalAtHitPoint.dot(wi)

            if (ndotwi > 0.0) {
                var hitPointInShadow = false

                if (light.generatesShadows) {
                    val shadowRay = Ray.create(info.hitPoint, wi)
                    hitPointInShadow = lightningAttributes.isHitPointInShadow(shadowRay)
                }

                if (!hitPointInShadow) {
                    L += (lightningAttributes.radiance() * diffuseBrdf.evaluate(info, wo, wi) * ndotwi)
                }
            }
        }

        return L
    }

    override fun areaLightShade(info: ShadingInfo): RgbColor {
        val world = info.world

        val wo = -info.ray.unitDirection
        var L = world.ambientLight.radiance(info) * ambientBrdf.rho(info, wo)

        for(light in world.lights) {
            val lightningAttributes = light.computeHitPointLightingAttributes(info)

            val wi = lightningAttributes.toLightDirection
            val ndotwi = info.normalAtHitPoint.dot(wi)

            if (ndotwi > 0.0) {
                var hitPointInShadow = false

                if (light.generatesShadows) {
                    val shadowRay = Ray.create(info.hitPoint, wi)
                    hitPointInShadow = lightningAttributes.isHitPointInShadow(shadowRay)
                }

                if (!hitPointInShadow) {
                    val dL = lightningAttributes.radiance() *
                            diffuseBrdf.evaluate(info, wo, wi) *
                            ndotwi *
                            lightningAttributes.samplePointGeometricFactor() /
                            lightningAttributes.samplePointPdf()
                    L = L + dL
                }
            }
        }

        return L
    }

}
