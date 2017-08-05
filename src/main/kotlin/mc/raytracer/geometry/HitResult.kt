package mc.raytracer.geometry

import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.util.ShadingInfo

sealed class HitResult

class Hit(
        val tmin: Double,
        val localHitPoint: Point3D,
        val normalAtHitPoint: Normal3D)
        : HitResult()

class Miss: HitResult {
        private constructor()

        companion object {
            val instance = Miss()
        }
}

