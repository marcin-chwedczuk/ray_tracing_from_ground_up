package mc.raytracer.material.brdf

import mc.raytracer.math.Vector3D
import mc.raytracer.util.RgbColor
import mc.raytracer.util.ShadingInfo

/**
 * Bidirectional reflectance distribution function
 */
public interface BRDFFunction {
    fun evaluate(
            shadingInfo: ShadingInfo,
            incomingLight: Vector3D,
            outgoingLight: Vector3D): RgbColor

    /**
     * Defines how much of the received light should
     * be reflected in direction outgoingLight.
     */
    fun rho(shadingInfo: ShadingInfo, outgoingLight: Vector3D): RgbColor

    /**
     * Should be used when sampling (generating) incoming light directions that
     * later will be used to compute pixel color.
     */
    fun sample(shadingInfo: ShadingInfo, outgoingLight: Vector3D): Pair<RgbColor,Vector3D>
}
