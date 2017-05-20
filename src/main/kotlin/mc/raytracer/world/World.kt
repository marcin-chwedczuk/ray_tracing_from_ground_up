package mc.raytracer.world

import mc.raytracer.geometry.Sphere
import mc.raytracer.math.Point3D
import mc.raytracer.math.Ray
import mc.raytracer.math.Vector3D
import mc.raytracer.tracers.Tracer
import mc.raytracer.util.RawBitmap
import mc.raytracer.util.RgbColor

class World(
        val viewPlane: ViewPlane,
        val backgroundColor: RgbColor,
        val tracer: Tracer,
        val paintArea: RawBitmap
) {
    init {
        tracer.init(this)
    }

    var sphere: Sphere = Sphere(Point3D(0,0,0), 100.0)

    fun renderScene() {
        val pixelSize = viewPlane.pixelSize
        val vecZ = Vector3D(0.0,0.0,-1.0)

        val hres = viewPlane.horizontalResolution
        val vres = viewPlane.verticalResolution

        for (r in 0 until vres) {
            for (c in 0 until hres) {
                val x = pixelSize * (c - hres/2.0 + 0.5)
                val y = pixelSize * (r - vres/2.0 + 0.5)
                val z = 1000.0

                val ray = Ray(Point3D(x,y,z), vecZ)
                val color = tracer.traceRay(ray)
                displayPixel(r,c,color)
            }
        }
    }

    private fun displayPixel(vpRow: Int, vpCol: Int, color: RgbColor) {
        var colorToDisplay =
            if (viewPlane.showOutOfGamutErrors)
                color.orWhenOutOfRange(RgbColor.red)
            else
                color.scaleMaxToOne()

        if (viewPlane.gamma != 1.0)
           colorToDisplay = colorToDisplay.powComponents(viewPlane.gammaInv)

        val argb = colorToDisplay.toInt()

        // synchronized(paintArea) {
            paintArea.setPixel(vpRow, vpCol, argb)
        // }
    }
}