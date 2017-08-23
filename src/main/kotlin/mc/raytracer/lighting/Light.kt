package mc.raytracer.lighting

import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

public interface Light {
    val generatesShadows: Boolean

    fun computeHitPointLightingAttributes(shadingInfo: ShadingInfo)
            : HitPointLightingAttributes
}

public interface HitPointLightingAttributes {
    val toLightDirection: Vector3D

    fun isHitPointInShadow(shadowRay: Ray): Boolean

    fun radiance(): RgbColor

    /**
     * Geometric factor for selected sample point.
     */
    fun samplePointGeometricFactor(): Double {
        return 1.0
    }

    /**
     * Probability density function value for selected sample point.
     */
    fun samplePointPdf(): Double {
        return 1.0
    }
}
