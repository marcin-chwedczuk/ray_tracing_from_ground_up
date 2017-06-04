package mc.raytracer.world

import jdk.nashorn.internal.runtime.regexp.joni.Regex
import mc.raytracer.antialiasing.JitteredAntialiasingStrategy
import mc.raytracer.antialiasing.RandomAntialiasingStrategy
import mc.raytracer.antialiasing.RegularAntialiasingStrategy
import mc.raytracer.cameras.BaseCamera
import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Sphere
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.tracers.Tracer
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
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

    fun addObject(obj: GeometricObject) {
        objects.add(obj)
    }
}