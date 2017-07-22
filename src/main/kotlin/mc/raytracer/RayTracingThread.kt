package mc.raytracer

import mc.raytracer.threading.CancelFlag
import mc.raytracer.util.RawBitmap

class RayTracingThread(val rayTracer: RayTracer)
    : Thread() {

    private val fieldLock = Any()

    private val cancelRenderingFlag = CancelFlag()
    private val cancelThreadFlag = CancelFlag()

    private lateinit var bitmap: RawBitmap

    init {
        isDaemon = true
    }

    fun changeBitmap(bitmap: RawBitmap) {
        cancelRenderingFlag.raise()

        synchronized(fieldLock) {
            this.bitmap = bitmap

            this.rayTracer.viewPlane.changeResolutionPreservingViewPlaneWidth(
                    bitmap.width, bitmap.heigh)
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
