package mc.raytracer.geometry.primitives.d2

import mc.raytracer.geometry.*
import mc.raytracer.math.Normal3D
import mc.raytracer.math.PI
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.sampling.JitteredSampler
import mc.raytracer.util.BoundingBox
import mc.raytracer.util.LocalCoordinateSystem

/**
 * This is <em>one side</em> annulus.
 */
public class Annulus(
        val center: Point3D,
        val innerRadius: Double,
        val outerRadius: Double,
        val normal: Normal3D,
        sampler: CircleSampler? = null
): GeometricObject(), SupportsSurfaceSampling {

    init {
        if (innerRadius > outerRadius)
            throw IllegalArgumentException("innerRadius must be <= than outerRadius.")

        if (innerRadius < 0.0)
            throw IllegalArgumentException("innerRadius must be >= 0.")
    }

    private val sampler by lazy {
        sampler ?: CircleSampler.fromSquareSampler(JitteredSampler())
    }

    private val localCoordinateSystem by lazy {
        LocalCoordinateSystem.fromNormal(normal)
    }

    private val area = PI * (outerRadius * outerRadius - innerRadius * innerRadius)
    private val invertedArea = 1.0 / area
    private val radiusDelta = outerRadius - innerRadius

    private val _boundingBox = BoundingBox(
            center.x - outerRadius, center.x + outerRadius,
            center.y - outerRadius, center.y + outerRadius,
            center.z - outerRadius, center.z + outerRadius)

    override val boundingBox: BoundingBox
        get() = _boundingBox

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t == null)
            return Miss.instance

        return Hit(
                tmin = t,
                localHitPoint = ray.pointOnRayPath(t),
                normalAtHitPoint = normal,
                material = material)
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val t = findIntersection(shadowRay)
        return t
    }

    private fun findIntersection(ray: Ray): Double? {
        val t = ((center - ray.origin) dot normal) / (ray.direction dot normal)

        if (t <= K_EPSILON)
            return null

        val hitPoint = ray.pointOnRayPath(t)
        val distance = center.distanceTo(hitPoint)
        if (distance < innerRadius || distance > outerRadius)
            return null

        return t
    }

    override fun selectSamplePoint(): Point3D {
        val point2D = sampler.nextSampleOnUnitDisk()

        val len = Math.sqrt(point2D.x*point2D.x + point2D.y*point2D.y)

        val nx = point2D.x / len
        val ny = point2D.y / len

        val pointOnAnnulus = center +
                localCoordinateSystem.u * (innerRadius * nx + radiusDelta * point2D.x) +
                localCoordinateSystem.v * (innerRadius * ny + radiusDelta * point2D.y)

        return pointOnAnnulus
    }

    override fun pdfOfSamplePoint(point: Point3D): Double {
        return invertedArea
    }

    override fun normalAtSamplePoint(point: Point3D): Normal3D {
        return normal
    }
}