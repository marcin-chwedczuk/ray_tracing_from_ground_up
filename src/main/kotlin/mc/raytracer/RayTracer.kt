package mc.raytracer

import mc.raytracer.cameras.BaseCamera
import mc.raytracer.cameras.PinholeCamera
import mc.raytracer.cameras.StereoCamera
import mc.raytracer.geometry.Cuboid
import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.Sphere
import mc.raytracer.material.ChessboardMaterial
import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.sampling.MultiJitteredSampler
import mc.raytracer.threading.CancelFlag
import mc.raytracer.tracers.SimpleMultipleObjectsTracer
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
        viewPlane = ViewPlane(400, 320, pixelSize=2.0)
        viewPlane.configureNumberOfSamplesPerPixel(4)

        val tracer = SimpleMultipleObjectsTracer()

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
        }
        else {
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
                        "Provided canvas (${canvas.width}x${canvas.heigh}) is too "+
                        "small for rendering of current view plane. " +
                        "Provide canvas with at least ${minH}x${minV} resolution.")

        camera.render(world, canvas, cancelFlag)
    }

    fun buildWorld() {
        val rnd = Random()
        rnd.setSeed(123456)

        val small = Sphere(Point3D(-7,0, 40), 3.0)
        small.material = ChessboardMaterial(RgbColor(66, 179, 244), RgbColor.black, patternSize = 1.0)
        world.addObject(small)

        val medium = Sphere(Point3D(0,0,-30), 6.0)
        medium.material = ChessboardMaterial(RgbColor.yellow, RgbColor.black, patternSize = 1.0)
        world.addObject(medium)

        val big = Sphere(Point3D(20,0,-270), 20.0)
        big.material = ChessboardMaterial(RgbColor.red, RgbColor.black, patternSize = 1.0)
        world.addObject(big)

        for (i in 1..20) {
             val sphere = Sphere(
                     Point3D(-100+rnd.nextInt(200), -100+rnd.nextInt(200), -500+rnd.nextInt(800)),
                     rnd.nextDouble()*20)

             sphere.material = ChessboardMaterial(
                     RgbColor(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble()),
                     RgbColor.black,
                     patternSize = sphere.radius / 10.0)
             world.addObject(sphere)
         }

        /*
        val sunny = Sphere(Point3D(0,800,0), 100.0)
        sunny.material = StaticColorMaterial(RgbColor.white)
        world.addObject(sunny)

        for(i in 1..16) {
            val box = Cuboid(Point3D(0,0,-300+i*40), length = 20.0, depth = 20.0, height = 100.0)
            box.material = ChessboardMaterial(RgbColor.black, RgbColor.randomColor(), patternSize=5.0)
            world.addObject(box)
        }

        for(i in 1..16) {
            val box = Cuboid(Point3D(30+i*40,0,-300), length = 20.0, depth = 20.0, height = 100.0)
            box.material = ChessboardMaterial(RgbColor.black, RgbColor.randomColor(), patternSize=5.0)
            world.addObject(box)
        }*/

        val floor = Plane(Point3D(0.0,-300.01,0.0), Normal3D(0,1,0))
        floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize=100.0)
        world.addObject(floor)
    }

    fun cube(center: Point3D, size: Double)
        = Cuboid(center, size, size, size)
}
