package mc.raytracer.geometry.primitives.d2.part

import mc.raytracer.geometry.*
import mc.raytracer.math.*
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.sampling.JitteredSampler
import mc.raytracer.util.LocalCoordinateSystem

public class PartAnnulus(
        val innerRadius: Double,
        val outerRadius: Double,
        val minAngle: Angle,
        val maxAngle: Angle
): GeometricObject() {

    val normal = Normal3D(0,1,0)

    init {
        if (innerRadius > outerRadius)
            throw IllegalArgumentException("innerRadius must be <= than outerRadius.")

        if (innerRadius < 0.0)
            throw IllegalArgumentException("innerRadius must be >= 0.")
    }

    private val radiusDelta = outerRadius - innerRadius

    override fun hit(ray: Ray): HitResult {
        val tHitPointPair = findIntersection(ray)
        if (tHitPointPair == null)
            return Miss.instance

        val (t, hitPoint) = tHitPointPair
        val normal = if (-ray.direction dot normal > 0.0) normal else -normal

        return Hit(
                tmin = t,
                localHitPoint = hitPoint,
                normalAtHitPoint = normal)
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val t = findIntersection(shadowRay)
        return t?.first
    }

    private fun findIntersection(ray: Ray): Pair<Double,Point3D>? {
        val t = ((Point3D.zero - ray.origin) dot normal) / (ray.direction dot normal)

        if (t <= K_EPSILON)
            return null

        val hitPoint = ray.pointOnRayPath(t)

        // test distance
        val distance = Point3D.zero.distanceTo(hitPoint)
        if (distance < innerRadius || distance > outerRadius)
            return null

        // test angle
        val angle = Angle.fromAtan2(hitPoint.z, hitPoint.x)
        if (!angle.withInRange(minAngle, maxAngle))
            return null

        return Pair(t,hitPoint)
    }
}