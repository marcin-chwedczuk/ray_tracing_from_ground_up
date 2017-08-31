package mc.raytracer.geometry.primitives.d2

import mc.raytracer.geometry.*
import mc.raytracer.math.*
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.sampling.JitteredSampler
import mc.raytracer.util.BoundingBox
import mc.raytracer.util.LocalCoordinateSystem

public class Disc(
        val center: Point3D,
        val normal: Normal3D,
        val radius: Double,
        sampler: CircleSampler? = null
): GeometricObject(), SupportsSurfaceSampling {

    private val sampler by lazy {
        sampler ?: CircleSampler.fromSquareSampler(JitteredSampler())
    }

    private val localCoordinateSystem by lazy {
        LocalCoordinateSystem.fromNormal(normal)
    }

    private val area = PI * radius * radius
    private val invertedArea = 1.0 / area

    private val _boundingBox = BoundingBox(
            center.x - radius, center.x + radius,
            center.y - radius, center.y + radius,
            center.z - radius, center.z + radius)
    
    override val boundingBox: BoundingBox
        get() = _boundingBox

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t == null)
            return Miss.instance

        // reverse normal if we are looking at the disk from the other side
        // val n = if (-ray.direction dot normal > 0.0) normal else -normal
        // don't revers when used as area light

        return Hit(
                tmin = t,
                localHitPoint = ray.origin + t*ray.direction,
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

        val p = ray.origin + t*ray.direction
        if (center.distanceTo(p) > radius)
            return null

        return t
    }

    override fun selectSamplePoint(): Point3D {
        val point2D = sampler.nextSampleOnUnitDisk()

        val pointOnDisk = center +
                localCoordinateSystem.u * point2D.x * radius +
                localCoordinateSystem.v * point2D.y * radius

        return pointOnDisk
    }

    override fun pdfOfSamplePoint(point: Point3D): Double {
        return invertedArea
    }

    override fun normalAtSamplePoint(point: Point3D): Normal3D {
        return normal
    }
}


