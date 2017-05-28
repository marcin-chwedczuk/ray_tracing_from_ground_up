package mc.raytracer.cameras

import mc.raytracer.math.Point2D
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.util.RawBitmap
import mc.raytracer.math.*
import mc.raytracer.util.RgbColor
import mc.raytracer.world.World

class ThinLensCamera(
        canvas: RawBitmap,
        val lensSampler: CircleSampler)
    : BaseCamera(canvas) {

    var lensRadius: Double = 1.0
    var viewPlaneDistance: Double = 500.0

    // focal plane is the plane on which camera focus
    var focalPlaneDistance: Double = 800.0
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

                    val lensPoint = lensRadius*lensSampler.nextSampleOnUnitDisk()

                    val rayOrigin = eye + lensPoint.x*u + lensPoint.y*v
                    val rayDirection = rayDirection(Point2D(x,y), lensPoint)
                    val ray = Ray(rayOrigin, rayDirection)

                    L += tracer.traceRay(ray)
                }
                L /= viewPlane.numerOfSamplesPerPixel.toDouble()
                L *= exposureTime

                displayPixel(viewPlane, r,c,L)
            }
        }
    }

    private fun rayDirection(viewPlanePoint: Point2D, lensPoint: Point2D): Vector3D {
        // compute focal point corresponding to lens point
        val x = viewPlanePoint.x * focalPlaneDistance/viewPlaneDistance
        val y = viewPlanePoint.y * focalPlaneDistance/viewPlaneDistance

        // compute ((x,y) - lensPoint) vector
        val direction = (x - lensPoint.x)*u +
                (y - lensPoint.y)*v +
                -focalPlaneDistance*w

        return direction.norm()
    }
}
