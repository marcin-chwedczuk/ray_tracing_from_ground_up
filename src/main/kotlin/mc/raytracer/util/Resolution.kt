package mc.raytracer.util

open class Resolution(
        val viewPortHorizontal: Int,
        val viewPortVertical: Int,

        val canvasHorizontal: Int = viewPortHorizontal,
        val canvasVertical: Int = viewPortVertical,

        val scale: Int = 1,
        val windowHorizontal: Int = canvasHorizontal*scale,
        val windowVertical: Int = canvasVertical*scale)

class StereoResolution(
        singleEyeCanvasHorizontal: Int,
        singleEyeCanvasVertical: Int,
        val pixelGap: Int,
        scale: Int = 1)
    : Resolution(
        viewPortHorizontal = singleEyeCanvasHorizontal,
        viewPortVertical = singleEyeCanvasVertical,
        canvasHorizontal = singleEyeCanvasHorizontal*2+pixelGap,
        canvasVertical = singleEyeCanvasVertical,
        windowHorizontal = (singleEyeCanvasHorizontal*2+pixelGap) * scale,
        windowVertical = singleEyeCanvasVertical * scale)