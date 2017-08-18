package mc.raytracer

import mc.raytracer.cameras.BaseCamera
import mc.raytracer.cameras.PinholeCamera
import mc.raytracer.cameras.StereoCamera
import mc.raytracer.geometry.Cuboid
import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.Sphere
import mc.raytracer.geometry.primitives.OpenCylinder
import mc.raytracer.geometry.primitives.Torus
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
        camera.moveUp(100.0)
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
        // world.addLight(PointLight(Point3D(0, 200, 0), RgbColor.white))

        val floor = Plane(Point3D(0.0, -3.01, 0.0), Normal3D(0, 1, 0))
        // floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize = 100.0)
        floor.material = MatteMaterial(
                RgbColor.grayscale(1.0),
                ambientCoefficient = 0.1, diffuseCoefficient = 1.0)


        /*
        val sphere = Sphere(Point3D(0, 100, -30), 30.0).apply {
            material = MatteMaterial(RgbColor.red, ambientCoefficient = .2, diffuseCoefficient = 1.0)
        }
        world.addObject(sphere)

        val sphere2 = Sphere(Point3D(40, 100, -90), 30.0).apply {
            material = MatteMaterial(RgbColor.orange, ambientCoefficient = .2, diffuseCoefficient = 1.0)
        }
        world.addObject(sphere2)

        val cylinder = OpenCylinder(0.0, 30.0, 90.0).apply {
            material = MatteMaterial(RgbColor.green, ambientCoefficient = .2, diffuseCoefficient = 1.0)
        }
        world.addObject(cylinder) */

        /*
        for (i in 1..7) {
            val torus = Torus(Point3D(-100 + rnd.nextInt(200), 0, -100 +rnd.nextInt(200)), 15+rnd.nextDouble()*10, 10.0).apply {
                material = MatteMaterial(
                        RgbColor.randomColor(),
                        ambientCoefficient = .2,
                        diffuseCoefficient = 1.0)
            }
            world.addObject(torus)
        }*/

        val torus = Torus(Point3D.zero, 300.0, 130.0).apply {
            material = MatteMaterial(
                    RgbColor.randomColor(),
                    ambientCoefficient = .5,
                    diffuseCoefficient = 1.0)
        }
        world.addObject(torus)

        world.addObject(floor)

    }

    fun cube(center: Point3D, size: Double)
            = Cuboid(center, size, size, size)
}
