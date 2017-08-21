package mc.raytracer.geometry

import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

class Plane(
        val a: Point3D,
        val n: Normal3D)
    : GeometricObject() {


    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t >= K_EPSILON) {
            return Hit(tmin = t,
                localHitPoint=ray.origin + ray.direction*t,
                normalAtHitPoint = n)
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

        val t = (a-ray.origin).dot(n) / ray.direction.dot(n)
        return t
    }
}
