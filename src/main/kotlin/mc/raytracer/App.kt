package mc.raytracer

import mc.raytracer.cameras.PinholeCamera
import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.SinX2Y2Function
import mc.raytracer.geometry.Sphere
import mc.raytracer.gui.MainWindow
import mc.raytracer.material.ChessboardMaterial
import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.*
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.sampling.HammersleySampler
import mc.raytracer.sampling.HemisphereSampler
import mc.raytracer.sampling.NRooksSampler
import mc.raytracer.tracers.SingleSphereTracer
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.ViewPlane
import mc.raytracer.world.World
import org.omg.PortableServer.POA
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities
import java.awt.event.WindowEvent.WINDOW_CLOSING



fun main(args: Array<String>) {
    val scale = 2
    val bitmap = RawBitmap(800/scale,640/scale)
    bitmap.clear(0xFFFF69B4.toInt())

    val mainWindow = MainWindow()
    SwingUtilities.invokeLater {
        mainWindow.isVisible = true
        mainWindow.setBitmap(bitmap)
    }

    val pixelSize = 800.0/bitmap.width
    val viewPlane = ViewPlane(bitmap.width, bitmap.heigh, pixelSize=pixelSize)

    // val nrooksSampler = NRooksSampler(3)
    //viewPlane.useSampler(nrooksSampler)
    // viewPlane.useSampler(HammersleySampler(16))

    viewPlane.setNumberOfSamples(4)

    val tracer = SingleSphereTracer()
    val camera = PinholeCamera(bitmap)
    camera.viewPlaneDistance = 400.0


    val world = World(viewPlane, RgbColor.black, tracer, camera)

    val rnd = Random()
    rnd.setSeed(123456)

    for (i in 1..40) {
        val sphere = Sphere(
                Point3D(-300+rnd.nextInt(700), -300+rnd.nextInt(700), -500+rnd.nextInt(300)),
                rnd.nextDouble()*130)

        sphere.material = StaticColorMaterial(RgbColor(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble()))
        world.addObject(sphere)
    }

    val zero = Sphere(Point3D(0,0,0), 100.0)
    zero.material = StaticColorMaterial(RgbColor.yellow)
    world.addObject(zero)

    val floor = Plane(Point3D(0,-300,0), Normal3D(0,1,0))
    floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize=100.0)
    world.addObject(floor)

    /*val tmp = pixelSize*Math.min(bitmap.width, bitmap.heigh) / 2
    val `fun` = SinX2Y2Function(Point2D(-tmp,tmp), Point2D(tmp,-tmp))
    world.addObject(`fun`) */

    val thread = object: Thread() {
        private val rand = Random()

        private fun generateBitmap() {
            camera.render(world)
        }

        override fun run() {
            var rotateRight = Matrix4.rotationMatrix(Vector3D.axisY, angleInDegrees=-5.0)
            var rotateLeft = Matrix4.rotationMatrix(Vector3D.axisY, angleInDegrees=5.0)

            while (true) {
                Thread.sleep(10)

                var keyCode: Int? = mainWindow.keyCodeQueue.poll()
                if (keyCode == null) {
                     generateBitmap()
                }
                else {
                    val step = 37.0
                    when (keyCode) {
                        KeyEvent.VK_Q -> {
                            SwingUtilities.invokeAndWait {
                                mainWindow.dispatchEvent(WindowEvent(mainWindow, WINDOW_CLOSING))
                            }
                            return
                        }

                        KeyEvent.VK_RIGHT -> {
                            val lookAtVec = camera.lookAt - camera.eye
                            val rotatedLookAtVec = rotateRight*lookAtVec
                            camera.lookAt = camera.eye + rotatedLookAtVec
                        }

                        KeyEvent.VK_LEFT -> {
                            val lookAtVec = camera.lookAt - camera.eye
                            val rotatedLookAtVec = rotateLeft*lookAtVec
                            camera.lookAt = camera.eye + rotatedLookAtVec
                        }

                        KeyEvent.VK_UP -> {
                            val lookAtVec = (camera.lookAt - camera.eye).norm() * step
                            camera.lookAt = camera.lookAt + lookAtVec
                            camera.eye = camera.eye + lookAtVec
                        }

                        KeyEvent.VK_DOWN -> {
                            val lookAtVec = (camera.lookAt - camera.eye).norm() * step
                            camera.lookAt = camera.lookAt - lookAtVec
                            camera.eye = camera.eye - lookAtVec
                        }

                        KeyEvent.VK_D -> {
                            val delta = camera.cameraRight*step
                            camera.eye = camera.eye + delta
                            camera.lookAt = camera.lookAt + delta
                        }

                        KeyEvent.VK_A -> {
                            val delta = -camera.cameraRight*step
                            camera.eye = camera.eye + delta
                            camera.lookAt = camera.lookAt + delta
                        }

                        KeyEvent.VK_R -> {
                            camera.rollAngleInDegrees += 5.0
                        }
                        KeyEvent.VK_T -> {
                            camera.rollAngleInDegrees -= 5.0
                        }
                    }

                    println("eye: ${camera.eye}, lookAt: ${camera.lookAt}")
                }
            }
        }
    }

    thread.isDaemon=true
    thread.run()
}
