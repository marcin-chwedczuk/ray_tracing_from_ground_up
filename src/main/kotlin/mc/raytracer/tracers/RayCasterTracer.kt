package mc.raytracer.tracers

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo
import mc.raytracer.world.World

class RayCasterTracer : Tracer {
    lateinit var world: World

    override fun init(world: World) {
        this.world = world
    }

    override fun traceRay(ray: Ray, depth: Int): RgbColor {
        val shadingInfo = world.tryHitObjects(ray, depth)

        if (shadingInfo.objectHit) {
            return shadingInfo.material.shade(shadingInfo)
        }
        else {
            return world.backgroundColor
        }
    }
}
