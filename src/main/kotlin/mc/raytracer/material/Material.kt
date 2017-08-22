package mc.raytracer.material

import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public abstract class Material {
    var castsShadows: Boolean = true

    abstract fun shade(info: ShadingInfo): RgbColor
    abstract fun areaLightShade(info:ShadingInfo): RgbColor
}

public class NullMaterial private constructor(): Material() {
    override fun shade(info: ShadingInfo): RgbColor {
        throw IllegalStateException("NullMaterial cannot shade anything.")
    }

    override fun areaLightShade(info: ShadingInfo): RgbColor {
        throw IllegalStateException("NullMaterial cannot shade anything.")
    }

    companion object {
        val instance = NullMaterial()
    }
}