package mc.raytracer.cameras

import mc.raytracer.math.*
import mc.raytracer.math.degToRad
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.World

class FishEyeCamera(bitmap: RawBitmap): BaseCamera(bitmap) {
    private var maxPsi = 90.0

    var fieldOfViewInDegrees: Double
        get() = maxPsi*2.0
        set(v) { maxPsi = v/2.0 }

    var zoom: Double = 1.0

    fun render(world: World) {
        val viewPlane = world.viewPlane
        val tracer = world.tracer
        val pixelSize = viewPlane.pixelSize / zoom

        val hres = viewPlane.horizontalResolution
        val vres = viewPlane.verticalResolution

        //IntStream.range(0, vres).parallel()
        //        .forEach { r ->
        for (r in 0 until vres) {
            for (c in 0 until hres) {

                var L = RgbColor.black

                for (sample in 1..viewPlane.numerOfSamplesPerPixel) {
                    val p = viewPlane.sampler.nextSample()

                    val x = pixelSize * (c - hres / 2.0 + p.x)
                    val y = pixelSize * (vres / 2.0 - r + p.y)

                    val direction = computeRayDirection(
                            x,y, vres, hres, pixelSize)

                    // point no in "fisheye" view
                    if (direction == null)
                        continue

                    val ray = Ray(eye, direction)

                    L += tracer.traceRay(ray)
                }
                L /= viewPlane.numerOfSamplesPerPixel.toDouble()
                L *= exposureTime

                displayPixel(viewPlane, r, c, L)
            }
        }
    }

    private fun computeRayDirection(
            x: Double, y: Double,
            vres: Int, hres: Int,
            pixelSize: Double): Vector3D? {

        val minRes = Math.min(vres, hres)

        // map point (x,y) on view plane to device
        // independent coordinates [-1;1]x[-1;1]
        val nx = x * 2.0 / (pixelSize * minRes)
        val ny = y * 2.0 / (pixelSize * minRes)

        val r2 = nx*nx + ny*ny

        if (r2 <= 1.0) {
            // map point on plane to point on sphere
            val r = Math.sqrt(r2)
            val psi = r * maxPsi.degToRad()

            val sinPsi = Math.sin(psi)
            val cosPsi = Math.cos(psi)

            val sinAlpha = ny / r
            val cosAlpha = nx / r

            val direction = sinPsi*cosAlpha*u +
                    sinPsi*sinAlpha*v +
                    -cosPsi*w

            return direction
        }

        return null
    }
}
