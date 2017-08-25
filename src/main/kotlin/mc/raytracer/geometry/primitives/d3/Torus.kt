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

class Torus(
        val position: Point3D,
        val ringRadius: Double,
        val tubeRadius: Double = 1.0
    ) : GeometricObject() {

    private val boundingBox = BoundingBox(
            -ringRadius-tubeRadius + position.x, ringRadius+tubeRadius + position.x,
            -tubeRadius + position.y, tubeRadius + position.y,
            -ringRadius-tubeRadius + position.z, ringRadius+tubeRadius + position.z)


    public fun computeNormalAtPoint(p: Point3D): Normal3D {
        val paramSquared = ringRadius*ringRadius + tubeRadius*tubeRadius

        // @mc: I added -position.xyz
        val x = p.x - position.x
        val y = p.y - position.y
        val z = p.z - position.z
        val sumSquared = x * x + y * y + z * z

        val normal = Normal3D(
            4.0 * x * (sumSquared - paramSquared),
            4.0 * y * (sumSquared - paramSquared + 2.0*ringRadius*ringRadius),
            4.0 * z * (sumSquared - paramSquared))

        return normal
    }

    override fun hit(ray: Ray): HitResult {
        val t = findIntersection(ray)

        if (t === null)
            return Miss.instance

        return Hit(
            tmin = t,
            localHitPoint = ray.origin + ray.direction*t,
            normalAtHitPoint = computeNormalAtPoint(ray.origin + ray.direction*t))
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val t = findIntersection(shadowRay)
        return t
    }

    private fun findIntersection(ray: Ray): Double? {
        if (!boundingBox.isIntersecting(ray))
            return null

        // @mc: I added -position.xyz
        val x1 = ray.origin.x - position.x
        val y1 = ray.origin.y - position.y
        val z1 = ray.origin.z - position.z

        val d1 = ray.direction.x
        val d2 = ray.direction.y
        val d3 = ray.direction.z

        // define the coefficients of the quartic equation
        val sum_d_sqrd 	= d1 * d1 + d2 * d2 + d3 * d3
        val e			= x1 * x1 + y1 * y1 + z1 * z1 - ringRadius*ringRadius - tubeRadius*tubeRadius
        val f			= x1 * d1 + y1 * d2 + z1 * d3
        val four_a_sqrd	= 4.0 * ringRadius*ringRadius

        val coeffs = listOf(
                e * e - four_a_sqrd * (tubeRadius*tubeRadius - y1 * y1),
                4.0 * f * e + 2.0 * four_a_sqrd * y1 * d2,
                2.0 * sum_d_sqrd * e + 4.0 * f * f + four_a_sqrd * d2 * d2,
                4.0 * sum_d_sqrd * f,
                sum_d_sqrd * sum_d_sqrd)

        val solution = EquationSolver.solveX4(coeffs)

        // ray misses the torus
        if (solution.isEmpty())
            return null

        // find the smallest root greater than kEpsilon, if any
        // the roots array is not sorted
        val t = solution
                .filter { it > K_EPSILON }
                .min()

        return t
    }
}
