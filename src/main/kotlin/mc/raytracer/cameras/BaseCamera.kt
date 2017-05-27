package mc.raytracer.cameras

import mc.raytracer.geometry.GeometricObject.Companion.K_EPSILON
import mc.raytracer.math.Matrix4
import mc.raytracer.math.Point3D
import mc.raytracer.math.Vector3D

abstract class BaseCamera {
    var eye: Point3D = Point3D(0,0,500)
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

    // camera rotation around eye-lookAt axis
    var rollAngleInDegrees: Double = 0.0
        set(value) {
            field = value
            computeUvw()
        }

    // orthonormal camera coordinate base (enter at eye point)
    // camera right vector
    protected var u = Vector3D(1,0,0)

    // camera up vector
    protected var v = Vector3D(0,1,0)

    // look at unit vector (from lookAt point to eye)
    protected var w = Vector3D(0,0,1)

    val cameraRight: Vector3D
        get() = u

    val cameraUp: Vector3D
        get() = v

    protected fun computeUvw() {
        w = (eye - lookAt).norm()

        u = (up cross w).norm()
        // if roll angle is set, rotate u around w axis
        if (Math.abs(rollAngleInDegrees) > K_EPSILON) {
            val rollRotationMatrix =
                    Matrix4.rotationMatrix(w, rollAngleInDegrees)
            u = (rollRotationMatrix*u).norm()
        }

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
    }
}
