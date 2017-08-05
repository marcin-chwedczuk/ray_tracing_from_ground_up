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

    /* Circle equation: (p-center) dot (p-center) = radius^2
     */

    override fun hit(ray: Ray): HitResult {
        val a = ray.direction.dot(ray.direction)

        val temp = ray.origin - center
        val b = 2.0 * temp.dot(ray.direction)
        val c = temp.dot(temp) - radius*radius

        // compute solutions to quadratic equation
        val delta = b*b - 4.0*a*c
        if (delta < 0.0)
            return Miss.instance

        val deltaSqrt = Math.sqrt(delta)

        // smaller root
        var t: Double = (-b - deltaSqrt) / (2.0*a)
        if (t > K_EPSILON) {
            return Hit(tmin = t,
                localHitPoint = ray.origin+ray.direction*t,
                normalAtHitPoint = Normal3D.fromVector((temp + ray.direction*t)/radius))
        }

        // larger root
        t = (-b + deltaSqrt) / (2.0*a)
        if (t > K_EPSILON) {
            return Hit(tmin = t,
                localHitPoint = ray.origin+ray.direction*t,
                normalAtHitPoint = Normal3D.fromVector((temp + ray.direction*t)/radius))
        }

        return Miss.instance
    }
}
