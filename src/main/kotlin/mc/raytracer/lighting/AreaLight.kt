package mc.raytracer.lighting

import mc.raytracer.geometry.GeometricObject
import mc.raytracer.geometry.SupportsSurfaceSampling
import mc.raytracer.material.EmissiveMaterial
import mc.raytracer.math.Normal3D
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public class AreaLight<out T>(val geometricObject: T)
    : LightWithShadowSupport
    where
        T: GeometricObject,
        T: SupportsSurfaceSampling
{
    override var castsShadows: Boolean = true

    private val material = (geometricObject.material as EmissiveMaterial)

    // TODO: @mc bad design refactor to stateless light
    private var samplePoint: Point3D = Point3D.zero
    private var normalAtSamplePoint: Normal3D = Normal3D.axisY
    private var wi: Vector3D = Vector3D.zero

    override fun computeDirectionFromHitPointToLight(shadingInfo: ShadingInfo): Vector3D {
        this.samplePoint = geometricObject.samplePoint()
        this.normalAtSamplePoint = geometricObject.getNormalAtPoint(this.samplePoint)

        this.wi = (this.samplePoint - shadingInfo.hitPoint).norm()
        return this.wi
    }

    override fun isHitPointInShadow(shadingInfo: ShadingInfo, shadowRay: Ray): Boolean {
        return shadingInfo.world.existsCastingShadowObjectInDirection(shadowRay, maxDistance=Double.MAX_VALUE)
    }

    override fun computeLuminanceContributedByLight(shadingInfo: ShadingInfo): RgbColor {
        // Light is emitted only in surface normal direction
        if ((-wi dot normalAtSamplePoint) < 0.0)
            return RgbColor.black

        return material.getRadiance()
    }

    override fun geometricFactor(info: ShadingInfo): Double {
        val ndotd = -wi dot normalAtSamplePoint
	    val d2 	= samplePoint.distanceToSquared(info.hitPoint)

	    val result = (ndotd / d2)
        return result
    }

    override fun monteCarloPdf(info: ShadingInfo): Double {
        return geometricObject.getPdfOfSample(samplePoint)
    }
}