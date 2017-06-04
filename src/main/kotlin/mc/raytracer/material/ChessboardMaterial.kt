package mc.raytracer.material

import mc.raytracer.math.PI_ON_180
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
        // add "irrational" number to avoid noise when object is put
        // on pattern boundary
        val delta = PI_ON_180 / 100.0

        val x = if (info.localHitPoint.x > 0) info.localHitPoint.x
                else patternSize - info.localHitPoint.x

        val y = if(info.localHitPoint.y > 0) info.localHitPoint.y
                else patternSize - info.localHitPoint.y

        val z = if (info.localHitPoint.z > 0) info.localHitPoint.z
                else patternSize - info.localHitPoint.z

        val xn = ((x+delta) / patternSize).toInt()
        val yn = ((y+delta) / patternSize).toInt()
        val zn = ((z+delta) / patternSize).toInt()

        val total = (xn%2) + (yn%2) + (zn%2)
        if ((total % 2) == 0)
            return color1

        return color2
    }
}
