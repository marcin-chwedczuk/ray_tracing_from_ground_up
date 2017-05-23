package mc.raytracer.world

import jdk.nashorn.internal.runtime.regexp.joni.Regex
import mc.raytracer.antialiasing.JitteredAntialiasingStrategy
import mc.raytracer.antialiasing.RandomAntialiasingStrategy
import mc.raytracer.antialiasing.RegularAntialiasingStrategy
import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.Sphere
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.tracers.Tracer
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor

class World(
        val viewPlane: ViewPlane,
        val backgroundColor: RgbColor,
        val tracer: Tracer,
        val paintArea: RawBitmap
) {
    init {
        tracer.init(this)
    }

    val objects: ArrayList<GeometricObject> = ArrayList()

    fun addObject(obj: GeometricObject) {
        objects.add(obj)
    }

    fun renderScene() {
        val pixelSize = viewPlane.pixelSize
        val vecZ = Vector3D(0.0,0.0,-1.0)

        val hres = viewPlane.horizontalResolution
        val vres = viewPlane.verticalResolution

        for (r in 0 until vres) {
            for (c in 0 until hres) {

                var pixelColor = RgbColor.black

                for (sample in 1..viewPlane.numerOfSamplesPerPixel) {

                    val p = viewPlane.sampler.nextSample()

                    val x = pixelSize * (c - hres / 2.0 + p.x)
                    val y = pixelSize * (vres / 2.0 - r + p.y)
                    val z = 280.0

                    val ray = Ray(Point3D(x, y, z), vecZ)
                    pixelColor += tracer.traceRay(ray)
                }
                pixelColor /= viewPlane.numerOfSamplesPerPixel.toDouble()

                displayPixel(r,c,pixelColor)
            }
        }
    }

    private fun displayPixel(vpRow: Int, vpCol: Int, color: RgbColor) {
        var colorToDisplay =
            if (viewPlane.showOutOfGamutErrors)
                color.orWhenOutOfRange(RgbColor.red)
            else
                color.scaleMaxToOne()

        if (viewPlane.gamma != 1.0)
           colorToDisplay = colorToDisplay.powComponents(viewPlane.gammaInv)

        val argb = colorToDisplay.toInt()

        // synchronized(paintArea) {
            paintArea.setPixel(vpRow, vpCol, argb)
        // }
    }
}