package mc.raytracer

import mc.raytracer.cameras.*
import mc.raytracer.geometry.Cuboid
import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.compound.SolidCone
import mc.raytracer.geometry.compound.SolidCylinder
import mc.raytracer.geometry.primitives.d2.Annulus
import mc.raytracer.geometry.primitives.d2.part.PartAnnulus
import mc.raytracer.geometry.primitives.d3.OpenCone
import mc.raytracer.geometry.primitives.d3.Sphere
import mc.raytracer.geometry.primitives.d3.Torus
import mc.raytracer.geometry.primitives.d3.part.*
import mc.raytracer.lighting.*
import mc.raytracer.material.*
import mc.raytracer.math.*
import mc.raytracer.sampling.*
import mc.raytracer.threading.CancelFlag
import mc.raytracer.tracers.AreaLightingTracer
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
        //viewPlane.showOutOfGamutErrors = true

        val tracer = AreaLightingTracer()

        //val tmp = ThinLensCamera(CircleSampler.fromSquareSampler(MultiJitteredSampler(numberOfSamples = 36)))
        camera = PinholeCamera()
        //tmp.lensRadius = 1.4
        //tmp.focalPlaneDistance = 200.0
        //camera.moveUp(100.0)
        world = World(viewPlane, RgbColor.black, tracer)
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

    public fun enableAmbientOcclusion(enable: Boolean) {
        val isEnabled = (world.ambientLight is AmbientOccluderAmbientLight)

        if (isEnabled == enable)
            return

        if (isEnabled) {
            world.ambientLight = ConstantColorAmbientLight(RgbColor.white, radianceScalingFactor = 0.25)
        }
        else {
            world.ambientLight = AmbientOccluderAmbientLight(
                    RgbColor.white,
                    HemisphereSampler.fromSquareSampler(MultiJitteredSampler(64)),
                    radianceScalingFactor = 1.0)
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

        enableAmbientOcclusion(false)
        //world.addLight(DirectionalLight(Vector3D(0,-1,0), RgbColor.white, radianceScalingFactor = 1.0))
       world.addLight(DirectionalLight(Vector3D(0.0,-1.0,-1.0), RgbColor.orange, radianceScalingFactor = 2.40))

        world.addLight(PointLight(Point3D(0.0,-1.0,0.0), RgbColor.red))


        val floor = Plane(Point3D(0.0, -3.01, 0.0), Normal3D(0, 1, 0))
        floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize = 50.0)
        floor.material = MatteMaterial(RgbColor.white)

        GlobalRandom.setSeed(12348)

        world.addObject(SolidCone(2.0, 1.0).apply {
            material = PhongMaterial(RgbColor.red)
        })

        /*
        for (i in 1..30) {
            val location = GlobalRandom.nextPoint(-300,300, 40,120, -300,300)
            val radius = GlobalRandom.nextDouble(10.0, 30.0)

            world.addObject(Sphere(location, radius).apply {
                material = PhongMaterial(RgbColor.randomColor())
            })

        }*/

        /*val torus = Torus(10.0, 2.0).apply {
            material = MatteMaterial(RgbColor.white, ambientCoefficient = 0.1)
        }
        world.addObject(torus)*/
        world.addObject(floor)

        /*
        world.addObject(OpenCylinder(1.0, 120.0, 50.0).apply {
            material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.3, specularExponent = 300.0)
        })*/

    }

    fun cube(center: Point3D, size: Double)
            = Cuboid(center, size, size, size)
}
