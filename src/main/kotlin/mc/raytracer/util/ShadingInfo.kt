package mc.raytracer.util

import mc.raytracer.material.Material
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.world.World

class ShadingInfo(
    val objectHit: Boolean,

    /**
     * Hit point in world coordinates.
     */
    val hitPoint: Point3D,

    /**
     * Hit point in local coordinates (for textures).
     */
    val localHitPoint: Point3D,

    /**
     * Normal at hit point in world coordinates.
     */
    val normalAtHitPoint: Normal3D,

    val material: Material,

    val areaLightsDirection: Vector3D,

    val ray: Ray,
    val recursionDepth: Int,

    val world: World
)
