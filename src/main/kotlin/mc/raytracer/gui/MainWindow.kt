package mc.raytracer.gui

import com.sun.javafx.scene.input.KeyCodeMap
import mc.raytracer.util.RawBitmap
import sun.swing.SwingUtilities2
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.image.ColorModel
import java.awt.image.WritableRaster
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.Lock
import javax.swing.SwingUtilities


class MainWindow : JFrame() {
    init { initUI() }

    private var bufferedImage: BufferedImage? = null
    private var raster: WritableRaster? = null
    private var bitmap: RawBitmap? = null

    var keyCodeQueue: Queue<Int> = ConcurrentLinkedQueue()

    fun setBitmap(bitmap: RawBitmap) {
        val colorModel = ColorModel.getRGBdefault()

        this.raster = colorModel.createCompatibleWritableRaster(bitmap.width, bitmap.heigh)
        this.bufferedImage = BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied, null)
        this.bitmap = bitmap

        // setSize(bitmap.width, bitmap.heigh)
    }

    private fun updateBufferedImage() {
        val bmp = bitmap

        if (bmp != null) {
            synchronized(bmp) {
               raster!!.setDataElements(
                   0, 0, bmp.width, bmp.heigh,
                   bmp.rawArgbData)
            }
        }
    }

    fun initUI() {
        title = "Ray Tracer"
        defaultCloseOperation = EXIT_ON_CLOSE
        isResizable = false
        setSize(800, 640)
        setLocationRelativeTo(null)

        val panel = object : JPanel() {
            override fun paintComponent(g: Graphics?) {
                super.paintComponent(g)

                val buff = bufferedImage ?: return

                // TODO: Make screen black or pink

                g!!.color = Color.RED
                g.drawImage(buff, 0, 0, 800, 640, null)
            }
        }
        add(panel)

        addKeyListener(object: KeyListener {
            override fun keyPressed(e: KeyEvent?) { }
            override fun keyTyped(e: KeyEvent?) {}

            override fun keyReleased(e: KeyEvent?) {
                if (e!!.keyCode == KeyEvent.VK_ESCAPE) {
                    keyCodeQueue.clear()
                }
                else {
                    keyCodeQueue.offer(e.keyCode)
                    println("Keyboard Event: $e")
                }
            }
        })

        val timer = javax.swing.Timer(100, {
            updateBufferedImage()
            panel.paintImmediately(0, 0, panel.width, panel.height)
        })
        timer.start()
    }
}
