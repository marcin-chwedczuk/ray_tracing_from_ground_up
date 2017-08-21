package mc.raytracer.world

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.lighting.AmbientLight
import mc.raytracer.lighting.Light
import mc.raytracer.lighting.LightWithShadowSupport
import mc.raytracer.material.NullMaterial
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.tracers.Tracer
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

class World(
        val viewPlane: ViewPlane,
        val backgroundColor: RgbColor,
        val tracer: Tracer
) {
    init {
        tracer.init(this)
    }

    val objects: ArrayList<GeometricObject> = ArrayList()

    val lights = mutableListOf<LightWithShadowSupport>()
    var ambientLight = AmbientLight(color=RgbColor.white)

    fun addObject(obj: GeometricObject) {
        objects.add(obj)
    }

    fun addLight(light: LightWithShadowSupport) {
        lights.add(light)
    }

    public fun tryHitObjects(ray: Ray, depth: Int): ShadingInfo {
        var tmin = Double.MAX_VALUE
        var hitMin: Hit? = null
        var objectMin: GeometricObject? = null

        for (obj in objects) {
            val hitResult = obj.hit(ray)

            if ((hitResult is Hit) && (hitResult.tmin < tmin)) {
                tmin = hitResult.tmin
                hitMin = hitResult
                objectMin = obj
            }
        }

        if (hitMin != null) {
            return ShadingInfo(
                    objectHit = true,
                    hitPoint = ray.origin + ray.direction*tmin,
                    localHitPoint = hitMin.localHitPoint,
                    normalAtHitPoint = hitMin.normalAtHitPoint,
                    material = objectMin!!.material,
                    areaLightsDirection = Vector3D.zero,
                    ray = ray,
                    recursionDepth = depth,
                    world = this)
        }

        return ShadingInfo(
                objectHit = false,
                hitPoint = Point3D.zero,
                localHitPoint = Point3D.zero,
                normalAtHitPoint = Normal3D.axisY,
                material = NullMaterial.instance,
                areaLightsDirection = Vector3D.zero,
                ray = ray,
                recursionDepth = depth,
                world = this)
    }

    public fun existsObjectInDirection(shadowRay: Ray, maxDistance: Double): Boolean {
        for (obj in objects) {
            if (!obj.material.castsShadows) continue

            val t = obj.shadowHit(shadowRay)
            if ((t !== null) && (t <= maxDistance)) {
                return true
            }
        }

        return false
    }
}