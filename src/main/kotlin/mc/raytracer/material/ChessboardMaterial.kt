package mc.raytracer.material

import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo
import org.w3c.dom.css.RGBColor

// This is simplified pattern - only works for planes in XZ plane
class ChessboardMaterial(
        val color1: RgbColor,
        val color2: RgbColor,
        val patternSize: Double = 1.0)
        : Material {

    override fun shade(info: ShadingInfo): RgbColor {
        val x = if (info.localHitPoint.x > 0) info.localHitPoint.x
                else patternSize - info.localHitPoint.x

        val y = if(info.localHitPoint.y > 0) info.localHitPoint.y
                else patternSize - info.localHitPoint.y

        val z = if (info.localHitPoint.z > 0) info.localHitPoint.z
                else patternSize - info.localHitPoint.z

        val xn = (x / patternSize).toInt()
        val yn = (y / patternSize).toInt()
        val zn = (z / patternSize).toInt()

        val total = (xn%2) + (yn%2) + (zn%2)
        if ((total % 2) == 0)
            return color1

        return color2
    }
}
