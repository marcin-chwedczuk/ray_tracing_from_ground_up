package mc.raytracer.cameras

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

    // orthonormal camera coordinate base (enter at eye point)
    protected var u = Vector3D(1,0,0)
    protected var v = Vector3D(0,1,0)
    protected var w = Vector3D(0,0,1)

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
    }
}
