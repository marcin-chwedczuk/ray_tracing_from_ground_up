package mc.raytracer.geometry

import mc.raytracer.material.Material
import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.Ray
import mc.raytracer.util.RgbColor

abstract class GeometricObject {
    open var material: Material =
        StaticColorMaterial(RgbColor.white)

    abstract fun hit(ray: Ray): HitResult

    companion object {
        val K_EPSILON = 0.0001
    }
}
