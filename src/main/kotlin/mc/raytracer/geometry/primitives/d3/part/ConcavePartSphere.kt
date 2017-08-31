package mc.raytracer.geometry.primitives.d3.part

import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Angle
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Ray

public class ConcavePartSphere(
        radius: Double,
        minAzimutAngle: Angle, maxAzimutAngle: Angle,
        minAngle: Angle, maxAngle: Angle)
    : PartSphere(radius, minAzimutAngle, maxAzimutAngle, minAngle, maxAngle) {

    override fun hit(ray: Ray): HitResult {
        val tHitPointPair = findIntersection(ray)
        if (tHitPointPair === null)
            return Miss.instance

        val (t,hitPoint) = tHitPointPair
        val normal = Normal3D(-hitPoint.x, -hitPoint.y, -hitPoint.z)

        return Hit(tmin = t,
                localHitPoint = hitPoint,
                normalAtHitPoint = normal,
                material = material)
    }
}