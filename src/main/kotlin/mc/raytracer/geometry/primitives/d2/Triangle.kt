package mc.raytracer.geometry.primitives.d2

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray

/**
 * Represents a triangle. Points {@code v0}, {@code v1}, {@code v2}
 * are triangle vertices and should
 * be specified in counterclockwise manner.
 *
 * Normal is computed from vertices.
 */
public class Triangle(
        val v0: Point3D, val v1: Point3D, val v2: Point3D
): GeometricObject() {

    public val normal: Normal3D = Normal3D.fromVector((v1 - v0) cross (v2 - v0))

    override fun hit(ray: Ray): HitResult {
        val t = computeIntersection(ray)

        if (t === null)
            return Miss.instance

        // val n = if ((normal dot -ray.direction) >= 0.0) normal else -normal

        return Hit(
                tmin = t,
                normalAtHitPoint = normal,
                localHitPoint = ray.pointOnRayPath(t))
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        return computeIntersection(shadowRay)
    }

    private fun computeIntersection(ray: Ray): Double? {
        val a = v0.x - v1.x; val b = v0.x - v2.x; val c = ray.direction.x; val d = v0.x - ray.origin.x
        val e = v0.y - v1.y; val f = v0.y - v2.y; val g = ray.direction.y; val h = v0.y - ray.origin.y
        val i = v0.z - v1.z; val j = v0.z - v2.z; val k = ray.direction.z; val l = v0.z - ray.origin.z

        val m = f * k - g * j; val n = h * k - g * l; val p = f * l - h * j
        val q = g * i - e * k; val s = e * j - f * i

        val inv_denom  = 1.0 / (a * m + b * q + c * s)

        val e1 = d * m - b * n - c * p
        val beta = e1 * inv_denom

        if (beta < 0.0)
            return null

        val r = e * l - h * i
        val e2 = a * n + d * q + c * r
        val gamma = e2 * inv_denom

        if (gamma < 0.0 )
            return null

        if (beta + gamma > 1.0)
            return null

        val e3 = a * p - b * r + d * s
        val t = e3 * inv_denom

        if (t < K_EPSILON)
            return null

        return t
    }
}
