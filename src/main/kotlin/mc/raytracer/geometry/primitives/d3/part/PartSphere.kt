package mc.raytracer.geometry.primitives.d3.part

import mc.raytracer.geometry.*
import mc.raytracer.math.*
import mc.raytracer.sampling.JitteredSampler
import mc.raytracer.sampling.UniformSphereSampler
import mc.raytracer.util.BoundingBox

/**
 * Represents a part sphere.
 *
 * @param minAzimutAngle Minimum value of the azimut angle (measured counterclockwise from Y axis in YZ plane).
 *  Must be in {@code 0..PI} range.
 * @param maxAzimutAngle Maximum value of the azimut angle (measured counterclockwise from Y axis in YZ plane).
 *  Must be in {@code 0..PI} range.
 * @param minAngle Minimum value of angle in XZ plane (measured counterclockwise). Value must be in {@code 0..2PI} range.
 * @param maxAngle Maximum value of angle in XZ plane (measured counterclockwise). Value must be in {@code 0..2PI} range.
 */
public open class PartSphere(
        val radius: Double,
        val minAzimutAngle: Angle,
        val maxAzimutAngle: Angle,
        val minAngle: Angle,
        val maxAngle: Angle
): GeometricObject(){

    private val minAzimutCos = minAzimutAngle.cos()
    private val maxAzimutCos = maxAzimutAngle.cos()

    override fun hit(ray: Ray): HitResult {
        val tHitPointPair = findIntersection(ray)
        if (tHitPointPair === null)
            return Miss.instance

        val (t,hitPoint) = tHitPointPair

        var normal = Normal3D(hitPoint.x, hitPoint.y, hitPoint.z)
        if ((-ray.direction dot normal) < 0.0)
            normal = -normal

        return Hit(tmin = t,
                localHitPoint = hitPoint,
                normalAtHitPoint = normal)
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val pair = findIntersection(shadowRay)
        return pair?.first
    }

    /**
     * Returns value {@code t} such that {@code ray.origin + ray.direction*t}
     * is the nearest point on the sphere that intersects the ray.
     * Returns {@code 0.0} when no such point exists.
     */
    protected fun findIntersection(ray: Ray): Pair<Double,Point3D>? {
        val a = ray.direction dot ray.direction

        val temp = ray.origin - Point3D.zero
        val b = 2.0 * (temp dot ray.direction)
        val c = (temp dot temp) - radius*radius

        // compute solutions to quadratic equation
        val delta = b*b - 4.0*a*c
        if (delta < 0.0)
            return null

        val deltaSqrt = Math.sqrt(delta)

        // order is important - smaller root first
        val solutions = arrayOf(
                (-b - deltaSqrt) / (2.0*a),
                (-b + deltaSqrt) / (2.0*a))

        for (t in solutions) {
            if (t < K_EPSILON) continue

            val hitPoint = ray.pointOnRayPath(t)

            val angle = Angle.fromAtan2(hitPoint.x, hitPoint.z)
            if (!angle.withInRange(minAngle, maxAngle)) {
                continue
            }

            // azimut angle check
            if ((maxAzimutCos*radius) <= hitPoint.y && hitPoint.y <= (minAzimutCos*radius)) {
                return Pair(t, hitPoint)
            }
        }

        return null
    }
}
