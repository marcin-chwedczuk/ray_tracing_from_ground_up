package mc.raytracer.geometry

import mc.raytracer.math.Angle
import mc.raytracer.math.Matrix4
import mc.raytracer.math.Point3D
import mc.raytracer.math.Vector3D

public class InstanceBuilder(
        val geometricObject: GeometricObject)
{
    private var matrix: Matrix4 = Matrix4.IDENTITY
    private var matrixInverse: Matrix4 = Matrix4.IDENTITY

    public fun translate(dx: Double = 0.0, dy: Double = 0.0, dz: Double = 0.0): InstanceBuilder {
        matrix = Matrix4.translate(dx, dy, dz) * matrix
        matrixInverse *= Matrix4.translateInverse(dx, dy, dz)
        return this
    }

    public fun translate(vec: Vector3D): InstanceBuilder {
        return translate(vec.x, vec.y, vec.z)
    }

    public fun rotateX(angle: Angle): InstanceBuilder {
        matrix = Matrix4.rotateX(angle) * matrix
        matrixInverse *= Matrix4.rotateXInverse(angle)
        return this
    }

    public fun rotateY(angle: Angle): InstanceBuilder {
        matrix = Matrix4.rotateY(angle) * matrix
        matrixInverse *= Matrix4.rotateYInverse(angle)
        return this
    }

    public fun rotateZ(angle: Angle): InstanceBuilder {
        matrix = Matrix4.rotateZ(angle) * matrix
        matrixInverse *= Matrix4.rotateZInverse(angle)
        return this
    }

    public fun scale(sx: Double, sy: Double, sz: Double): InstanceBuilder {
        matrix = Matrix4.scale(sx, sy, sz) * matrix
        matrixInverse *= Matrix4.scaleInverse(sx, sy, sz)
        return this
    }

    public fun sheare(
            hyx: Double = 0.0, hzx: Double = 0.0,
            hxy: Double = 0.0, hzy: Double = 0.0,
            hxz: Double = 0.0, hyz: Double = 0.0): InstanceBuilder {
        matrix = Matrix4.shear(hyx, hzx, hxy, hzy, hxz, hyz) * matrix
        matrixInverse *= Matrix4.shearInverse(hyx, hzx, hxy, hzy, hxz, hyz)
        return this
    }

    public fun create(): Instance {
        return Instance(geometricObject, matrixInverse, matrix)
    }
}