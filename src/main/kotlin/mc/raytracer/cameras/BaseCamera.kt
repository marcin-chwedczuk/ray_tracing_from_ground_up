package mc.raytracer.cameras

import mc.raytracer.geometry.GeometricObject.Companion.K_EPSILON
import mc.raytracer.math.Matrix4
import mc.raytracer.math.Point3D
import mc.raytracer.math.Vector3D
import mc.raytracer.threading.CancelFlag
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor
import mc.raytracer.world.ViewPlane
import mc.raytracer.world.World

abstract class BaseCamera {

    var eye: Point3D = Point3D(0,0,20)
        set(newValue) {
            field = newValue
            computeUvw()
        }

    var lookAt: Point3D = Point3D.zero
        set(newValue) {
            field = newValue
            computeUvw()
        }

    var up: Vector3D = Vector3D(0,1,0)
        set(newValue) {
            field = newValue
            computeUvw()
        }

    var exposureTime: Double = 1.0

    // camera tilt
    var rollAngleInDegrees: Double = 0.0
        set(value) {
            field = value
            computeUvw()
        }

    // useful to look at the sky or at the ground
    var pitchAngleInDegrees: Double = 0.0
        set(value) {
            field = value
            computeUvw()
        }

    // look at the left or at the right
    var yawAngleInDegrees: Double = 0.0
        set(value) {
            field = value
            computeUvw()
        }

    // orthonormal camera coordinate base (enter at eye point)
    // camera right vector
    protected var u = Vector3D(1,0,0)
    val cameraRight: Vector3D
        get() = u

    // camera up vector
    protected var v = Vector3D(0,1,0)
    val cameraUp: Vector3D
        get() = v

    // look at unit vector (from lookAt point to eye)
    protected var w = Vector3D(0,0,1)
    val cameraLookAt: Vector3D
        get() = -w

    fun setViewDirection(vec: Vector3D) {
        val newLookAt = eye + vec.norm()
        lookAt = newLookAt
        computeUvw()
    }

    protected fun computeUvw() {
        w = (eye - lookAt).norm()
        u = (up cross w).norm()
        v = (w cross u).norm()

        // take care of the singularity by hardwiring in specific camera orientations

        // camera looking vertically down
        if (eye.x == lookAt.x && eye.z == lookAt.z && eye.y > lookAt.y) {
            u = Vector3D(0, 0, 1)
            v = Vector3D(1, 0, 0)
            w = Vector3D(0, 1, 0)
        }

        // camera looking vertically up
        if (eye.x == lookAt.x && eye.z == lookAt.z && eye.y < lookAt.y) {
            u = Vector3D(1, 0, 0)
            v = Vector3D(0, 0, 1)
            w = Vector3D(0, -1, 0)
        }

        // rotate camera - order of operations is important
        var rotationMatrix = Matrix4.identity

        if (Math.abs(rollAngleInDegrees) > K_EPSILON) {
            rotationMatrix *=
                    Matrix4.rotationMatrix(w, rollAngleInDegrees)
        }

        if (Math.abs(yawAngleInDegrees) > K_EPSILON) {
            rotationMatrix *=
                    Matrix4.rotationMatrix(v, yawAngleInDegrees)
        }

        if (Math.abs(pitchAngleInDegrees) > K_EPSILON) {
            rotationMatrix *=
                    Matrix4.rotationMatrix(u, pitchAngleInDegrees)
        }

        w = (rotationMatrix*w).norm()
        u = (rotationMatrix*u).norm()
        v = (rotationMatrix*v).norm()

        afterUvwComputed()
    }

    protected open fun afterUvwComputed() { }

    abstract fun render(
            world: World, canvas: RawBitmap, cancelFlag: CancelFlag)

    open fun renderStereo(
            world: World, canvas: RawBitmap, cancelFlag: CancelFlag,
            viewPortOffsetX: Double = 0.0, viewPortOffsetY: Double = 0.0,
            canvasOffsetX: Int = 0, canvasOffsetY: Int = 0) {
        throw NotImplementedError("This camera doesn't support rendering stereo.")
    }

    fun moveForward(distance: Double) {
        val lookAtVec = cameraLookAt * distance

        lookAt += lookAtVec
        eye += lookAtVec
    }

    fun moveBackwards(distance: Double) {
        moveForward(-distance)
    }

    fun moveRight(distance: Double) {
        val delta = cameraRight*distance
        eye += delta
        lookAt += delta
    }

    fun moveLeft(distance: Double) {
        moveRight(-distance)
    }

    open fun minNeededHorizontalPixels(world: World): Int {
        return world.viewPlane.horizontalResolution
    }

    open fun minNeededVerticalPixels(world: World): Int {
        return world.viewPlane.verticalResolution
    }

    fun copyPositionAndOrientationFrom(other: BaseCamera) {
        this.eye = other.eye
        this.lookAt = other.lookAt
        this.up = other.up

        this.rollAngleInDegrees = other.rollAngleInDegrees
        this.pitchAngleInDegrees = other.pitchAngleInDegrees
        this.yawAngleInDegrees = other.yawAngleInDegrees
    }

    override fun toString(): String {
        return "camera(pos: $eye, look at: $lookAt)"
    }
}
