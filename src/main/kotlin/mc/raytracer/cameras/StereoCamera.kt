package mc.raytracer.cameras

import mc.raytracer.math.*
import mc.raytracer.threading.CancelFlag
import mc.raytracer.util.RawBitmap
import mc.raytracer.world.World

class StereoCamera(
        val leftCamera: BaseCamera,
        val rightCamera: BaseCamera)
    : BaseCamera() {

    var viewingMode: ViewingMode = ViewingMode.TRANSVERSE
    var stereoSeparationAngle: Angle = Angle.fromDegrees(15)

    var pixelGap = 8
        set(newGap) {
            if (newGap < 0)
                throw IllegalArgumentException("newGap cannot be less than zero.")

            field = newGap
        }

    protected override fun afterUvwComputed() {
        val x = computeHalfCameraSeparation()

        leftCamera.eye = eye - x*cameraRight
        leftCamera.lookAt = Point3D.zero + (cameraLookAt - x*cameraRight)
        leftCamera.up = cameraUp

        rightCamera.eye = eye + x*cameraRight
        rightCamera.lookAt = Point3D.zero + (cameraLookAt + x*cameraRight)
        rightCamera.up = cameraUp
    }

    override fun render(world: World, canvas: RawBitmap, cancelFlag: CancelFlag) {
        val x = computeHalfCameraSeparation()

        if (viewingMode == ViewingMode.PARALLEL) {
            leftCamera.renderStereo(world, canvas, cancelFlag,
                    0.0, 0.0, 0, 0)

            if (cancelFlag.shouldCancel)
                return

            rightCamera.renderStereo(world, canvas, cancelFlag,
                    0.0, 0.0, world.viewPlane.horizontalResolution + pixelGap, 0)
        }
        else {
            leftCamera.renderStereo(world, canvas, cancelFlag,
                    0.0, 0.0, 0, 0)

            if (cancelFlag.shouldCancel)
                return

            rightCamera.renderStereo(world, canvas, cancelFlag,
                    0.0, 0.0, world.viewPlane.horizontalResolution + pixelGap, 0)
        }
    }

    private fun computeHalfCameraSeparation(): Double {
        val r = eye.distanceTo(lookAt)
        val x = r * (stereoSeparationAngle / 2).tan()

        return x
    }

    override fun minNeededHorizontalPixels(world: World): Int {
        return leftCamera.minNeededHorizontalPixels(world) +
            pixelGap +
            rightCamera.minNeededHorizontalPixels(world)
    }

    override fun minNeededVerticalPixels(world: World): Int {
        return Math.max(
                leftCamera.minNeededVerticalPixels(world),
                rightCamera.minNeededVerticalPixels(world))
    }
}

enum class ViewingMode {
    // left picture is for left eye
    PARALLEL,

    // left picture is for right eye (for cross eye-viewing)
    TRANSVERSE
}
