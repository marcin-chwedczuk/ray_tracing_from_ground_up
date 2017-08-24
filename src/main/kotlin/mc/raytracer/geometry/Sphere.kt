package mc.raytracer.geometry

import mc.raytracer.math.Normal3D
import mc.raytracer.math.PI
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.sampling.HemisphereSampler
import mc.raytracer.sampling.JitteredSampler
import mc.raytracer.sampling.UniformSphereSampler
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

class Sphere(
        val center: Point3D,
        val radius: Double,
        sampler: UniformSphereSampler? = null
    ): GeometricObject(), SupportsSurfaceSampling {

    private val invertedArea = 1 / (4 * PI * radius)

    private val sampler by lazy {
        sampler ?: UniformSphereSampler.fromSquareSampler(JitteredSampler())
    }

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t >= K_EPSILON) {
            val temp = ray.origin - center

            return Hit(tmin = t,
                localHitPoint = ray.origin+ray.direction*t,
                normalAtHitPoint = Normal3D.fromVector((temp + ray.direction*t)/radius))
        }
        else {
            return Miss.instance
        }
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val t = findIntersection(shadowRay)
        return if (t >= K_EPSILON) t else null
    }

    /**
     * Returns value {@code t} such that {@code ray.origin + ray.direction*t}
     * is the nearest point on the sphere that intersects the ray.
     * Returns {@code 0.0} when no such point exists.
     */
    private fun findIntersection(ray: Ray): Double {
        val a = ray.direction.dot(ray.direction)

        val temp = ray.origin - center
        val b = 2.0 * temp.dot(ray.direction)
        val c = temp.dot(temp) - radius*radius

        // compute solutions to quadratic equation
        val delta = b*b - 4.0*a*c
        if (delta < 0.0)
            return 0.0

        val deltaSqrt = Math.sqrt(delta)

        // smaller root
        var t: Double = (-b - deltaSqrt) / (2.0*a)
        if (t >= K_EPSILON) {
            return t
        }

        // larger root
        t = (-b + deltaSqrt) / (2.0*a)
        if (t >= K_EPSILON) {
            return t
        }

        return 0.0
    }

    override fun selectSamplePoint(): Point3D {
        return center + (sampler.nextSampleVectorOnUnitSphere() * radius)
    }

    override fun pdfOfSamplePoint(point: Point3D): Double {
        return invertedArea
    }

    override fun normalAtSamplePoint(point: Point3D): Normal3D {
        return Normal3D.fromVector(point - center)
    }
}
