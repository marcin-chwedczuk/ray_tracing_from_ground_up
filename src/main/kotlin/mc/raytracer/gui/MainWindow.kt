package mc.raytracer.gui

import mc.raytracer.util.RawBitmap
import sun.swing.SwingUtilities2
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.image.ColorModel
import java.util.*
import javax.swing.SwingUtilities


class MainWindow : JFrame() {
    init { initUI() }

    fun initUI() {
        title = "Ray Tracer"
        defaultCloseOperation = EXIT_ON_CLOSE
        isResizable = false
        setSize(800, 640)
        setLocationRelativeTo(null)

        //val image = BufferedImage(800, 640, TYPE_INT_ARGB)

        val colorModel = ColorModel.getRGBdefault()
        val raster = colorModel.createCompatibleWritableRaster(800, 640)
        val image = BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied, null)

        val rand = Random()
        val bitmap = RawBitmap(800, 640)

        fun generateBitmap() {
            for (row in 0 until bitmap.heigh) {
                for (col in 0 until bitmap.width) {
                    // Format ARGB
                    // val color = 0xFF00FF00.toInt()
                    val color = (0xFF000000 or rand.nextLong()).toInt()
                    bitmap.setPixel(row, col, color)
                }
            }
        }

        generateBitmap()
        raster.setDataElements(0, 0, 800, 640, bitmap.rawArgbData)

        val panel = object : JPanel() {
            override fun paintComponent(g: Graphics?) {
                super.paintComponent(g)

                g!!.color = Color.RED
                g.drawImage(image, 0, 0, 800, 640, null)

            }
        }
        add(panel)

        /*var update: Runnable? = null
        update = Runnable {
            for (index in imageData.indices) {
                // Format ARGB
                // val color = 0xFF00FF00.toInt()
                val color = (0xFF000000 or rand.nextLong()).toInt()
                imageData[index] = color
            }

            raster.setDataElements(0,0,800,640,imageData)
            panel.paintImmediately(0,0,800,640)

            Thread.sleep(0)
            SwingUtilities.invokeLater(update)
        }

        update.run() */

        // panel.background = Color.BLACK

        val timer = javax.swing.Timer(200, {
            generateBitmap()
            raster.setDataElements(0, 0, 800, 640, bitmap.rawArgbData)
            panel.paintImmediately(0, 0, 800, 640)
        })

        timer.start()
    }
}
