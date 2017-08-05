package mc.raytracer.material.brdf

import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class GlossySpecularBRDF: BRDFFunction {
    override fun evaluate(shadingInfo: ShadingInfo, incomingLight: Vector3D, outgoingLight: Vector3D): RgbColor {
        TODO("chapter 15")
    }

    override fun rho(shadingInfo: ShadingInfo, outgoingLight: Vector3D): RgbColor {
        return RgbColor.black
    }

    override fun sample(shadingInfo: ShadingInfo, outgoingLight: Vector3D): Pair<RgbColor, Vector3D> {
        TODO("chapter 25")
    }

}