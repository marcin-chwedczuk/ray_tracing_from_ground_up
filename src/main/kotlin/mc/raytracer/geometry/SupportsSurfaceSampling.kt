package mc.raytracer.geometry

import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D

public interface SupportsSurfaceSampling {
    fun samplePoint(): Point3D

    /**
     * @return Probability density function (pdf) value for sample point.
     */
    fun getPdfOfSample(point: Point3D): Double

    fun getNormalAtPoint(point: Point3D): Normal3D
}
