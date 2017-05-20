package mc.raytracer

import mc.raytracer.gui.MainWindow
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
