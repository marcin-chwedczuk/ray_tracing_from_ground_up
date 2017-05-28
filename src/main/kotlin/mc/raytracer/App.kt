package mc.raytracer

import mc.raytracer.cameras.PinholeCamera
import mc.raytracer.cameras.ThinLensCamera
import mc.raytracer.geometry.Cuboid
import mc.raytracer.geometry.Plane
import mc.raytracer.geometry.Sphere
import mc.raytracer.gui.MainWindow
import mc.raytracer.material.ChessboardMaterial
import mc.raytracer.material.StaticColorMaterial
import mc.raytracer.math.*
import mc.raytracer.sampling.CircleSampler
import mc.raytracer.sampling.MultiJitteredSampler
import mc.raytracer.tracers.SingleSphereTracer
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.ViewPlane
import mc.raytracer.world.World
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import java.util.*
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

    val lensSampler = CircleSampler.fromSquareSampler(
            MultiJitteredSampler(viewPlane.numerOfSamplesPerPixel))
    val camera = ThinLensCamera(bitmap, lensSampler)

    camera.viewPlaneDistance = 200.0
    camera.focalPlaneDistance = 450.0
    camera.lensRadius = 14.0
    // camera.eye = Point3D(0,0,-280)

    val world = World(viewPlane, RgbColor.black, tracer, camera)

    val rnd = Random()
    rnd.setSeed(123456)

    for (i in 1..80) {
        val sphere = Sphere(
                Point3D(-300+rnd.nextInt(700), -300+rnd.nextInt(700), -500+rnd.nextInt(800)),
                rnd.nextDouble()*60)

        sphere.material = StaticColorMaterial(RgbColor(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble()))
        world.addObject(sphere)
    }

    val sunny = Sphere(Point3D(0,800,0), 100.0)
    sunny.material = StaticColorMaterial(RgbColor.white)
    world.addObject(sunny)

   /* for(i in 1..16) {
        val box = Cuboid(Point3D(0,0,-300+i*40), length = 20.0, depth = 20.0, height = 100.0)
        box.material = ChessboardMaterial(RgbColor.black, RgbColor.randomColor(), patternSize=5.0)
        world.addObject(box)
    }

    for(i in 1..16) {
        val box = Cuboid(Point3D(30+i*40,0,-300), length = 20.0, depth = 20.0, height = 100.0)
        box.material = ChessboardMaterial(RgbColor.black, RgbColor.randomColor(), patternSize=5.0)
        world.addObject(box)
    } */

    val floor = Plane(Point3D(0.0,-300.01,0.0), Normal3D(0,1,0))
    floor.material = ChessboardMaterial(RgbColor.grayscale(0.97), RgbColor.black, patternSize=100.0)
    world.addObject(floor)

    //val box = Cuboid(Point3D(0,250,-300), 150.0)
    //box.material = ChessboardMaterial(RgbColor.black, RgbColor.red, patternSize=15.0)
    //world.addObject(box)

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
                        KeyEvent.VK_ESCAPE, KeyEvent.VK_Q -> {
                            SwingUtilities.invokeAndWait {
                                mainWindow.dispatchEvent(WindowEvent(mainWindow, WINDOW_CLOSING))
                            }
                            return
                        }

                        KeyEvent.VK_RIGHT -> {
                            camera.yawAngleInDegrees -= 5.0
                        }

                        KeyEvent.VK_LEFT -> {
                            camera.yawAngleInDegrees += 5.0
                        }

                        KeyEvent.VK_UP -> {
                            camera.pitchAngleInDegrees += 5.0
                        }

                        KeyEvent.VK_DOWN -> {
                            camera.pitchAngleInDegrees -= 5.0
                        }

                        KeyEvent.VK_W -> {
                            val lookAtVec = camera.cameraLookAt * step
                            camera.lookAt = camera.lookAt + lookAtVec
                            camera.eye = camera.eye + lookAtVec
                        }

                        KeyEvent.VK_S -> {
                            val lookAtVec = camera.cameraLookAt * step
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
                            //camera.fieldOfViewInDegrees += 10.0
                            camera.rollAngleInDegrees += 5.0
                        }
                        KeyEvent.VK_T -> {
                            //camera.fieldOfViewInDegrees -= 10.0
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
