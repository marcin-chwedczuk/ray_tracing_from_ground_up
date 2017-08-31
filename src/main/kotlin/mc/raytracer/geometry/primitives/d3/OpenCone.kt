package mc.raytracer.geometry.primitives.d3

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.EquationSolver
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.util.BoundingBox

public class OpenCone(
        val height: Double,
        val radius: Double
): GeometricObject() {

    private val h2 = height * height
    private val r2 = radius * radius

    private val _boundingBox = BoundingBox.fromMinMaxPoints(
            Point3D(-radius, 0.0, -radius),
            Point3D(radius, height, radius))

    override val boundingBox: BoundingBox
        get() = _boundingBox

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t === null) return Miss.instance

        val hitPoint = ray.pointOnRayPath(t)
        var normal = computeNormal(hitPoint)

        if (normal dot -ray.direction < 0.0)
            normal = -normal

        return Hit(
                tmin = t,
                localHitPoint = hitPoint,
                normalAtHitPoint = normal,
                material = material)
    }

    private fun computeNormal(hitPoint: Point3D): Normal3D {
        val hr = height / radius

        return Normal3D(
                hitPoint.x*hr*hr,
                height - hitPoint.y,
                hitPoint.z*hr*hr)
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        return findIntersection(shadowRay)
    }

    private fun findIntersection(ray: Ray): Double? {
        val ox = ray.origin.x; val oy = ray.origin.y; val oz = ray.origin.z
        val dx = ray.direction.x; val dy = ray.direction.y; val dz = ray.direction.z

        val A = (h2/r2 * (dx*dx + dz*dz)) - dy*dy
        val B = (h2/r2 * (2*ox*dx + 2*oz*dz)) - 2*dy*(oy- height)
        val C = (h2/r2 * (ox*ox + oz*oz)) - (oy- height)*(oy- height)

        val solutions = EquationSolver.solveX2(listOf(C, B, A))

        val t = solutions
                .filter { it > K_EPSILON }
                .filter {
                    val py = oy + it*dy
                    val withinRange = (py > 0.0) && (py < height)
                    withinRange
                }
                .min()

        if (t === null)
            return null

        return t
    }
}
