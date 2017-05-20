package mc.raytracer.geometry

import mc.raytracer.util.ShadingInfo

sealed class HitResult

class Hit(
    val tmin: Double,
    val shadeInfo: ShadingInfo)
        : HitResult()

class Miss: HitResult {
        private constructor()

        companion object {
            val instance = Miss()
        }
}

