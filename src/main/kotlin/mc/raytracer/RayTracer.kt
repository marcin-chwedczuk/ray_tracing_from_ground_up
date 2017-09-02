package mc.raytracer

import mc.raytracer.cameras.*
import mc.raytracer.geometry.acceleration.AccelerationGrid
import mc.raytracer.geometry.primitives.d3.Plane
import mc.raytracer.geometry.compound.beveled.BeveledCylinder
import mc.raytracer.geometry.mesh.TessellatedSphere
import mc.raytracer.geometry.primitives.d3.Box
import mc.raytracer.geometry.primitives.d3.Sphere
import mc.raytracer.lighting.*
import mc.raytracer.material.*
import mc.raytracer.math.*
import mc.raytracer.sampling.*
import mc.raytracer.threading.CancelFlag
import mc.raytracer.tracers.AreaLightingTracer
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
        world.addLight(DirectionalLight(Vector3D(-1,0,0), RgbColor.white, radianceScalingFactor = 1.0))
        //world.addLight(DirectionalLight(Vector3D(0.0,-1.0,-1.0), RgbColor.white, radianceScalingFactor = 1.00))
        world.addLight(PointLight(Point3D(0.2,-0.8,0.1), RgbColor.white))
        //world.addObject(Sphere(Point3D(0.2,-0.8,0.1), 0.2))

        val floor = Plane(Point3D(0.0, -1.01, 0.0), Normal3D(0, 1, 0))
        floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize = 5.0)
        //floor.material = MatteMaterial(RgbColor.white)

        val grid = AccelerationGrid(multiplier = 5)

        TessellatedSphere(3, 2, PhongMaterial(RgbColor.red)).addTrianglesToGrid(grid)
        grid.forEachObject { world.addObject(it) }


        grid.initialize()
        //world.addObject(grid)

        world.addObject(floor)
    }
}
