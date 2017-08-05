package mc.raytracer.world

import jdk.nashorn.internal.runtime.regexp.joni.Regex
import mc.raytracer.antialiasing.JitteredAntialiasingStrategy
import mc.raytracer.antialiasing.RandomAntialiasingStrategy
import mc.raytracer.antialiasing.RegularAntialiasingStrategy
import mc.raytracer.cameras.BaseCamera
import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Hit
import mc.raytracer.geometry.Sphere
import mc.raytracer.lighting.AmbientLight
import mc.raytracer.lighting.Light
import mc.raytracer.material.Material
import mc.raytracer.material.NullMaterial
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.tracers.Tracer
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo
import java.util.stream.IntStream
import java.util.stream.Stream
import java.util.stream.StreamSupport

class World(
        val viewPlane: ViewPlane,
        val backgroundColor: RgbColor,
        val tracer: Tracer
) {
    init {
        tracer.init(this)
    }

    val objects: ArrayList<GeometricObject> = ArrayList()

    val lights = mutableListOf<Light>()
    var ambientLight = AmbientLight(color=RgbColor.white)

    fun addObject(obj: GeometricObject) {
        objects.add(obj)
    }

    fun addLight(light: Light) {
        if (light is AmbientLight)
            throw IllegalArgumentException("Use world ambientLight property to set ambient light.")

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
}