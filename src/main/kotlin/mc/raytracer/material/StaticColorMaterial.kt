package mc.raytracer.material

import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

class StaticColorMaterial(val color: RgbColor): Material() {
    override fun shade(info: ShadingInfo): RgbColor {
        return color
    }

    override fun areaLightShade(info: ShadingInfo): RgbColor {
        return shade(info)
    }
}
