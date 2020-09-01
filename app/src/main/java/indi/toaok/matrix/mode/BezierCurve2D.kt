package indi.toaok.matrix.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import kotlin.math.sqrt

/**
 * 二次贝塞尔曲线的方程
 * x(t)=(1-t)^2 * x0 + 2t(1-t) * x1 + t^2 * x2
 * y(t)=(1-t)^2 * y0 + 2t(1-t) * y1 + t^2 * y2
 * ==>> (P0-2P1+P2)*t^2 + (2*P1-2*P0)*t + P0
 * 一个二阶贝塞尔曲线类，主计算曲线的基向量和变换矩阵
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
        } else if (convertedLineA != 0f) {
            val x = -convertedLineC / convertedLineA
            val y = x * x
            intersections.add(matrix.transfromPoint(Point(x, y)))
        }

        return intersections
    }

    /**
     * 曲线和曲线的交点
     */
    fun withBezierCurveIntersection(bezierCurve: BezierCurve2D): ArrayList<Point> {
        //交点
        val intersections = ArrayList<Point>()
        //贝塞尔曲线x分量的三个系数

        //贝塞尔曲线y分量的三个系数


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
        } else if (convertedLineA != 0f) {
            val x = -convertedLineC / convertedLineA
            val y = x * x
            intersections.add(matrix.transfromPoint(Point(x, y)))
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