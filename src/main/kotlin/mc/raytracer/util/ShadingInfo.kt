package mc.raytracer.util

import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D

class ShadingInfo(
    val objectHit: Boolean,
    val localHitPoint: Point3D,
    val normalAtHitPoint: Normal3D,
    val color: RgbColor
)
