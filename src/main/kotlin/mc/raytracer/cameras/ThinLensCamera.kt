package mc.raytracer.cameras

import mc.raytracer.math.Point2D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.math.times
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.threading.CancelFlag
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.World

class ThinLensCamera(val lensSampler: CircleSampler)
    : BaseCamera() {

    var lensRadius: Double = 14.0
    var viewPlaneDistance: Double = 500.0

    // focal plane is the plane on which camera focus
    var focalPlaneDistance: Double = 800.0
    var zoom: Double = 1.0

    override fun render(world: World, canvas: RawBitmap, cancelFlag: CancelFlag) {
        val viewPlane = world.viewPlane
        val tracer = world.tracer
        val pixelSize = viewPlane.pixelSize / zoom

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

                    val lensPoint = lensRadius*lensSampler.nextSampleOnUnitDisk()

                    val rayOrigin = eye + lensPoint.x*u + lensPoint.y*v
                    val rayDirection = rayDirection(Point2D(x,y), lensPoint)
                    val ray = Ray.create(rayOrigin, rayDirection)

                    L += tracer.traceRay(ray)
                }
                L /= viewPlane.numberOfSamplesPerPixel.toDouble()
                L *= exposureTime

                viewPlane.displayPixel(canvas, r,c,L)
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
