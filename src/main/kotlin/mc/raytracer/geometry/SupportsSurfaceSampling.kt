package mc.raytracer.geometry

import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D

public interface SupportsSurfaceSampling {
    fun selectSamplePoint(): Point3D

    /**
     * @return Probability density function (pdf) value for sample point.
     */
    fun pdfOfSamplePoint(point: Point3D): Double

    fun normalAtSamplePoint(point: Point3D): Normal3D
}
