package mc.raytracer.geometry

import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

class Sphere(
        val center: Point3D,
        val radius: Double
    ): GeometricObject() {

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t >= K_EPSILON) {
            val temp = ray.origin - center

            return Hit(tmin = t,
                localHitPoint = ray.origin+ray.direction*t,
                normalAtHitPoint = Normal3D.fromVector((temp + ray.direction*t)/radius))
        }
        else {
            return Miss.instance
        }
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val t = findIntersection(shadowRay)
        return if (t >= K_EPSILON) t else null
    }

    /**
     * Returns value {@code t} such that {@code ray.origin + ray.direction*t}
     * is the nearest point on the sphere that intersects the ray.
     * Returns {@code 0.0} when no such point exists.
     */
    private fun findIntersection(ray: Ray): Double {
        val a = ray.direction.dot(ray.direction)

        val temp = ray.origin - center
        val b = 2.0 * temp.dot(ray.direction)
        val c = temp.dot(temp) - radius*radius

        // compute solutions to quadratic equation
        val delta = b*b - 4.0*a*c
        if (delta < 0.0)
            return 0.0

        val deltaSqrt = Math.sqrt(delta)

        // smaller root
        var t: Double = (-b - deltaSqrt) / (2.0*a)
        if (t >= K_EPSILON) {
            return t
        }

        // larger root
        t = (-b + deltaSqrt) / (2.0*a)
        if (t >= K_EPSILON) {
            return t
        }

        return 0.0
    }
}
