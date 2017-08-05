package mc.raytracer

import mc.raytracer.cameras.BaseCamera
import mc.raytracer.cameras.PinholeCamera
import mc.raytracer.cameras.StereoCamera
import mc.raytracer.geometry.Cuboid
import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.Sphere
import mc.raytracer.lighting.AmbientLight
import mc.raytracer.lighting.DirectionalLight
import mc.raytracer.lighting.PointLight
import mc.raytracer.material.ChessboardMaterial
import mc.raytracer.material.MatteMaterial
import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.*
import mc.raytracer.threading.CancelFlag
import mc.raytracer.tracers.RayCasterTracer
import mc.raytracer.util.GlobalRandom
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.ViewPlane
import mc.raytracer.world.World
import java.util.*

class RayTracer {
    var camera: BaseCamera

    var world: World
        private set

    var viewPlane: ViewPlane
        private set

    init {
        viewPlane = ViewPlane(400, 320, pixelSize = 2.0)
        viewPlane.configureNumberOfSamplesPerPixel(4)

        val tracer = RayCasterTracer()

        camera = PinholeCamera()
        world = World(viewPlane, RgbColor.grayscale(0.2), tracer)
    }

    fun enableStereoMode(): StereoCamera<PinholeCamera> {
        enableStereoMode(true)

        @Suppress("UNCHECKED_CAST")
        return (camera as StereoCamera<PinholeCamera>)
    }

    fun disableStereoMode() {
        enableStereoMode(false)
    }

    private fun enableStereoMode(enable: Boolean) {
        val isEnabled = (camera is StereoCamera<*>)

        if (isEnabled == enable)
            return

        if (enable) {
            // we must use 2 different camera's
            val stereoCamera = StereoCamera(PinholeCamera(), PinholeCamera())
            stereoCamera.copyPositionAndOrientationFrom(camera)

            camera = stereoCamera
        } else {
            val pinholeCamera = PinholeCamera()
            pinholeCamera.copyPositionAndOrientationFrom(camera)

            camera = pinholeCamera
        }
    }

    fun render(canvas: RawBitmap, cancelFlag: CancelFlag) {
        val minH = camera.minNeededHorizontalPixels(world)
        val minV = camera.minNeededVerticalPixels(world)

        if (canvas.width < minH || canvas.heigh < minV)
            throw IllegalArgumentException(
                    "Provided canvas (${canvas.width}x${canvas.heigh}) is too " +
                            "small for rendering of current view plane. " +
                            "Provide canvas with at least ${minH}x${minV} resolution.")

        camera.render(world, canvas, cancelFlag)
    }

    fun buildWorld() {
        val rnd = Random()
        rnd.setSeed(12345)

        /*
        val big = Sphere(Point3D(20,0,-270), 20.0)
        big.material = ChessboardMaterial(RgbColor.red, RgbColor.black, patternSize = 1.0)
        world.addObject(big)
        */

        world.ambientLight = AmbientLight(RgbColor.white)

        val N = 20
        val R = 200.0

        for (i in 1..N) {
            val angle = TWO_PI * i.toDouble() / N

            val x = Math.cos(angle) * R - R/2
            val z = Math.sin(angle) * R - R/2

            val sphere = Sphere(
                    Point3D(x, 100.0, z),
                    10 + rnd.nextDouble() * 10)

            sphere.material = MatteMaterial(RgbColor.randomColor(), ambientCoefficient = 0.2)

            world.addObject(sphere)
        }

        /*
        for (i in 1..1) {
            val location = Point3D(-50 + rnd.nextInt(100), rnd.nextInt(200), -50 + rnd.nextInt(100))

            val sphere = Sphere(location, 4.0)
            sphere.material = StaticColorMaterial(RgbColor.white)
            world.addObject(sphere)

            val pointLight = PointLight(location, RgbColor.white)
            world.addLight(pointLight)
        }
        */
        world.addLight(DirectionalLight(Vector3D(-1,-1,-1), RgbColor.white, radianceScalingFactor = 3.0))

        val floor = Plane(Point3D(0.0, -300.01, 0.0), Normal3D(0, 1, 0))
        // floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize = 100.0)
        floor.material = MatteMaterial(RgbColor.grayscale(0.3),
                ambientCoefficient = 0.1, diffuseCoefficient = 1.0)
        world.addObject(floor)
    }

    fun cube(center: Point3D, size: Double)
            = Cuboid(center, size, size, size)
}
