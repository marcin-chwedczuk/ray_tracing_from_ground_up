package mc.raytracer

import mc.raytracer.gui.MainWindow
import mc.raytracer.util.GlobalRandom
import javax.swing.SwingUtilities


fun main(args: Array<String>) {
    GlobalRandom.setSeed(303)

    val rayTracer = RayTracer()
    rayTracer.buildWorld()

    val rayTracingThread = RayTracingThread(rayTracer)

    val mainWindow = MainWindow(rayTracingThread)
    SwingUtilities.invokeLater {
        mainWindow.isVisible = true
    }

    rayTracingThread.run()
}
