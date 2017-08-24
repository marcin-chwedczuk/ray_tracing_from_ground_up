package mc.raytracer

import mc.raytracer.cameras.*
import mc.raytracer.geometry.Cuboid
import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.primitives.d2.Rectangle
import mc.raytracer.geometry.Sphere
import mc.raytracer.geometry.primitives.Torus
import mc.raytracer.geometry.primitives.d2.Disc
import mc.raytracer.lighting.*
import mc.raytracer.material.*
import mc.raytracer.math.*
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.sampling.HemisphereSampler
import mc.raytracer.sampling.MultiJitteredSampler
import mc.raytracer.sampling.UniformSphereSampler
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
        viewPlane.configureNumberOfSamplesPerPixel(256)
        //viewPlane.showOutOfGamutErrors = true

        val tracer = AreaLightingTracer() //  RayCasterTracer()

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
                    HemisphereSampler.fromSquareSampler(MultiJitteredSampler(256)),
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

        // world.addLight(DirectionalLight(Vector3D(0,-1,-1), RgbColor.white, radianceScalingFactor = 1.0))

        val floor = Plane(Point3D(0.0, -3.01, 0.0), Normal3D(0, 1, 0))
        floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize = 100.0)
         floor.material = MatteMaterial(RgbColor.white, ambientCoefficient = 0.3)


        GlobalRandom.setSeed(12348)


        val size = 500


        val rot4 = Matrix4.rotationMatrix(Vector3D.axisY, Angle.fromDegrees(120))
        val vec = Vector3D(-1.0,-0.3,0.0)*200.0
        val vec2 = rot4 * vec
        val vec3 = rot4 * vec2

/*
        world.addLight(SpotLight(Point3D.zero - vec, vec, Angle.fromDegrees(50), RgbColor.red,radianceScalingFactor = 1.0))
        world.addLight(SpotLight(Point3D.zero - vec2, vec2, Angle.fromDegrees(50), RgbColor.green,radianceScalingFactor = 2.0))
        world.addLight(SpotLight(Point3D.zero - vec3, vec3, Angle.fromDegrees(50), RgbColor.blue,radianceScalingFactor = 1.0))
*/

        //world.addLight(SpotLight(Point3D.zero - vec3, vec3, Angle.fromDegrees(50), RgbColor.orange,radianceScalingFactor = 2.3))


        /*
        val obj = Rectangle(Point3D(0,250,0), Vector3D.axisX*150.0, Vector3D.axisZ*150.0, MultiJitteredSampler(256)).apply {
            material = EmissiveMaterial(RgbColor.yellow, radianceScalingFactor = 5.0)
        }
        world.addLight(AreaLight(obj))
        world.addObject(obj)

        val obj2 = Rectangle(Point3D(0,250,-300), Vector3D.axisX*150.0, Vector3D.axisZ*150.0, MultiJitteredSampler(256)).apply {
            material = EmissiveMaterial(RgbColor.red, radianceScalingFactor = 5.0)
        }
        world.addLight(AreaLight(obj2))
        world.addObject(obj2)
        */

        /*
        for (i in 1..8) {
            val location = GlobalRandom.nextPoint(-size,size, 200,250, -size,size)
            val raysDirection = GlobalRandom.nextPoint(-size,size, 0,-3000, -size,size)

            val color = RgbColor.randomColor()

            world.addObject(Sphere(location, 10.0).apply {
                material = StaticColorMaterial(color).apply {
                    generatesShadows = false
                }
            })
            world.addLight(DirectionalLight(Point3D.zero - location, color, radianceScalingFactor = 1.3))

        }*/

        // world.addLight(DirectionalLight(-Vector3D.axisY, RgbColor.red))

        for (i in 1..15) {
            val location = GlobalRandom.nextPoint(-size,size, 23,23, -size,size)

            world.addObject(Sphere(location, 23.0).apply {
                material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.3)
            })
        }



        /*
        val disc = Disc(
                Point3D(30,242,-100),
                -Normal3D.axisY,
                160.0,
                CircleSampler.fromSquareSampler(MultiJitteredSampler(256))).apply {
            material = EmissiveMaterial(RgbColor.white)
        }
        world.addObject(disc)
        world.addLight(AreaLight(disc))
        */


        val glowingSphere = Sphere(
                Point3D(30, 242, -100),
                180.0,
                UniformSphereSampler.fromSquareSampler(MultiJitteredSampler(256))).apply {
                    material = EmissiveMaterial(RgbColor.white, radianceScalingFactor = 4*PI*18)
                }
        world.addObject(glowingSphere)
        world.addLight(AreaLight(glowingSphere))

        /*
        val torus = Torus(Point3D(0,0,0), 100.0, 30.0).apply {
            material = PhongMaterial(RgbColor.white, ambientCoefficient = 0.3, specularExponent = 300.0)
        }
        world.addObject(torus) */

        /*val openCylinder = OpenCylinder(-3.0, 30.0, 30.0).apply {
            material = MatteMaterial(RgbColor.white, ambientCoefficient = 0.3)
        }
        world.addObject(openCylinder)

        world.addObject(Sphere(Point3D(0,60,0), 30.0).apply {
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
