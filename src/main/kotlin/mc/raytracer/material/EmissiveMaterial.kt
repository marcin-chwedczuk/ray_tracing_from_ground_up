package mc.raytracer.material

import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class EmissiveMaterial(
        val color: RgbColor,
        val radianceScalingFactor: Double = 1.0
): Material() {

    init {
        castsShadows = false
    }

    override fun shade(info: ShadingInfo): RgbColor {
        // on the same side as normal
        if (-info.ray.direction dot info.normalAtHitPoint > 0.0) {
            return color * radianceScalingFactor
        }

        return RgbColor.pink
    }

    override fun areaLightShade(info: ShadingInfo): RgbColor {
        return shade(info)
    }

    public fun getRadiance(): RgbColor {
        return color * radianceScalingFactor
    }
}