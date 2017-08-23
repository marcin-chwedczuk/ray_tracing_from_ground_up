package mc.raytracer.geometry.primitives.d2

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.*

public class Disc(
        val center: Point3D,
        val normal: Normal3D,
        val radius: Double
): GeometricObject() {

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t == null)
            return Miss.instance

        // reverse normal if we are looking at the disk from the other side
        val n = if (-ray.direction dot normal > 0.0) normal else -normal

        return Hit(
                tmin = t,
                localHitPoint = ray.origin + t*ray.direction,
                normalAtHitPoint = n)
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val t = findIntersection(shadowRay)
        return t
    }

    private fun findIntersection(ray: Ray): Double? {
        val t = ((center - ray.origin) dot normal) / (ray.direction dot normal)

        if (t <= K_EPSILON)
            return null

        val p = ray.origin + t*ray.direction
        if (center.distanceTo(p) > radius)
            return null

        return t
    }
}


