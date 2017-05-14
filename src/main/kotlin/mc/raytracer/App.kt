package mc.raytracer

import mc.raytracer.gui.MainWindow
import mc.raytracer.util.RawBitmap
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    SwingUtilities.invokeLater {
        val mainWindow = MainWindow()
        mainWindow.isVisible = true
    }
}
