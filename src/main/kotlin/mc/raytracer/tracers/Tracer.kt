package mc.raytracer.tracers

import mc.raytracer.math.Ray
import mc.raytracer.util.RgbColor
import mc.raytracer.world.World

interface Tracer {
    fun init(world: World)

    fun traceRay(ray: Ray, depth: Int = 0): RgbColor
}
