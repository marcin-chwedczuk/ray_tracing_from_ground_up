package mc.raytracer.geometry.primitives.d3.part

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.*

/**
 * Represents an part cylinder.
 *
 * Reverses normal when ray hits inside of the object.
 *
 * @param angleMin Angle in range @{code 0..2PI} (measured counterclockwise from Z axis in ZX plane) at which cylinder starts.
 * @param angleMax Angle in range @{code 0..2PI} (measured counterclockwise from Z axis in ZX plane) at which cylinder ends.
 */
open class PartCylinder(
        val radius: Double,
        val yBottom: Double,
        val yTop: Double,
        val angleMin: Angle,
        val angleMax: Angle
): GeometricObject() {

    override fun hit(ray: Ray): HitResult {
        val tHitPointPair = findHitPoint(ray)
        if (tHitPointPair === null)
            return Miss.instance

        val (t, hitPoint) = tHitPointPair

        var normal = Normal3D(hitPoint.x, 0.0, hitPoint.z)
        if ((-ray.direction dot normal) < 0.0)
            normal = -normal

        return Hit(
                tmin = t,
                localHitPoint = hitPoint,
                normalAtHitPoint = normal)
    }

    override fun shadowHit(ray: Ray): Double? {
        val tHitPointPair = findHitPoint(ray)
        if (tHitPointPair === null)
            return null

        return tHitPointPair.first
    }

    protected fun findHitPoint(ray: Ray): Pair<Double,Point3D>? {
        val ox = ray.origin.x
        val oz = ray.origin.z
        val dx = ray.direction.x
        val dz = ray.direction.z

        val a = dx * dx + dz * dz
        val b = 2.0 * (ox * dx + oz * dz)
        val c = ox * ox + oz * oz - radius * radius
        val delta = b * b - 4.0 * a * c

        if (delta < 0.0)
            return null

        val deltaSqrt = Math.sqrt(delta)
        val _2a = 2.0 * a

        // order is important - smaller root first
        val solutions = arrayOf(
                (-b - deltaSqrt) / _2a,
                (-b + deltaSqrt) / _2a)

        for (t in solutions) {
            if (t < K_EPSILON) continue

            val hitPoint = ray.pointOnRayPath(t)

            var angle = Math.atan2(hitPoint.x, hitPoint.z)
            if (angle < 0.0) {
                angle += 2*PI
            }

            if ((hitPoint.y in yBottom..yTop) &&
                (angleMin.toRadians() <= angle && angle <= angleMax.toRadians())) {

                return Pair(t, hitPoint)
            }
        }

        return null
    }
}