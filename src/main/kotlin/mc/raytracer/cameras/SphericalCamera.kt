package mc.raytracer.cameras

import mc.raytracer.math.*
import mc.raytracer.math.degToRad
import mc.raytracer.threading.CancelFlag
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.World

class SphericalCamera: BaseCamera() {

    // ration of fov's must be the same as bitmap ration
    var horizontalFieldOfViewInDegrees: Double = 90.0
    var verticalFieldOfViewInDegrees: Double = 90.0

    override fun render(world: World, canvas: RawBitmap, cancelFlag: CancelFlag) {
        val viewPlane = world.viewPlane
        val tracer = world.tracer
        val pixelSize = viewPlane.pixelSize

        val hres = viewPlane.horizontalResolution
        val vres = viewPlane.verticalResolution

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

                    val direction = computeRayDirection(
                            x,y, vres, hres, pixelSize)

                    val ray = Ray.create(eye, direction)
                    L += tracer.traceRay(ray)
                }
                L /= viewPlane.numberOfSamplesPerPixel.toDouble()
                L *= exposureTime

                viewPlane.displayPixel(canvas, r, c, L)
            }
        }
    }

    private fun computeRayDirection(
            x: Double, y: Double,
            vres: Int, hres: Int,
            pixelSize: Double): Vector3D {

        // map point (x,y) on view plane to device
        // independent coordinates [-1;1]x[-1;1]
        val nx = x * 2.0 / (pixelSize * hres)
        val ny = y * 2.0 / (pixelSize * vres)

        val lambda = nx * horizontalFieldOfViewInDegrees.degToRad()/2.0
        val psi = ny * verticalFieldOfViewInDegrees.degToRad()/2.0

        // convert to azimuth and polar angles
        val phi = PI - lambda
        val theta = 0.5 * PI - psi

        val sinPhi = Math.sin(phi)
        val cosPhi = Math.cos(phi)

        val sinTheta = Math.sin(theta)
        val cosTheta = Math.cos(theta)

        val direction = sinTheta*sinPhi*u + cosTheta*v + sinTheta*cosPhi*w
        return direction
    }
}
