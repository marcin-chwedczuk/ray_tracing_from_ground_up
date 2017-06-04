package mc.raytracer

import mc.raytracer.cameras.BaseCamera
import mc.raytracer.cameras.PinholeCamera
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
        private set

    var world: World
        private set

    var viewPlane: ViewPlane
        private set

    init {
        viewPlane = ViewPlane(800, 640, pixelSize=1.0)
        viewPlane.configureNumberOfSamplesPerPixel(4)

        val tracer = SimpleMultipleObjectsTracer()

        camera = PinholeCamera()
        world = World(viewPlane, RgbColor.grayscale(0.2), tracer)
    }

    fun render(canvas: RawBitmap, cancelFlag: CancelFlag) {
        if (canvas.width < viewPlane.horizontalResolution ||
            canvas.heigh < viewPlane.verticalResolution)
                throw IllegalArgumentException(
                        "Provided canvas is too small for rendering of current view plane. " +
                        "Provide canvas with at lest ${viewPlane.horizontalResolution}x${viewPlane.verticalResolution} resolution.")

        camera.render(world, canvas, cancelFlag)
    }

    fun buildWorld() {
        val rnd = Random()
        rnd.setSeed(123456)

        /* for (i in 1..80) {
             val sphere = Sphere(
                     Point3D(-300+rnd.nextInt(700), -300+rnd.nextInt(700), -500+rnd.nextInt(800)),
                     rnd.nextDouble()*60)

             sphere.material = StaticColorMaterial(RgbColor(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble()))
             world.addObject(sphere)
         }*/

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
        }

        val floor = Plane(Point3D(0.0,-300.01,0.0), Normal3D(0,1,0))
        floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize=100.0)
        world.addObject(floor)
    }
}
