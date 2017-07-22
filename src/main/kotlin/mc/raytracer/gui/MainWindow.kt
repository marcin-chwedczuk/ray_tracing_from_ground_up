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
import java.io.File
import java.io.FileFilter
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter


class MainWindow(val rayTracingThread: RayTracingThread)
    : JFrame() {

    private var currentResolution = 4
    private var supportedResolutions = arrayOf(
            Resolution(100,80),
            Resolution(200,160),
            Resolution(400,320),
            Resolution(800,640),
            Resolution(810, 640) /* for stereo viewing */)

    private var currentSampleNumber = 2

    private lateinit var bitmap: RawBitmap
    private lateinit var raster: WritableRaster
    private lateinit var bufferedImage: BufferedImage

    private lateinit var panel: JPanel

    init {
        initUI()
        changeToCurrentResolution()
    }

    private fun changeToCurrentResolution() {
        val newResolution = supportedResolutions[currentResolution]

        // default color model has AARRBBGG format
        val colorModel = ColorModel.getRGBdefault()

        this.bitmap = RawBitmap(newResolution.horizontal, newResolution.vertical)
        this.raster = colorModel.createCompatibleWritableRaster(
                newResolution.horizontal, newResolution.vertical)

        this.bufferedImage = BufferedImage(
                colorModel, raster, colorModel.isAlphaPremultiplied, null)

        bitmap.clear(RgbColor.black.toArgb())

        rayTracingThread.changeBitmap(bitmap)

        if (newResolution.horizontal >= 800 && newResolution.vertical >= 640) {
            setSize(newResolution.horizontal, newResolution.vertical)
            panel.setSize(newResolution.horizontal, newResolution.vertical)
        }
        else {
            setSize(800, 640)
            panel.setSize(800, 640)
        }
    }

    private fun updateBufferedImage() {
        raster.setDataElements(
                0, 0, bitmap.width, bitmap.heigh,
                bitmap.rawArgbData)

        panel.paintImmediately(0, 0, panel.width, panel.height)
    }

    private fun saveCurrentImageToFile() {
        val saveDialog = JFileChooser()

        saveDialog.dialogTitle = "Save current image..."
        saveDialog.fileFilter = FileNameExtensionFilter("PNG files (*.png)", "png")

        val result = saveDialog.showSaveDialog(null)

        if (result == JFileChooser.APPROVE_OPTION) {
            var filePath = saveDialog.selectedFile.absolutePath

            if (!filePath.endsWith("png", ignoreCase=true)) {
                filePath += ".png"
            }

            try {
                if (!ImageIO.write(bufferedImage, "PNG", File(filePath)))
                    throw RuntimeException("ImageIO.write failed: Cannot find PNG format writer.")
            }
            catch(e: Exception) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cannot save file: " + filePath + ". " + e.message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE)
            }
        }
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

                        KeyEvent.VK_0 -> {
                            saveCurrentImageToFile()
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
