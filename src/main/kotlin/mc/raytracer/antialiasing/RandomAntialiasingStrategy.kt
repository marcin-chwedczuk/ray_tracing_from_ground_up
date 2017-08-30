package mc.raytracer.antialiasing

import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.tracers.Tracer
import mc.raytracer.util.RgbColor
import java.util.*

class RandomAntialiasingStrategy(val numOfSamples: Int, val pixelSize: Double) {
    private val rnd = Random()

    fun antialias(pixelLeft: Double, pixelTop: Double, tracer: Tracer): RgbColor {
        val vecZ = Vector3D(0,0,-1)
        var accColor = RgbColor.black

        for (row in 1..numOfSamples) {
                val x = pixelLeft + pixelSize*rnd.nextDouble()
                val y = pixelTop + pixelSize*rnd.nextDouble()
                val z = 1000.0

                val ray = Ray.create(Point3D(x,y,z), vecZ)
                accColor += tracer.traceRay(ray)
        }

        return accColor / numOfSamples.toDouble()
    }
}
