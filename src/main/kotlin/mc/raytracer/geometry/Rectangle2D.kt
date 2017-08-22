package mc.raytracer.geometry

import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.*
import mc.raytracer.util.BoundingBox
import java.lang.Math.max
import java.lang.Math.min
import mc.raytracer.math.Vector3D
import mc.raytracer.sampling.JitteredSampler
import mc.raytracer.sampling.SquareSampler
import mc.raytracer.util.ShadingInfo

class Rectangle2D(
        val point: Point3D,
        val spanA: Vector3D,
        val spanB: Vector3D,
        sampler: SquareSampler? = null)
    : GeometricObject(), SupportsSurfaceSampling {

    private val normal = Normal3D.fromVector(spanA cross spanB)
    private val sampler = sampler ?: JitteredSampler()
    private val invertedArea = 1.0 / (spanA cross spanB).length

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t <= K_EPSILON)
            return Miss.instance

        // TODO: reverse normal
        // val n = if ((-ray.direction dot normal) >= 0.0) normal else -normal

        return Hit(tmin = t,
                localHitPoint = Point3D.zero + ((ray.origin + ray.direction*t) - point),
                normalAtHitPoint = normal)
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

    override fun samplePoint(): Point3D {
        val sample = sampler.nextSampleOnUnitSquare()
        val point = point + spanA*sample.x + spanB*sample.y
        return point
    }

    override fun getPdfOfSample(point: Point3D): Double {
        return invertedArea
    }

    override fun getNormalAtPoint(point: Point3D): Normal3D {
        return normal
    }
}
