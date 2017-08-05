package mc.raytracer.material

import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

interface Material {
    fun shade(info: ShadingInfo): RgbColor
}

public class NullMaterial private constructor(): Material {
    override fun shade(info: ShadingInfo): RgbColor {
        throw IllegalStateException("NullMaterial cannot shade anything.")
    }

    companion object {
        val instance = NullMaterial()
    }
}