package mc.raytracer

import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.SinX2Y2Function
import mc.raytracer.geometry.Sphere
import mc.raytracer.gui.MainWindow
import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point2D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Vector3D
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.sampling.HammersleySampler
import mc.raytracer.sampling.HemisphereSampler
import mc.raytracer.sampling.NRooksSampler
import mc.raytracer.tracers.SingleSphereTracer
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.ViewPlane
import mc.raytracer.world.World
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    val scale = 8
    val bitmap = RawBitmap(800/scale,640/scale)
    bitmap.clear(0xFFFF69B4.toInt())

    SwingUtilities.invokeLater {
        val mainWindow = MainWindow()
        mainWindow.isVisible = true
        mainWindow.setBitmap(bitmap)
    }

    val pixelSize = 800.0/bitmap.width
    val viewPlane = ViewPlane(bitmap.width, bitmap.heigh, pixelSize=pixelSize)

    // val nrooksSampler = NRooksSampler(3)
    //viewPlane.useSampler(nrooksSampler)
    // viewPlane.useSampler(HammersleySampler(16))

    viewPlane.setNumberOfSamples(16)

    val tracer = SingleSphereTracer()
    val world = World(viewPlane, RgbColor.black, tracer, bitmap)

    val rnd = Random()
    rnd.setSeed(123456)

    for (i in 1..40) {
        val sphere = Sphere(
                Point3D(-300+rnd.nextInt(700), -300+rnd.nextInt(700), -300+rnd.nextInt(300)),
                rnd.nextDouble()*130)

        sphere.material = StaticColorMaterial(RgbColor(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble()))
        world.addObject(sphere)
    }

    /*val tmp = pixelSize*Math.min(bitmap.width, bitmap.heigh) / 2
    val `fun` = SinX2Y2Function(Point2D(-tmp,tmp), Point2D(tmp,-tmp))
    world.addObject(`fun`) */

    val thread = object: Thread() {
        private val rand = Random()

        private fun generateBitmap() {
            world.renderScene()
        }

        override fun run() {
            generateBitmap()
        }
    }

    thread.isDaemon=true
    thread.run()
}
