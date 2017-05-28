package mc.raytracer.cameras

import mc.raytracer.geometry.GeometricObject.Companion.K_EPSILON
import mc.raytracer.math.Ray
import mc.raytracer.math.*
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.ViewPlane
import mc.raytracer.world.World

class PinholeCamera(canvas: RawBitmap): BaseCamera(canvas) {

    var viewPlaneDistance: Double = 500.0
    var fieldOfViewInDegrees: Double = 0.0
        set(value) {
            if (value <= 0.0 || value >= 180.0)
                throw IllegalArgumentException("field of view must be withing 0-180 degrees range.")

            field = value
        }

    var zoom: Double = 1.0

    fun render(world: World) {
        val viewPlane = world.viewPlane
        val tracer = world.tracer
        val pixelSize = viewPlane.pixelSize / zoom

        val hres = viewPlane.horizontalResolution
        val vres = viewPlane.verticalResolution

        val dist = getViewPlaneDistance(vres)

        //IntStream.range(0, vres).parallel()
        //        .forEach { r ->
        for (r in 0 until vres) {
            for (c in 0 until hres) {

                var L = RgbColor.black

                for (sample in 1..viewPlane.numerOfSamplesPerPixel) {
                    val p = viewPlane.sampler.nextSample()

                    val x = pixelSize * (c - hres / 2.0 + p.x)
                    val y = pixelSize * (vres / 2.0 - r + p.y)

                    val direction = x*u + y*v - dist*w
                    val ray = Ray(eye, direction)

                    L += tracer.traceRay(ray)
                }
                L /= viewPlane.numerOfSamplesPerPixel.toDouble()
                L *= exposureTime

                displayPixel(viewPlane, r,c,L)
            }
        }
    }

    private fun getViewPlaneDistance(verticalResolution: Int): Double {
        if (fieldOfViewInDegrees < K_EPSILON)
            return viewPlaneDistance

        val dist = verticalResolution / Math.tan(fieldOfViewInDegrees.degToRad()/2.0)
        return dist
    }
}
