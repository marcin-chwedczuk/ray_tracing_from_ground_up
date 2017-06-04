package mc.raytracer.tracers

import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.HitResult
import mc.raytracer.geometry.Miss
import mc.raytracer.math.Ray
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo
import mc.raytracer.world.World

class SimpleMultipleObjectsTracer : Tracer {
    lateinit var world: World

    override fun init(world: World) {
        this.world = world
    }

    override fun traceRay(ray: Ray, depth: Int): RgbColor {
        var tmin = Double.MAX_VALUE
        var info: ShadingInfo? = null

        for (obj in world.objects) {
            val hitResult = obj.hit(ray)
            if ((hitResult is Hit) && (hitResult.tmin < tmin)) {
                tmin = hitResult.tmin
                info = hitResult.shadeInfo
            }
        }

        if (info != null) {
            return info.material.shade(info)
        }

        return world.backgroundColor
    }
}
