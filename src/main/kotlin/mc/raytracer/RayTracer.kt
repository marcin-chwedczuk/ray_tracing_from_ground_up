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
import mc.raytracer.lighting.SpotLight
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
        // viewPlane.showOutOfGamutErrors = true

        val tracer = RayCasterTracer()

        camera = PinholeCamera()
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
          floor.material = MatteMaterial(RgbColor.white)


        GlobalRandom.setSeed(12349)


        val size = 500

        for (i in 1..10) {
            val location = GlobalRandom.nextPoint(-size,size, 200,250, -size,size)
            val lookAt = GlobalRandom.nextPoint(-size,size, 0,-3000, -size,size)

            val color = RgbColor.randomColor()

            world.addLight(SpotLight(location, -Vector3D.axisY, Angle.fromDegrees(15),
                    color, radianceScalingFactor = 2.0, cutOffExponent = 400.0))

        }




        for (i in 1..20) {
            val location = GlobalRandom.nextPoint(-size,size, 13,13, -size,size)

            world.addObject(Sphere(location, 13.0).apply {
                material = MatteMaterial(RgbColor.white, ambientCoefficient = 0.3)
            })
        }


        val torus = Torus(Point3D(0,0,0), 100.0, 30.0).apply {

            material = MatteMaterial(RgbColor.white, ambientCoefficient = 0.3)
        }
        world.addObject(torus)
        world.addObject(floor)

    }

    fun cube(center: Point3D, size: Double)
            = Cuboid(center, size, size, size)
}
