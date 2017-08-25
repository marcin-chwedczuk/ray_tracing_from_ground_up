package mc.raytracer

import mc.raytracer.cameras.*
import mc.raytracer.geometry.Cuboid
import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.primitives.d2.Rectangle
import mc.raytracer.geometry.Sphere
import mc.raytracer.geometry.primitives.d3.Torus
import mc.raytracer.geometry.primitives.d2.Disc
import mc.raytracer.geometry.primitives.d2.Triangle
import mc.raytracer.geometry.primitives.d3.Box
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

        val tracer =  AreaLightingTracer()

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
            world.ambientLight = ConstantColorAmbientLight(RgbColor.white)
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
        // world.ambientLight = ConstantColorAmbientLight(RgbColor.black)
        world.addLight(DirectionalLight(Vector3D(0,-1,0), RgbColor.white, radianceScalingFactor = 3.0))

        val floor = Plane(Point3D(0.0, -3.01, 0.0), Normal3D(0, 1, 0))
        floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize = 100.0)
        //floor.material = MatteMaterial(RgbColor.white, ambientCoefficient = 0.3)

        GlobalRandom.setSeed(12348)


        val minBoxSize = 20.0; val maxBoxSize = 40.0; val padding = 40.0

        /*for (row in 1..10) {
            for (col in 1..10) {
                val x = (maxBoxSize + padding) * (col - 0.5)
                val z = (maxBoxSize + padding) * (row - 0.5)

                val boxSize = GlobalRandom.nextDouble(minBoxSize, maxBoxSize)
                val height = GlobalRandom.nextDouble(30.0, 70.0)
                val p0 = Point3D(x,-3.0,z) - Vector3D(1,0,1)*boxSize/2.0
                val p1 = Point3D(x,height,z) + Vector3D(1,0,1)*boxSize/2.0

                world.addObject(Box(p0,p1).apply {
                    material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.8)
                })
            }
        }*/

        for (i in 1..30) {
            val vertices = (1..3).map {
                GlobalRandom.nextPoint(-100,100, 100,140, -100,100)
            }

            world.addObject(Triangle(vertices[0], vertices[1], vertices[2]).apply {
                material = PhongMaterial(RgbColor.randomColor())
            })
        }



        /*val disc = Disc(Point3D(50,200,120), -Normal3D.axisY, 20.0, CircleSampler.fromSquareSampler(MultiJitteredSampler(256))).apply {
            material = EmissiveMaterial(RgbColor.yellow, radianceScalingFactor = 50.5)
        }
        world.addLight(AreaLight(disc))
        world.addObject(disc)*/

/*
        val rect = Rectangle(Point3D(0,100,0), Vector3D(100,0,0), Vector3D(0,0,100), MultiJitteredSampler(256)).apply {
            material = EmissiveMaterial(RgbColor.yellow, radianceScalingFactor = 4.5)
        }
        world.addLight(AreaLight(rect))
        world.addObject(rect)
    //        */

        /*
        val torus = Torus(Point3D(0,0,0), 100.0, 30.0).apply {
            material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.8, specularExponent = 300.0)
        }
        world.addObject(torus)*/

        /*val openCylinder = OpenCylinder(-3.0, 30.0, 30.0).apply {
            material = MatteMaterial(RgbColor.white, ambientCoefficient = 0.3)
        }
        world.addObject(openCylinder)
        */
        /*world.addObject(Sphere(Point3D(50,80,50), 20.0).apply {
            material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.3)
        })*/

        world.addObject(floor)/*

        world.addObject(OpenCylinder(1.0, 120.0, 50.0).apply {
            material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.3, specularExponent = 300.0)
        })*/

    }

    fun cube(center: Point3D, size: Double)
            = Cuboid(center, size, size, size)
}
