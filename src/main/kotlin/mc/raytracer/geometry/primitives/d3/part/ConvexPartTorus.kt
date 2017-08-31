package mc.raytracer.geometry.primitives.d3.part

import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Angle
import mc.raytracer.math.Ray

public class ConvexPartTorus(
        sweptRadius: Double,
        tubeRadius: Double,
        minSweptAngle: Angle, maxSweptAngle: Angle,
        minTubeAngle: Angle, maxTubeAngle: Angle)
    : PartTorus(sweptRadius, tubeRadius, minSweptAngle, maxSweptAngle, minTubeAngle, maxTubeAngle) {

    override fun hit(ray: Ray): HitResult {
        val tHitPointPair = findIntersection(ray)
        if (tHitPointPair === null)
            return Miss.instance

        val (t,hitPoint) = tHitPointPair
        val normal = computeNormalAtPoint(hitPoint)

        return Hit(
                tmin = t,
                localHitPoint = hitPoint,
                normalAtHitPoint = normal,
                material = material)
    }
}