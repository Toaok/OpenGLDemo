package indi.toaok.matrix.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import indi.toaok.matrix.units.calcFourFormulaZero
import indi.toaok.matrix.units.getRealSolutions
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * 二次贝塞尔曲线的方程
 * x(t)=(1-t)^2 * x0 + 2t(1-t) * x1 + t^2 * x2
 * y(t)=(1-t)^2 * y0 + 2t(1-t) * y1 + t^2 * y2
 * ==>> (P0-2P1+P2)*t^2 + (2*P1-2*P0)*t + P0
 * 一个二阶贝塞尔曲线类，主要计算曲线的基向量和变换矩阵
 * 以及和其他曲线的交点
 */
class BezierCurve2D(val p0: Point, val p1: Point, val p2: Point) {

    private val TAG = "BezierCurve2D"

    //端点连线的中点
    private val anchorCenter: Point
    //抛物线的顶点
    private val anchor: Point

    //x方向的基向量
    val baseX: Vector2D
    //y方向的基向量
    val baseY: Vector2D

    //变换矩阵
    val matrix: Matrix

    //单位大小
    var unitSize = 40f

    //原点
    var origin: Point = Point()

    init {
        //端点连线的中点
        anchorCenter = Point((p0.x + p2.x) * 0.5, (p0.y + p2.y) * 0.5)//(0,1)
        //抛物线的顶点
        anchor = Point(
            (anchorCenter.x + p1.x) * 0.5,
            (anchorCenter.y + p1.y) * 0.5
        )//(0,0)

        //x方向的基向量
        baseX = Vector2D(p2.x - anchorCenter.x, p2.y - anchorCenter.y)//(1,0)
        //y方向的基向量
        baseY =
            Vector2D(
                anchorCenter.x - anchor.x,
                anchorCenter.y - anchor.y
            )//(1,1)
        //变换矩阵
        matrix = Matrix(
            a = baseX.x, c = baseY.x, tx = anchor.x,
            b = baseX.y, d = baseY.y, ty = anchor.y
        )
    }


    fun draw(canvas: Canvas, paint: Paint) {
        val screenP0 = toScreen(p0)
        val screenP1 = toScreen(p1)
        val screenP2 = toScreen(p2)
        //绘制曲线
        val bezierPath = Path()
        bezierPath.moveTo(screenP0.x, screenP0.y)
        bezierPath.quadTo(screenP1.x, screenP1.y, screenP2.x, screenP2.y)
        canvas.drawPath(bezierPath, paint)
    }

    /**
     * 与直线的交点
     */
    fun withLineIntersection(line: Line): ArrayList<Point> {
        //交点
        val intersections = ArrayList<Point>()
        //基向量矩阵变换后得到的直线ABC系数
        val convertedLineA = line.A * matrix.a + line.B * matrix.b
        val convertedLineB = line.A * matrix.c + line.B * matrix.d
        val convertedLineC =
            line.A * matrix.tx + line.B * matrix.ty + line.C

        //接着求根公式
        val delta = convertedLineA * convertedLineA - 4 * convertedLineB * convertedLineC
        Log.i(TAG, "delta:$delta")
        if (convertedLineB != 0f && delta >= 0) {
            val x1 = (-convertedLineA + sqrt(delta)) / 2 / convertedLineB
            val y1 = x1 * x1
            val x2 = (-convertedLineA - sqrt(delta)) / 2 / convertedLineB
            val y2 = x2 * x2
            val intersection1 = Point(x1, y1)
            val intersection2 = Point(x2, y2)
            if (isOnLine(intersection1)) {
                intersections.add(matrix.transfromPoint(intersection1))
            }
            if (isOnLine(intersection2)) {
                intersections.add(matrix.transfromPoint(intersection2))
            }
        }/* else if (convertedLineA != 0f) {
            val x = -convertedLineC / convertedLineA
            val y = x * x
            intersections.add(matrix.transfromPoint(Point(x, y)))
        }*/

        return intersections
    }

    /**
     * 曲线和曲线的交点
     */
    fun withBezierCurveIntersection(bezierCurve: BezierCurve2D): ArrayList<Point> {
        //交点
        val intersections = ArrayList<Point>()
        //当前曲线矩阵的逆矩阵
        val matrixInvert = matrix.invert()
        //第一条(当前)贝塞尔曲线经过matrix基向量矩阵的逆矩阵变换后得到的一定是Y=X^2,
        //现在我们把两条曲线都进行matrix的你变换，则第一条（当前）将变成Y=X^2,
        //而另一条则可以通过变换控制点的坐标重新生成，无需带入方程中进行计算
        val transformedP0 = matrixInvert.transfromPoint(bezierCurve.p0)
        val transformedP1 = matrixInvert.transfromPoint(bezierCurve.p1)
        val transformedP2 = matrixInvert.transfromPoint(bezierCurve.p2)

        //贝塞尔曲线x分量的三个系数
        val ax = transformedP0.x - 2 * transformedP1.x + transformedP2.x
        val bx = 2 * transformedP1.x - 2 * transformedP0.x
        val cx = transformedP0.x
        //贝塞尔曲线y分量的三个系数
        val ay = transformedP0.y - 2 * transformedP1.y + transformedP2.y
        val by = 2 * transformedP1.y - 2 * transformedP0.y
        val cy = transformedP0.y

        //由Y=x^2和X=ax*t^2+bx*t+cx和Y=ay*t^2+by*t+cy,得到
        //ay*t^2+by*t+cy=(ax*t^2+bx*t+cx)^2
        //展开化简将得到一个四次方程，系数如下
        val fourFormulaA = ax * ax
        val fourFormulaB = 2 * ax * bx
        val fourFormulaC = bx * bx + 2 * ax * cx - ay
        val fourFormulaD = 2 * bx * cx - by
        val fourFormulaE = cx * cx - cy

        //用四次求根的类进行求解
        Log.i(TAG, "\n$fourFormulaA,$fourFormulaB,$fourFormulaC,$fourFormulaD,$fourFormulaE")
        val resolutions = calcFourFormulaZero(
            fourFormulaA,
            fourFormulaB,
            fourFormulaC,
            fourFormulaD,
            fourFormulaE
        )
        Log.i(TAG, resolutions.toString())
        val realResolutions = getRealSolutions(resolutions)

        for (realResolution in realResolutions) {
            if (realResolution in 0.0..1.0) {//是否在第一条曲线上
                //算出在基向量矩阵逆变换后的交点
                val p = Point()
                p.x = (ax * realResolution * realResolution + bx * realResolution + cx).toFloat()
                p.y = (ay * realResolution * realResolution + by * realResolution + cy).toFloat()
                //第二条曲线在基向量逆矩阵变换后为Y=X^2,X的取值范围为-1到1
                if (p.x in -1f..1f) {
                    //因为这个交点是方程在基向量逆变换后求得，所以要执行基向量矩阵的原始变换才能让结果恢复到变换前
                    intersections.add(matrix.transfromPoint(p))
                }
            }
        }
        return intersections
    }

    /**
     * 将坐标转换到屏幕上
     * @param transformPoint 要转换的点
     * @param unitSize 转换单位
     * @param origin 转换坐标系原点
     */
    private fun toScreen(
        transformPoint: Point,
        unitSize: Float = this.unitSize,
        origin: Point = this.origin
    ): Point {
        return Point(
            origin.x + transformPoint.x * unitSize,
            origin.y - transformPoint.y * unitSize
        )
    }


    //是否在曲线上
    fun isOnLine(point: Point): Boolean {
        return point.x > -1 && point.x < 1 && point.y < 1
    }
}