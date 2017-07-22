package mc.raytracer.cameras

import mc.raytracer.geometry.GeometricObject.Companion.K_EPSILON
import mc.raytracer.math.Ray
import mc.raytracer.math.*
import mc.raytracer.threading.CancelFlag
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.World

class PinholeCamera : BaseCamera() {

    var viewPlaneDistance: Double = 500.0
    var fieldOfViewInDegrees: Double = 0.0
        set(value) {
            if (value <= 0.0 || value >= 180.0)
                throw IllegalArgumentException("field of view must be withing 0-180 degrees range.")

            field = value
        }

    var zoom: Double = 1.0

    override fun render(world: World, canvas: RawBitmap, cancelFlag: CancelFlag) {
        val viewPlane = world.viewPlane
        val tracer = world.tracer
        val pixelSize = viewPlane.pixelSize / zoom

        val hres = viewPlane.horizontalResolution
        val vres = viewPlane.verticalResolution

        val dist = getViewPlaneDistance(vres*pixelSize)

        //IntStream.range(0, vres).parallel()
        //        .forEach { r ->
        for (r in 0 until vres) {
            if (cancelFlag.shouldCancel) return

            for (c in 0 until hres) {

                var L = RgbColor.black

                for (sample in 1..viewPlane.numberOfSamplesPerPixel) {
                    val p = viewPlane.sampler.nextSampleOnUnitSquare()

                    val x = pixelSize * (c - hres / 2.0 + p.x)
                    val y = pixelSize * (vres / 2.0 - r + p.y)

                    val direction = x * u + y * v - dist * w
                    val ray = Ray(eye, direction)

                    L += tracer.traceRay(ray)
                }
                L /= viewPlane.numberOfSamplesPerPixel.toDouble()
                L *= exposureTime

                viewPlane.displayPixel(canvas, r, c, L)
            }
        }
    }

    override fun renderStereo(world: World, canvas: RawBitmap, cancelFlag: CancelFlag,
                              viewPortOffsetX: Double, viewPortOffsetY: Double,
                              canvasOffsetX: Int, canvasOffsetY: Int) {
        val viewPlane = world.viewPlane
        val tracer = world.tracer
        val pixelSize = viewPlane.pixelSize / zoom

        val hres = viewPlane.horizontalResolution
        val vres = viewPlane.verticalResolution

        val dist = getViewPlaneDistance(vres*pixelSize)

        //IntStream.range(0, vres).parallel()
        //        .forEach { r ->
        for (r in 0 until vres) {
            if (cancelFlag.shouldCancel) return

            for (c in 0 until hres) {

                var L = RgbColor.black

                for (sample in 1..viewPlane.numberOfSamplesPerPixel) {
                    val p = viewPlane.sampler.nextSampleOnUnitSquare()

                    val x = pixelSize * (c - hres / 2.0 + p.x) + viewPortOffsetX
                    val y = pixelSize * (vres / 2.0 - r + p.y) + viewPortOffsetY

                    val direction = x * u + y * v - dist * w
                    val ray = Ray(eye, direction)

                    L += tracer.traceRay(ray)
                }
                L /= viewPlane.numberOfSamplesPerPixel.toDouble()
                L *= exposureTime

                viewPlane.displayPixel(canvas, r+canvasOffsetY, c+canvasOffsetX, L)
            }
        }
    }

    private fun getViewPlaneDistance(viewPlaneWidth: Double): Double {
        if (fieldOfViewInDegrees < K_EPSILON)
            return viewPlaneDistance

        val dist = viewPlaneWidth / Math.tan(fieldOfViewInDegrees.degToRad() / 2.0)
        return dist
    }
}
