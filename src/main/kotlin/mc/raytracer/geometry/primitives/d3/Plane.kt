package mc.raytracer.geometry.primitives.d3

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.util.BoundingBox

class Plane(
        val a: Point3D,
        val normal: Normal3D)
    : GeometricObject() {

    override val boundingBox: BoundingBox
        get() = BoundingBox.INFINITE

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t >= K_EPSILON) {
            return Hit(tmin = t,
                    localHitPoint = ray.origin + ray.direction * t,
                    normalAtHitPoint = normal,
                    material = material)
        }

        return Miss.instance
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val t = findIntersection(shadowRay)
        return if (t >= K_EPSILON) t else null
    }

    private fun findIntersection(ray: Ray): Double {
        // Plane equation: p belongs to Plane
        // when (p-a) dot n = 0.

        val t = (a-ray.origin).dot(normal) / ray.direction.dot(normal)
        return t
    }
}
