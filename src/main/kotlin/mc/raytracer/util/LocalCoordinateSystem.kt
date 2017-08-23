package mc.raytracer.util

import mc.raytracer.math.Normal3D
import mc.raytracer.math.Vector3D

public class LocalCoordinateSystem {
    public val u: Normal3D
    public val v: Normal3D

    /**
     * Vector that points in the same direction as normal from which
     * this local coordinate system was created.
     */
    public val w: Normal3D

    private constructor(u: Normal3D, w: Normal3D, v: Normal3D) {
        this.u = u
        this.w = w
        this.v = v
    }

    companion object {
        public fun fromNormal(n: Normal3D): LocalCoordinateSystem {
            val w = n
            val v = Normal3D.fromVector(w cross Vector3D(0.0072, 1.0, 0.0034))
            val u = Normal3D.fromVector(v cross w)

            return LocalCoordinateSystem(u,w,v)
        }
    }
}
