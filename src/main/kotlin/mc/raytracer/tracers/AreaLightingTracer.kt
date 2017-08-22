package mc.raytracer.tracers

import mc.raytracer.math.Ray
import mc.raytracer.util.RgbColor
import mc.raytracer.world.World

class AreaLightingTracer: Tracer {
    lateinit var world: World

    override fun init(world: World) {
        this.world = world
    }

    override fun traceRay(ray: Ray, depth: Int): RgbColor {
        val shadingInfo = world.tryHitObjects(ray, depth)

        if (shadingInfo.objectHit) {
            return shadingInfo.material.areaLightShade(shadingInfo)
        }
        else {
            return world.backgroundColor
        }
    }
}
