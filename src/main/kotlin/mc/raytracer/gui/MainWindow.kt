package mc.raytracer.gui

import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.image.WritableRaster
import java.awt.image.ColorModel
import java.util.*


class MainWindow : JFrame {
    constructor() : super() {
        initUI()
    }

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
        val imageData = IntArray(800*640)
        for (index in imageData.indices) {
            // Format ARGB
            // val color = 0xFF00FF00.toInt()
            val color = (0xFF000000 or rand.nextLong()).toInt()
            imageData[index] = color
        }
        raster.setDataElements(0,0,800,640,imageData)

        val panel = object : JPanel() {
            override fun paintComponent(g: Graphics?) {
                super.paintComponent(g)

                g!!.color = Color.RED
                g.drawImage(image, 0, 0, 800, 640, null)
            }
        }

        // panel.background = Color.BLACK
        add(panel)

        val timer = javax.swing.Timer(500, {
            for (index in imageData.indices) {
                // Format ARGB
                // val color = 0xFF00FF00.toInt()
                val color = (0xFF000000 or rand.nextLong()).toInt()
                imageData[index] = color
            }

            raster.setDataElements(0,0,800,640,imageData)
            panel.paintImmediately(0,0,800,640)
        })

        timer.start()
    }
}
