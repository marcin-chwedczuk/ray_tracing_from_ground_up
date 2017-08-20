package mc.raytracer.material.brdf

import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class GlossySpecularBRDF(
        val specularColor: RgbColor = RgbColor.white,
        val specularCoefficient: Double = 1.0,
        val specularExponent: Double = 300.0
): BRDFFunction {

    override fun evaluate(shadingInfo: ShadingInfo, incomingLight: Vector3D, outgoingLight: Vector3D): RgbColor {
        val incomingDotNormal = incomingLight dot shadingInfo.normalAtHitPoint
        val reflectedIncomingLightVector =
                (-incomingLight + shadingInfo.normalAtHitPoint * incomingDotNormal * 2.0)

        val rDotOutgoing = reflectedIncomingLightVector dot outgoingLight

        if (rDotOutgoing > 0.0) {
            return specularColor * Math.pow(rDotOutgoing, specularExponent) * specularCoefficient
        }

        return RgbColor.black
    }

    override fun rho(shadingInfo: ShadingInfo, outgoingLight: Vector3D): RgbColor {
        return RgbColor.black
    }

    override fun sample(shadingInfo: ShadingInfo, outgoingLight: Vector3D): Pair<RgbColor, Vector3D> {
        TODO("chapter 25")
    }

}