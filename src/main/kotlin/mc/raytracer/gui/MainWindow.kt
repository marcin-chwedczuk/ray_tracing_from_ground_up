package mc.raytracer.gui

import mc.raytracer.RayTracer
import mc.raytracer.RayTracingThread
import mc.raytracer.cameras.BaseCamera
import mc.raytracer.lighting.AmbientOccluderAmbientLight
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.Resolution
import mc.raytracer.util.RgbColor
import mc.raytracer.util.StereoResolution
import java.awt.Graphics
import java.awt.event.*
import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.WritableRaster
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter


class MainWindow(val rayTracingThread: RayTracingThread)
    : JFrame() {

    private var currentResolution = 1
    private var supportedResolutions = arrayOf(
            Resolution(100,80, scale=8),
            Resolution(200,160, scale=4),
            Resolution(400,320, scale=2),
            Resolution(800,640, scale=1),
            StereoResolution(400, 320, pixelGap=8), /* for stereo viewing */
            StereoResolution(200, 160, pixelGap=4, scale=2))

    private var currentSampleNumber = 2

    private lateinit var bitmap: RawBitmap
    private lateinit var raster: WritableRaster
    private lateinit var bufferedImage: BufferedImage

    private lateinit var panel: JPanel
    private val keyboardState = KeyboardState()

    init {
        initUI()
        changeToCurrentResolution()
    }

    private fun changeToCurrentResolution() {
        val newResolution = supportedResolutions[currentResolution]

        // default color model has AARRBBGG format
        val colorModel = ColorModel.getRGBdefault()

        this.bitmap = RawBitmap(newResolution.canvasHorizontal, newResolution.canvasVertical)
        this.raster = colorModel.createCompatibleWritableRaster(
                newResolution.canvasHorizontal, newResolution.canvasVertical)

        this.bufferedImage = BufferedImage(
                colorModel, raster, colorModel.isAlphaPremultiplied, null)

        bitmap.clear(RgbColor.black.toArgb())

        rayTracingThread.changeResolution(bitmap, newResolution)

        setSize(newResolution.windowHorizontal, newResolution.windowVertical)
        panel.setSize(newResolution.windowHorizontal, newResolution.windowVertical)
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

        keyboardState.registerListener(this)

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                rayTracingThread.cancelThreadAndWait()
            }
        })

        val timer = javax.swing.Timer(100, {
            // This event handler is executed
            // on Swing event thread.

            updateBufferedImage()
            runKeyPressedListeners()
        })
        timer.start()
    }

    private fun runKeyPressedListeners() {
        // updateRayTracer will stop rendering of
        // the current frame, I want to do this only
        // when we actually change the scene via key presses.
        if (!keyboardState.hasPressedKeys) return

        rayTracingThread.updateRayTracer { rayTracer ->
            keyboardState.forPressedKeys { key ->
                handleKeyPress(keyboardState, key, rayTracer)
            }
        }
    }

    private fun handleKeyPress(sender: KeyboardState, key: KeyboardState.KeyInfo, rayTracer: RayTracer) {
        val step = 10.0

        when (key.keyCode) {
            KeyEvent.VK_ESCAPE, KeyEvent.VK_Q -> {
                SwingUtilities.invokeLater {
                    this@MainWindow.dispatchEvent(
                            WindowEvent(this@MainWindow, WindowEvent.WINDOW_CLOSING))
                }
            }

            KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4,
            KeyEvent.VK_5, KeyEvent.VK_6 -> {
                val index: Int = key.keyChar - '1'

                currentResolution = index
                changeToCurrentResolution()
            }

            KeyEvent.VK_0 -> {
                saveCurrentImageToFile()
                sender.clearKey(key)
            }

            KeyEvent.VK_RIGHT -> {
                rayTracer.camera.yawAngleInDegrees -= 5.0
                afterCameraUpdate(rayTracer.camera)
            }
            KeyEvent.VK_LEFT -> {
                rayTracer.camera.yawAngleInDegrees += 5.0
                afterCameraUpdate(rayTracer.camera)
            }
            KeyEvent.VK_UP -> {
                rayTracer.camera.pitchAngleInDegrees += 5.0
                afterCameraUpdate(rayTracer.camera)
            }
            KeyEvent.VK_DOWN -> {
                rayTracer.camera.pitchAngleInDegrees -= 5.0
                afterCameraUpdate(rayTracer.camera)
            }
            KeyEvent.VK_R -> {
                rayTracer.camera.rollAngleInDegrees += 5.0
                afterCameraUpdate(rayTracer.camera)
            }
            KeyEvent.VK_T -> {
                rayTracer.camera.rollAngleInDegrees -= 5.0
                afterCameraUpdate(rayTracer.camera)
            }

            KeyEvent.VK_W -> {
                rayTracer.camera.moveForward(step)
                afterCameraUpdate(rayTracer.camera)
            }
            KeyEvent.VK_S -> {
                rayTracer.camera.moveBackwards(step)
                afterCameraUpdate(rayTracer.camera)
            }
            KeyEvent.VK_D -> {
                rayTracer.camera.moveRight(step)
                afterCameraUpdate(rayTracer.camera)
            }
            KeyEvent.VK_A -> {
                rayTracer.camera.moveLeft(step)
                afterCameraUpdate(rayTracer.camera)
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

            KeyEvent.VK_L -> {
                if (rayTracer.world.ambientLight is AmbientOccluderAmbientLight) {
                    rayTracer.enableAmbientOcclusion(false)
                }
                else {
                    rayTracer.enableAmbientOcclusion(true)
                }
            }
        }
    }

    private fun afterCameraUpdate(camera: BaseCamera) {
        this.title = camera.toString()
    }
}
