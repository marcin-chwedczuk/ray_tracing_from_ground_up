package mc.raytracer.geometry

import mc.raytracer.math.Angle
import mc.raytracer.math.Matrix4

public class InstanceBuilder(
        val geometricObject: GeometricObject)
{
    private var matrixInverse: Matrix4 = Matrix4.IDENTITY

    public fun translate(dx: Double, dy: Double, dz: Double): InstanceBuilder {
        matrixInverse *= Matrix4.translateInverse(dx, dy, dz)
        return this
    }

    public fun rotateX(angle: Angle): InstanceBuilder {
        matrixInverse *= Matrix4.rotateXInverse(angle)
        return this
    }

    public fun rotateY(angle: Angle): InstanceBuilder {
        matrixInverse *= Matrix4.rotateYInverse(angle)
        return this
    }

    public fun rotateZ(angle: Angle): InstanceBuilder {
        matrixInverse *= Matrix4.rotateZInverse(angle)
        return this
    }

    public fun scale(sx: Double, sy: Double, sz: Double): InstanceBuilder {
        matrixInverse *= Matrix4.scaleInverse(sx, sy, sz)
        return this
    }

    public fun sheare(
            hyx: Double = 0.0, hzx: Double = 0.0,
            hxy: Double = 0.0, hzy: Double = 0.0,
            hxz: Double = 0.0, hyz: Double = 0.0): InstanceBuilder {
        matrixInverse *= Matrix4.shearInverse(hyx, hzx, hxy, hzy, hxz, hyz)
        return this
    }

    public fun create(): Instance {
        return Instance(geometricObject, matrixInverse)
    }
}