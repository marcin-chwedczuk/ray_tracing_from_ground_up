package mc.raytracer.geometry.acceleration

import mc.raytracer.geometry.*
import mc.raytracer.math.Ray
import mc.raytracer.util.BoundingBox
import mc.raytracer.util.clamp
import mc.raytracer.util.clampToInt
import java.lang.IllegalStateException


public class AccelerationGrid(
        val multiplier: Int = 2
): GeometricObject() {
    private var initialized = false

    private val geometricObjects = mutableListOf<GeometricObject>()
    private var _boundingBox = BoundingBox.EMPTY

    private lateinit var cells: MutableList<GeometricObject?>
    private var nx = 0
    private var ny = 0
    private var nz = 0

    public fun addObject(obj: GeometricObject) {
        if (initialized)
            throw IllegalStateException("Grid cannot be changed after initialization.")

        geometricObjects.add(obj)
    }

    public fun initialize() {
        _boundingBox = computeBoundingBox()
        setupCells()

        for (obj in geometricObjects) {
            linkCellsToObject(obj)
        }

        initialized = true
    }

    private fun computeBoundingBox(): BoundingBox {
        val BOX_DELTA = 1e-6

        return geometricObjects
                .map { it.boundingBox }
                .fold(BoundingBox.EMPTY) { acc, elem -> acc.merge(elem) }
                .extend(BOX_DELTA)
    }

    private fun setupCells() {
        val dx = _boundingBox.dx
        val dy = _boundingBox.dy
        val dz = _boundingBox.dz

	    val s = Math.cbrt(dx*dy*dz / geometricObjects.size)

        nx = (multiplier*dx/s + 1).toInt()
        ny = (multiplier*dy/s + 1).toInt()
        nz = (multiplier*dz/s + 1).toInt()

        cells = MutableList(nx*ny*nz, { null })
    }

    private fun linkCellsToObject(obj: GeometricObject) {
        val box = obj.boundingBox

		val ixMin = ((box.xMin-_boundingBox.xMin) * nx / (_boundingBox.xMax-_boundingBox.xMin)).clampToInt(0, nx - 1)
		val ixMax = ((box.xMax-_boundingBox.xMin) * nx / (_boundingBox.xMax-_boundingBox.xMin)).clampToInt(0, nx - 1)

        val iyMin = ((box.yMin-_boundingBox.yMin) * ny / (_boundingBox.yMax-_boundingBox.yMin)).clampToInt(0, ny - 1)
        val iyMax = ((box.yMax-_boundingBox.yMin) * ny / (_boundingBox.yMax-_boundingBox.yMin)).clampToInt(0, ny - 1)

        val izMin = ((box.zMin-_boundingBox.zMin) * nz / (_boundingBox.zMax-_boundingBox.zMin)).clampToInt(0, nz - 1)
        val izMax = ((box.zMax-_boundingBox.zMin) * nz / (_boundingBox.zMax-_boundingBox.zMin)).clampToInt(0, nz - 1)

        for (ix in ixMin..ixMax) {
            for (iy in iyMin..iyMax) {
                for (iz in izMin..izMax) {

                    val cellValue = getCell(ix, iy, iz)
                    if (cellValue === null) {
                        setCell(ix, iy, iz, obj)
                    }
                    else if (cellValue is CellBucket) {
                        cellValue.addObject(obj)
                    }
                    else {
                        // convert single object to bucket
                        setCell(ix, iy, iz, CellBucket().apply {
                            addObject(cellValue)
                            addObject(obj)
                        })
                    }
                }
            }
        }

        for (ix in ixMin..ixMax) {
            for (iy in iyMin..iyMax) {
                for (iz in izMin..izMax) {
                    val tmp = getCell(ix, iy, iz)
                    if (tmp is CellBucket && tmp.size > 1000) {
                        System.out.println("BUCKET $ix, $iy, $iz, size: ${tmp.size}")
                    }
                }
            }
        }
    }

    private inline fun getCell(ix: Int, iy: Int, iz: Int): GeometricObject? {
        val index = ix*ny*nz + iy*nz + iz
        return cells[index]
    }

    private inline fun setCell(ix: Int, iy: Int, iz: Int, obj: GeometricObject?) {
        val index = ix*ny*nz + iy*nz + iz
        cells[index] = obj
    }

    override val boundingBox: BoundingBox
        get() = _boundingBox


    private inline fun <HIT_RESULT> hitAlgorithmTemplate(
            tryHit: (GeometricObject?, Ray) -> HIT_RESULT,
            isHit: (HIT_RESULT) -> Boolean,
            extractT: (HIT_RESULT) -> Double,
            missResult: HIT_RESULT,
            ray: Ray
    ): HIT_RESULT {

        assertInitialized()

        // INTERSECT RAY WITH THE BOUNDING BOX AND OBTAIN MIN_T -----------------------
        val origin = ray.origin
        val direction = ray.direction

        // the following code includes modifications from Shirley and Morley (2003)
        val a = 1.0 / direction.x

        val tx_min:Double; val tx_max: Double
        if (a >= 0) {
            tx_min = (_boundingBox.xMin - origin.x) * a
            tx_max = (_boundingBox.xMax - origin.x) * a
        }
        else {
            tx_min = (_boundingBox.xMax - origin.x) * a
            tx_max = (_boundingBox.xMin - origin.x) * a
        }

        val b = 1.0 / direction.y

        val ty_min: Double; val ty_max: Double
        if (b >= 0) {
            ty_min = (_boundingBox.yMin - origin.y) * b
            ty_max = (_boundingBox.yMax - origin.y) * b
        }
        else {
            ty_min = (_boundingBox.yMax - origin.y) * b
            ty_max = (_boundingBox.yMin - origin.y) * b
        }

        val c = 1.0 / direction.z

        val tz_min: Double; val tz_max: Double
        if (c >= 0) {
            tz_min = (_boundingBox.zMin - origin.z) * c
            tz_max = (_boundingBox.zMax - origin.z) * c
        }
        else {
            tz_min = (_boundingBox.zMax - origin.z) * c
            tz_max = (_boundingBox.zMin - origin.z) * c
        }

        val t0 = Math.max(tx_min, Math.max(ty_min, tz_min))
        val t1 = Math.min(tx_max, Math.min(ty_max, tz_max))

        // NO INTERSECTION
        if (t0 > t1)
            return missResult

        // COMPUTE RAY START IN GRID -----------------------------------

        // ix,iy,iz - indexes of the first cell on ray way into the grid
        var ix: Int; var iy: Int; var iz: Int

        if (_boundingBox.isInside(origin)) {
            ix = ((origin.x - _boundingBox.xMin) * nx / _boundingBox.dx).clampToInt(0, nx - 1)
            iy = ((origin.y - _boundingBox.yMin) * ny / _boundingBox.dy).clampToInt(0, ny - 1)
            iz = ((origin.z - _boundingBox.zMin) * nz / _boundingBox.dz).clampToInt(0, nz - 1)
        }
        else {
            val enterPoint = ray.pointOnRayPath(t0)
            ix = ((enterPoint.x - _boundingBox.xMin) * nx / _boundingBox.dx).clampToInt(0, nx - 1)
            iy = ((enterPoint.y - _boundingBox.yMin) * ny / _boundingBox.dy).clampToInt(0, ny - 1)
            iz = ((enterPoint.z - _boundingBox.zMin) * nz / _boundingBox.dz).clampToInt(0, nz - 1)
        }

        // COMPUTE INCREMENTS ------------------------------------------

        val dtx = (tx_max - tx_min) / nx
        val dty = (ty_max - ty_min) / ny
        val dtz = (tz_max - tz_min) / nz

        var tx_next: Double; var ty_next: Double; var tz_next: Double
        val ix_step: Int; val iy_step: Int; val iz_step: Int
        val ix_stop: Int; val iy_stop: Int; val iz_stop: Int

        if (direction.x > 0) {
            tx_next = tx_min + (ix + 1) * dtx
            ix_step = +1
            ix_stop = nx
        }
        else if (direction.x < 0) {
            tx_next = tx_min + (nx - ix) * dtx
            ix_step = -1
            ix_stop = -1
        }
        else {
            tx_next = Double.MAX_VALUE
            ix_step = -1
            ix_stop = -1
        }

        if (direction.y > 0) {
            ty_next = ty_min + (iy + 1) * dty
            iy_step = +1
            iy_stop = ny
        }
        else if (direction.y < 0) {
            ty_next = ty_min + (ny - iy) * dty
            iy_step = -1
            iy_stop = -1
        }
        else {
            ty_next = Double.MAX_VALUE
            iy_step = -1
            iy_stop = -1
        }

        if (direction.z > 0) {
            tz_next = tz_min + (iz + 1) * dtz
            iz_step = +1
            iz_stop = nz
        }
        else if (direction.z < 0) {
            tz_next = tz_min + (nz - iz) * dtz
            iz_step = -1
            iz_stop = -1
        }
        else {
            tz_next = Double.MAX_VALUE
            iz_step = -1
            iz_stop = -1
        }

        // TRAVERSE THE GRID --------------------------------------

        while (true) {
            val obj = getCell(ix, iy, iz)
            val hitResult: HIT_RESULT = tryHit(obj, ray)

            if (tx_next < ty_next && tx_next < tz_next) {
                if (isHit(hitResult) && (extractT(hitResult) < tx_next)) {
                    return hitResult
                }

                tx_next += dtx
                ix += ix_step

                if (ix == ix_stop)
                    return missResult

            } else if (ty_next < tz_next) {
                if (isHit(hitResult) && (extractT(hitResult) < ty_next)) {
                    return hitResult
                }

                ty_next += dty
                iy += iy_step

                if (iy == iy_stop)
                    return missResult

            } else {
                if (isHit(hitResult) && (extractT(hitResult) < tz_next)) {
                    return hitResult
                }

                tz_next += dtz
                iz += iz_step

                if (iz == iz_stop)
                    return missResult
            }
        }
    }

    override fun hit(ray: Ray): HitResult {
        return hitAlgorithmTemplate<HitResult>(
                { obj, r -> obj?.hit(r) ?: Miss.instance },
                { hitResult -> (hitResult is Hit) },
                { hitResult -> (hitResult as Hit).tmin },
                Miss.instance,
                ray)
    }

    override fun shadowHit(shadowRay: Ray): Double? {
        return hitAlgorithmTemplate<Double?>(
                { obj, r -> obj?.shadowHit(r) },
                { hitResult -> (hitResult !== null) },
                { hitResult -> hitResult!! },
                null,
                shadowRay)
    }

    private fun assertInitialized() {
        if (!initialized)
            throw IllegalStateException("Initialize grid before using it.")
    }
}