package indi.toaok.matrix.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import kotlin.math.sqrt

/**
 * 一个直线类，主要根据两个点计算直线一般公式的系数
 * (y2-y1)*x+(x1-x2)*y+(y1*x2-x1*y2)=0
 */
class Line(val p0: Point, val p1: Point) {
    private val TAG = "line"
    var A: Float
    var B: Float
    var C: Float


    //单位大小
    var unitSize = 40f

    //原点
    var origin: Point = Point()

    init {
        //根据两点式求出的直线一般式系数
        A = (p1.y - p0.y)
        B = (p0.x - p1.x)
        C = (p1.x * p0.y - p1.y * p0.x)
        var lineEquation = "$A*x"
        lineEquation += if (B > 0) {
            "+$B*y"
        } else if (B == 0f) {
            ""
        } else {
            "-$B*y"
        }
        lineEquation += if (C > 0) {
            "+$C"
        } else if (B == 0f) {
            ""
        } else {
            "-$C"
        }

        Log.i(TAG, "line:$lineEquation")
    }

    fun draw(canvas: Canvas, paint: Paint) {
        val screenP0 = toScreen(p0)
        val screenP1 = toScreen(p1)
        //绘制曲线
        //绘制直线
        val linePath = Path()
        linePath.moveTo(screenP0.x, screenP0.y)
        linePath.lineTo(screenP1.x, screenP1.y)
        canvas.drawPath(linePath, paint)
    }

    /**
     * 和曲线的交点
     */
    fun whitBezierIntersection(bezierCurve: BezierCurve2D): ArrayList<Point> {
        //交点
        val intersections = ArrayList<Point>()
        //基向量矩阵变换后得到的直线ABC系数
        val convertedLineA = A * bezierCurve.matrix.a + B * bezierCurve.matrix.b
        val convertedLineB = A * bezierCurve.matrix.c + B * bezierCurve.matrix.d
        val convertedLineC =
            A * bezierCurve.matrix.tx + B * bezierCurve.matrix.ty + C

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
            if (bezierCurve.isOnLine(intersection1)) {
                intersections.add(bezierCurve.matrix.transfromPoint(intersection1))
            }
            if (bezierCurve.isOnLine(intersection2)) {
                intersections.add(bezierCurve.matrix.transfromPoint(intersection2))
            }
        } else if (convertedLineA != 0f) {
            val x = -convertedLineC / convertedLineA
            val y = x * x
            intersections.add(bezierCurve.matrix.transfromPoint(Point(x, y)))
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
}