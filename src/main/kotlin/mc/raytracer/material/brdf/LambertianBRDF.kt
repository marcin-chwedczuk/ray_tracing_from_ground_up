package mc.raytracer.material.brdf

import mc.raytracer.math.INV_PI
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

/**
 * @param diffuseCoefficient fraction of light reflected by object
 */
public class LambertianBRDF(
        val diffuseColor: RgbColor,
        val diffuseCoefficient: Double = 1.0)
    : BRDFFunction {

    init {
        if (diffuseCoefficient < 0 || diffuseCoefficient > 1.0)
            throw IllegalArgumentException(
                    "diffuseCoefficient must have value from range [0,1] but was $diffuseCoefficient")
    }

    override fun evaluate(shadingInfo: ShadingInfo, incomingLight: Vector3D, outgoingLight: Vector3D): RgbColor {
        return diffuseColor * diffuseCoefficient * INV_PI
    }

    override fun rho(shadingInfo: ShadingInfo, outgoingLight: Vector3D): RgbColor {
        return diffuseColor * diffuseCoefficient
    }

    override fun sample(shadingInfo: ShadingInfo, outgoingLight: Vector3D): Pair<RgbColor, Vector3D> {
        TODO("waiting for chapter 26")
    }
}
