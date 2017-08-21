package mc.raytracer

import mc.raytracer.cameras.*
import mc.raytracer.geometry.Cuboid
import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.Sphere
import mc.raytracer.geometry.primitives.OpenCylinder
import mc.raytracer.geometry.primitives.Torus
import mc.raytracer.lighting.AmbientLight
import mc.raytracer.lighting.DirectionalLight
import mc.raytracer.lighting.PointLight
import mc.raytracer.lighting.SpotLight
import mc.raytracer.material.ChessboardMaterial
import mc.raytracer.material.MatteMaterial
import mc.raytracer.material.PhongMaterial
import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.*
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.sampling.MultiJitteredSampler
import mc.raytracer.sampling.SquareSampler
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
        // viewPlane.showOutOfGamutErrors = true

        val tracer = RayCasterTracer()

        //val tmp = ThinLensCamera(CircleSampler.fromSquareSampler(MultiJitteredSampler(numberOfSamples = 36)))
        camera = PinholeCamera()
        //tmp.lensRadius = 1.4
        //tmp.focalPlaneDistance = 200.0
        //camera.moveUp(100.0)
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

        world.ambientLight = AmbientLight(RgbColor.white)
        // world.addLight(DirectionalLight(-Vector3D.axisZ, RgbColor.white, radianceScalingFactor = 1.0))

        val floor = Plane(Point3D(0.0, -3.01, 0.0), Normal3D(0, 1, 0))
        floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize = 100.0)
        floor.material = PhongMaterial(RgbColor.white)


        GlobalRandom.setSeed(123489)


        val size = 500

        for (i in 1..2) {
            val location = GlobalRandom.nextPoint(-size,size, 200,250, -size,size)
            val lookAt = GlobalRandom.nextPoint(-size,size, 0,-3000, -size,size)

            val color = RgbColor.randomColor()

            world.addObject(Sphere(location, 10.0).apply {
                material = StaticColorMaterial(color).apply {
                    castsShadows = false
                }
            })
            world.addLight(PointLight(location, color))

        }

        // world.addLight(DirectionalLight(-Vector3D.axisY, RgbColor.red))

        for (i in 1..15) {
            val location = GlobalRandom.nextPoint(-size,size, 23,23, -size,size)

            world.addObject(Sphere(location, 23.0).apply {
                material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.3, specularExponent = 40.0)
            })
        }

        /*
        val torus = Torus(Point3D(0,0,0), 100.0, 30.0).apply {

            material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.3, specularExponent = 300.0)
        }
        world.addObject(torus)*/
        world.addObject(floor)/*

        world.addObject(OpenCylinder(1.0, 120.0, 50.0).apply {
            material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.3, specularExponent = 300.0)
        })*/

    }

    fun cube(center: Point3D, size: Double)
            = Cuboid(center, size, size, size)
}
