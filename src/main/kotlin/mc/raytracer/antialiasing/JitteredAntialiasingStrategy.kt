package mc.raytracer.antialiasing

import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.tracers.Tracer
import mc.raytracer.util.RgbColor
import java.util.*

class JitteredAntialiasingStrategy(val samplesPerPixelSide: Int, val pixelSize: Double) {
    init {
        if (samplesPerPixelSide < 1)
            throw IllegalArgumentException("samplesPerPixelSide must be at least 1.")
    }

    private val rnd = Random()

    fun antialias(pixelLeft: Double, pixelTop: Double, tracer: Tracer): RgbColor {
        val sampleSize = pixelSize / samplesPerPixelSide
        val vecZ = Vector3D(0,0,-1)

        var accColor = RgbColor.black

        for (row in 1..samplesPerPixelSide) {
            for (col in 1..samplesPerPixelSide) {
                val x = pixelLeft + sampleSize*(row - rnd.nextDouble())
                val y = pixelTop + sampleSize*(col - rnd.nextDouble())
                val z = 1000.0

                val ray = Ray(Point3D(x,y,z), vecZ)
                accColor += tracer.traceRay(ray)
            }
        }

        return accColor / (samplesPerPixelSide*samplesPerPixelSide).toDouble()
    }
}
