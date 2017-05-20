package mc.raytracer.tracers

import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Ray
import mc.raytracer.util.RgbColor
import mc.raytracer.world.World

class SingleSphereTracer: Tracer {
    lateinit var world: World

    override fun init(world: World) {
        this.world = world
    }

    override fun traceRay(ray: Ray, depth: Int): RgbColor {
        if (world.sphere.hit(ray) is Hit)
            return RgbColor.red
        return world.backgroundColor
    }
}
