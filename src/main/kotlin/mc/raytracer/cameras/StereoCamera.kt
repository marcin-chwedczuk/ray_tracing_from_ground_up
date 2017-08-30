package mc.raytracer.cameras

import mc.raytracer.math.*
import mc.raytracer.threading.CancelFlag
import mc.raytracer.util.RawBitmap
import mc.raytracer.world.World

class StereoCamera<out CameraType : BaseCamera>(
        val leftCamera: CameraType,
        val rightCamera: CameraType)
    : BaseCamera()
{

    init {
        if (leftCamera === rightCamera)
            throw IllegalArgumentException(
                    "Since left and right cameras will have different coordinates, " +
                    "they must be two distinct instances.")
    }

    var viewingMode: ViewingMode = ViewingMode.TRANSVERSE
    var stereoSeparationAngle: Angle = Angle.fromDegrees(15)
        set(value) {
            field = value
            computeUvw()
        }

    var pixelGap = 8
        set(newGap) {
            if (newGap < 0)
                throw IllegalArgumentException("newGap cannot be less than ZERO.")

            field = newGap
        }

    protected override fun afterUvwComputed() {
        val x = computeHalfCameraSeparation()

        adjustCamera(leftCamera, -x)
        adjustCamera(rightCamera, x)
    }

    private fun adjustCamera(camera: CameraType, dx: Double) {
        camera.eye = eye + dx*cameraRight
        camera.lookAt = eye + cameraLookAt + dx*cameraRight
        camera.up = cameraUp
        camera.yawAngleInDegrees = 0.0
        camera.rollAngleInDegrees = 0.0
        camera.pitchAngleInDegrees = 0.0

        if (camera is PinholeCamera) {
            camera.fieldOfViewInDegrees = 30.0
        }
    }

    override fun render(world: World, canvas: RawBitmap, cancelFlag: CancelFlag) {
        val halfCameraSeparation = computeHalfCameraSeparation()

        if (viewingMode == ViewingMode.PARALLEL) {
            leftCamera.renderStereo(world, canvas, cancelFlag,
                    viewPortOffsetX = halfCameraSeparation)

            if (cancelFlag.shouldCancel)
                return

            rightCamera.renderStereo(world, canvas, cancelFlag,
                    viewPortOffsetX = -halfCameraSeparation,
                    canvasOffsetX = world.viewPlane.horizontalResolution + pixelGap)
        }
        else {
            rightCamera.renderStereo(world, canvas, cancelFlag,
                    viewPortOffsetX = -halfCameraSeparation)

            if (cancelFlag.shouldCancel)
                return

            leftCamera.renderStereo(world, canvas, cancelFlag,
                    viewPortOffsetX = halfCameraSeparation,
                    canvasOffsetX = world.viewPlane.horizontalResolution + pixelGap)
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

    public fun forEachEyeCamera(action: CameraType.() -> Unit) {
        leftCamera.action()
        rightCamera.action()
    }
}

enum class ViewingMode {
    // left picture is for left eye
    PARALLEL,

    // left picture is for right eye (for cross eye-viewing)
    TRANSVERSE
}
