package mc.raytracer.geometry

import mc.raytracer.material.Material
import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.util.BoundingBox
import mc.raytracer.util.RgbColor

abstract class GeometricObject {
    open var material: Material =
        StaticColorMaterial(RgbColor.white)

    abstract val boundingBox: BoundingBox

    abstract fun hit(ray: Ray): HitResult
    abstract fun shadowHit(shadowRay: Ray): Double?

    fun newInstance() = InstanceBuilder(this)

    companion object {
        // TODO: @mc define this separately for each gemetric object
        val K_EPSILON = 0.0001
    }
}
