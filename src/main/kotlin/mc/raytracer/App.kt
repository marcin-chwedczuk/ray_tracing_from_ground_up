package mc.raytracer

import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.Sphere
import mc.raytracer.gui.MainWindow
import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Vector3D
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
    val bitmap = RawBitmap(800,640)
    bitmap.clear(0xFFFF69B4.toInt())

    SwingUtilities.invokeLater {
        val mainWindow = MainWindow()
        mainWindow.isVisible = true
        mainWindow.setBitmap(bitmap)
    }

    val viewPlane = ViewPlane(800,640)
    val tracer = SingleSphereTracer()
    val world = World(viewPlane, RgbColor.black, tracer, bitmap)

    val point = Sphere(Point3D(-300,300,0), 20.0)
    point.material = StaticColorMaterial(RgbColor.blue)
    world.addObject(point)

    val sphere = Sphere(Point3D.zero, 100.0)
    sphere.material = StaticColorMaterial(RgbColor.red)
    world.addObject(sphere)

    val sphere2 = Sphere(Point3D(0,40,0), 80.0)
    sphere2.material = StaticColorMaterial(RgbColor.yellow)
    world.addObject(sphere2)

    val plane = Plane(Point3D.zero, Normal3D(0,1,1))
    plane.material = StaticColorMaterial(RgbColor.green)
    world.addObject(plane)

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
