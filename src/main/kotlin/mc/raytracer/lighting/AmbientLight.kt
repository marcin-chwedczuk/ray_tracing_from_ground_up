package mc.raytracer.lighting

import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public interface AmbientLight {
    fun radiance(shadingInfo: ShadingInfo): RgbColor
}
