package indi.toaok.matrix.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import indi.toaok.matrix.mode.Point
import indi.toaok.matrix.mode.Vector2D

/**
 * 一个二阶贝塞尔曲线类，主计算曲线的基向量和变换矩阵
 */
class BezierCurve2D(val p0: Point, val p1: Point, val p2: Point) {
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
    var unitSize= 40f

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


    fun draw(canvas: Canvas,paint: Paint){
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