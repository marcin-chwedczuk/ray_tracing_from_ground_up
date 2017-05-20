package mc.raytracer.geometry

import mc.raytracer.math.Ray

abstract class GeometricObject {
    abstract fun hit(ray: Ray): HitResult

    companion object {
        val K_EPSILON = 0.0001
    }
}
