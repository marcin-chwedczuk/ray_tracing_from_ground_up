package mc.raytracer.material

import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

interface Material {
    fun shade(info: ShadingInfo): RgbColor
}