package indi.toaok.matrix.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import indi.toaok.matrix.mode.Point

/**
 * 一个直线类，主要根据两个点计算直线一般公式的系数
 * (y2-y1)*x+(x1-x2)*y+(y1*x2-x1*y2)=0
 */
class Line(val p0: Point, val p1: Point) {
    private val TAG = "line"
    var A:Float
    var B:Float
    var C:Float


    //单位大小
    var unitSize= 40f

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

    fun draw(canvas: Canvas, paint: Paint){
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
     * 将坐标转换到屏幕上
     * @param transformPoint 要转换的点
     * @param unitSize 转换单位
     * @param origin 转换坐标系原点
     */
    private fun toScreen(
        transformPoint: Point,
        unitSize: Float=this.unitSize,
        origin: Point = this.origin
    ): Point {
        return Point(
            origin.x + transformPoint.x * unitSize,
            origin.y - transformPoint.y * unitSize
        )
    }
}