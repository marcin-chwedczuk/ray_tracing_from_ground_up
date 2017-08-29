package mc.raytracer.geometry.primitives.d3.part

import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Angle
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Ray

public class ConvexPartCylinder(
        radius: Double,
        yBottom: Double, yTop: Double,
        angleMin: Angle, angleMax: Angle)
    : PartCylinder(radius, yBottom, yTop, angleMin, angleMax) {

    override fun hit(ray: Ray): HitResult {
        val tHitPointPair = findHitPoint(ray)
        if (tHitPointPair === null)
            return Miss.instance

        val (t, hitPoint) = tHitPointPair
        val normal = Normal3D(hitPoint.x, 0.0, hitPoint.z)

        return Hit(
                tmin = t,
                localHitPoint = hitPoint,
                normalAtHitPoint = normal)
    }
}