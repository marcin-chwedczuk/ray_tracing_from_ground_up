package mc.raytracer.geometry.primitives.d3.part

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.*
import mc.raytracer.util.BoundingBox

open class PartTorus(
        val sweptRadius: Double,
        val tubeRadius: Double,
        val minSweptAngle: Angle,
        val maxSweptAngle: Angle,
        val minTubeAngle: Angle,
        val maxTubeAngle: Angle
) : GeometricObject() {

    private val boundingBox = BoundingBox(
            -sweptRadius - tubeRadius, sweptRadius + tubeRadius,
            -tubeRadius, tubeRadius,
            -sweptRadius - tubeRadius, sweptRadius + tubeRadius)

    public fun computeNormalAtPoint(p: Point3D): Normal3D {
        val paramSquared = sweptRadius*sweptRadius + tubeRadius*tubeRadius

        val x = p.x
        val y = p.y
        val z = p.z
        val sumSquared = x * x + y * y + z * z

        val normal = Normal3D(
                4.0 * x * (sumSquared - paramSquared),
                4.0 * y * (sumSquared - paramSquared + 2.0*sweptRadius*sweptRadius),
                4.0 * z * (sumSquared - paramSquared))

        return normal
    }

    override fun hit(ray: Ray): HitResult {
        val tHitPointPair = findIntersection(ray)
        if (tHitPointPair === null)
            return Miss.instance

        val (t,hitPoint) = tHitPointPair

        var normal = computeNormalAtPoint(hitPoint)
        if ((-ray.direction dot normal) < 0.0)
            normal = -normal

        return Hit(
                tmin = t,
                localHitPoint = hitPoint,
                normalAtHitPoint = normal)
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        val t = findIntersection(shadowRay)
        return t?.first
    }

    protected fun findIntersection(ray: Ray): Pair<Double,Point3D>? {
        if (!boundingBox.isIntersecting(ray))
            return null

        // @mc: I added -position.xyz
        val x1 = ray.origin.x
        val y1 = ray.origin.y
        val z1 = ray.origin.z

        val d1 = ray.direction.x
        val d2 = ray.direction.y
        val d3 = ray.direction.z

        // define the coefficients of the quartic equation
        val sum_d_sqrd 	= 1.0 // d1 * d1 + d2 * d2 + d3 * d3
        val e			= x1 * x1 + y1 * y1 + z1 * z1 - sweptRadius * sweptRadius - tubeRadius*tubeRadius
        val f			= x1 * d1 + y1 * d2 + z1 * d3
        val four_a_sqrd	= 4.0 * sweptRadius * sweptRadius

        val coeffs = listOf<Double>(
                e * e - four_a_sqrd * (tubeRadius*tubeRadius - y1 * y1),
                4.0 * f * e + 2.0 * four_a_sqrd * y1 * d2,
                2.0 * sum_d_sqrd * e + 4.0 * f * f + four_a_sqrd * d2 * d2,
                4.0 * sum_d_sqrd * f,
                sum_d_sqrd * sum_d_sqrd)

        val solutions = EquationSolver.solveX4(coeffs)

        // ray misses the torus
        if (solutions.isEmpty())
            return null

        for (t in solutions.sorted()) {
            if (t < K_EPSILON) continue

            val hitPoint = ray.pointOnRayPath(t)

            val angle = Angle.fromAtan2(hitPoint.x, hitPoint.z)
            if (!angle.withInRange(minSweptAngle, maxSweptAngle)) {
                continue
            }

            val toCenter = Normal3D(hitPoint.x, 0.0, hitPoint.z)
            val rx = (hitPoint - Point3D.zero - toCenter*sweptRadius) dot toCenter
            val ry = hitPoint.y
            val angleY = Angle.fromAtan2(rx, ry)
            if (!angleY.withInRange(minTubeAngle, maxTubeAngle)) {
                continue
            }

            return Pair(t, hitPoint)
        }

        return null
    }
}
