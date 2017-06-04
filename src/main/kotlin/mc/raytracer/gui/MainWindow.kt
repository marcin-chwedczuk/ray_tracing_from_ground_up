package mc.raytracer.gui

import mc.raytracer.RayTracingThread
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.Resolution
import mc.raytracer.util.RgbColor
import java.awt.Graphics
import java.awt.event.*
import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.WritableRaster
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities


class MainWindow(val rayTracingThread: RayTracingThread)
    : JFrame() {

    private var currentResolution = 1
    private var supportedResolutions = arrayOf(
            Resolution(100,80),
            Resolution(200,160),
            Resolution(400,320),
            Resolution(800,640))

    private var currentSampleNumber = 2

    private lateinit var bitmap: RawBitmap
    private lateinit var raster: WritableRaster
    private lateinit var bufferedImage: BufferedImage

    private lateinit var panel: JPanel

    init {
        changeToCurrentResolution()
        initUI()
    }

    private fun changeToCurrentResolution() {
        val newResolution = supportedResolutions[currentResolution]

        val colorModel = ColorModel.getRGBdefault()

        this.bitmap = RawBitmap(newResolution.horizontal, newResolution.vertical)
        this.raster = colorModel.createCompatibleWritableRaster(
                newResolution.horizontal, newResolution.vertical)

        this.bufferedImage = BufferedImage(
                colorModel, raster, colorModel.isAlphaPremultiplied, null)

        bitmap.clear(RgbColor.black.toArgb())

        rayTracingThread.changeBitmap(bitmap)
    }

    private fun updateBufferedImage() {
        raster.setDataElements(
                0, 0, bitmap.width, bitmap.heigh,
                bitmap.rawArgbData)

        panel.paintImmediately(0, 0, panel.width, panel.height)
    }

    fun initUI() {
        title = "Ray Tracer"
        defaultCloseOperation = EXIT_ON_CLOSE
        isResizable = false
        setSize(800, 640)
        setLocationRelativeTo(null)

        panel = object : JPanel() {
            override fun paintComponent(g: Graphics?) {
                super.paintComponent(g)

                g!!.drawImage(
                        bufferedImage,
                        0, 0,
                        panel.width, panel.height,
                        null)
            }
        }
        add(panel)

        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                rayTracingThread.updateRayTracer { rayTracer ->
                    val step = 20.0

                    when (e!!.keyCode) {
                        KeyEvent.VK_ESCAPE, KeyEvent.VK_Q -> {
                            SwingUtilities.invokeLater {
                                this@MainWindow.dispatchEvent(
                                        WindowEvent(this@MainWindow, WindowEvent.WINDOW_CLOSING))
                            }
                        }

                        KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4 -> {
                            val index: Int = e.keyChar - '1'

                            currentResolution = index
                            changeToCurrentResolution()
                        }

                        KeyEvent.VK_RIGHT -> {
                            rayTracer.camera.yawAngleInDegrees -= 5.0
                        }
                        KeyEvent.VK_LEFT -> {
                            rayTracer.camera.yawAngleInDegrees += 5.0
                        }
                        KeyEvent.VK_UP -> {
                            rayTracer.camera.pitchAngleInDegrees += 5.0
                        }
                        KeyEvent.VK_DOWN -> {
                            rayTracer.camera.pitchAngleInDegrees -= 5.0
                        }
                        KeyEvent.VK_R -> {
                            rayTracer.camera.rollAngleInDegrees += 5.0
                        }
                        KeyEvent.VK_T -> {
                            rayTracer.camera.rollAngleInDegrees -= 5.0
                        }

                        KeyEvent.VK_W -> {
                            rayTracer.camera.moveForward(step)
                        }
                        KeyEvent.VK_S -> {
                            rayTracer.camera.moveBackwards(step)
                        }
                        KeyEvent.VK_D -> {
                            rayTracer.camera.moveRight(step)
                        }
                        KeyEvent.VK_A -> {
                            rayTracer.camera.moveLeft(step)
                        }

                        KeyEvent.VK_P -> {
                            currentSampleNumber++
                            rayTracer.viewPlane.configureNumberOfSamplesPerPixel(
                                    currentSampleNumber*currentSampleNumber)
                        }
                        KeyEvent.VK_O -> {
                            currentSampleNumber = Math.max(1, currentSampleNumber-1)
                            rayTracer.viewPlane.configureNumberOfSamplesPerPixel(
                                    currentSampleNumber*currentSampleNumber)
                        }
                    }
                }
            }
        })

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                rayTracingThread.cancelThreadAndWait()
            }
        })

        val timer = javax.swing.Timer(100, {
            // This event handler is executed
            // on Swing event thread.

            updateBufferedImage()
        })
        timer.start()
    }
}
