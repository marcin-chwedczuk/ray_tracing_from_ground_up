package mc.raytracer

import mc.raytracer.cameras.StereoCamera
import mc.raytracer.cameras.ViewingMode
import mc.raytracer.math.Angle
import mc.raytracer.threading.CancelFlag
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.Resolution
import mc.raytracer.util.StereoResolution

class RayTracingThread(val rayTracer: RayTracer)
    : Thread() {

    private val fieldLock = Any()

    private val cancelRenderingFlag = CancelFlag()
    private val cancelThreadFlag = CancelFlag()

    private lateinit var bitmap: RawBitmap

    init {
        isDaemon = true
    }

    fun changeResolution(bitmap: RawBitmap, viewPlaneResolution: Resolution) {
        cancelRenderingFlag.raise()

        synchronized(fieldLock) {
            this.bitmap = bitmap

            rayTracer.viewPlane.changeResolutionPreservingViewPlaneWidth(
                    viewPlaneResolution.viewPortHorizontal,
                    viewPlaneResolution.viewPortVertical)

            if (viewPlaneResolution is StereoResolution) {
                val stereoCamera = rayTracer.enableStereoMode()
                stereoCamera.pixelGap = viewPlaneResolution.pixelGap
                stereoCamera.stereoSeparationAngle = Angle.fromDegrees(4.75)

                stereoCamera.forEachEyeCamera {
                    viewPlaneDistance = 80.0
                    fieldOfViewInDegrees = 10.0
                }
                // stereoCamera.viewingMode = ViewingMode.PARALLEL
            }
            else {
                rayTracer.disableStereoMode()
            }
        }
    }

    fun cancelThreadAndWait() {
        cancelThreadFlag.raise()
        join()
    }

    fun updateRayTracer(action: (RayTracer)->Unit) {
        cancelRenderingFlag.raise()

        synchronized(fieldLock) {
            action(rayTracer)
        }
    }

    override fun run() {
        while(!cancelThreadFlag.shouldCancel) {
            Thread.sleep(10)

            cancelRenderingFlag.clear()
            synchronized(fieldLock) {
                rayTracer.render(bitmap, cancelRenderingFlag)
            }
        }
    }

}
