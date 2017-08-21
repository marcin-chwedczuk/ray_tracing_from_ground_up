package mc.raytracer.geometry

import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.*
import mc.raytracer.util.BoundingBox
import java.lang.Math.max
import java.lang.Math.min
import mc.raytracer.math.Vector3D
import mc.raytracer.util.ShadingInfo

class Rectangle2D(
        val point: Point3D,
        val spanA: Vector3D,
        val spanB: Vector3D)
    : GeometricObject() {

    val normal = (spanA cross spanB).norm()

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t <= K_EPSILON)
            return Miss.instance

        return Hit(tmin = t,
                localHitPoint = Point3D.zero + ((ray.origin + ray.direction*t) - point),
                normalAtHitPoint = Normal3D.fromVector(normal))
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val t = findIntersection(shadowRay)
        return if (t >= K_EPSILON) t else null
    }

    private fun findIntersection(ray: Ray): Double {
        val p0 = point; val a = spanA; val b = spanB
        val t = ((p0 - ray.origin) dot normal) / (ray.direction dot normal)

        if (t <= K_EPSILON)
            return 0.0

        val p = ray.origin + t*ray.direction
        val d = p - p0

        val ddota = d dot a
        if (ddota < 0.0 || ddota > a.lengthSquared)
            return 0.0

        val ddotb = d dot b
        if (ddotb < 0.0 || ddotb > b.lengthSquared)
            return 0.0

        return t
    }
}
