package indi.toaok.matrix.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import indi.toaok.matrix.mode.BezierCurve2D
import indi.toaok.matrix.mode.Line
import indi.toaok.matrix.mode.Point
import indi.toaok.matrix.units.dp2px
import indi.toaok.matrix.units.max
import indi.toaok.matrix.units.min
import kotlin.math.abs
import kotlin.math.sqrt

class CurveIntersectionView : View {

    private val TAG = "CurveIntersectionView"

    private lateinit var linePaint: Paint
    private lateinit var pointPaint: Paint
    private lateinit var textPaint: TextPaint

    private val bezierCurveStyle = 0xff0000cc
    private val pointStyle = 0xffcc0000
    private val textStyle = 0xff00cc00

    private var strokeWidth = 2f

    private var unitSize = dp2px(40f)

    //直线上的两点
    private val lineP0 = Point(-2, -2)
    private val lineP1 = Point(4.0, 14 / 5.0)
    private val line = Line(lineP0, lineP1)

    //起点
    private val p0 = Point(0, -1)
    //控制点
    private val p1 = Point(1, 2)
    //终点
    private val p2 = Point(2, 5)
    //二阶贝塞尔曲线
    private val bezierCurve = BezierCurve2D(p0, p1, p2)

    //交点
    private val intersections = ArrayList<Point>()

    init {
        lineWhitBezierIntersection()
    }

    private fun lineWhitBezierIntersection() {
        //基向量矩阵变换后得到的直线ABC系数
        val convertedLineA = line.A * bezierCurve.matrix.a + line.B * bezierCurve.matrix.b
        val convertedLineB = line.A * bezierCurve.matrix.c + line.B * bezierCurve.matrix.d
        val convertedLineC =
            line.A * bezierCurve.matrix.tx + line.B * bezierCurve.matrix.ty + line.C

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
                intersections.add(bezierCurve.matrix.transfromPoint(intersection1))
            }
            if (isOnLine(intersection2)) {
                intersections.add(bezierCurve.matrix.transfromPoint(intersection2))
            }
        } else if (convertedLineA != 0f) {
            val x = -convertedLineC / convertedLineA
            val y = x * x
            intersections.add(bezierCurve.matrix.transfromPoint(Point(x, y)))
        }
    }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        linePaint = Paint()
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = dp2px(strokeWidth)
        linePaint.color = bezierCurveStyle.toInt()

        pointPaint = Paint()
        pointPaint.isAntiAlias = true
        pointPaint.color = pointStyle.toInt()

        textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.color = textStyle.toInt()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        unitSize =
            min(
                width / max(
                    abs(p0.x - p2.x),
                    abs(p0.x - p1.x),
                    abs(p1.x - p2.x)
                ),
                height / max(
                    abs(p0.y - p2.y),
                    abs(p0.y - p1.y),
                    abs(p1.y - p2.y)
                )
            )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        onDrawBezierCurve(canvas)
    }

    private fun onDrawBezierCurve(canvas: Canvas) {
        //把点转换到屏幕上
//            val screenCenter = Point(width * 0.5, height * 0.5)
        val screenCenter = Point(
            width * 0.5,
            (height /*+ max(
                abs(p0.y - p1.y),
                abs(p0.y - p2.y),
                abs(p1.y - p2.y)
            ) * unitSize*/) * 0.5
        )

        //绘制曲线
        bezierCurve.unitSize = this.unitSize
        bezierCurve.origin = screenCenter
        bezierCurve.draw(canvas, linePaint)

        //绘制直线
        line.unitSize = this.unitSize
        line.origin = screenCenter
        line.draw(canvas, linePaint)
        //绘制交点
        val radius = dp2px(2f)
        for (i in 0..intersections.size - 1) {
            Log.i(TAG, "(x,y):(${intersections[i].x},${intersections[i].y})")
            val screenPoint =
                toScreen(intersections[i], unitSize, screenCenter)
            canvas.drawCircle(screenPoint.x, screenPoint.y, radius, pointPaint)
        }
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
        origin: Point = Point()
    ): Point {
        return Point(
            origin.x + transformPoint.x * unitSize,
            origin.y - transformPoint.y * unitSize
        )
    }

    //是否在曲线上
    private fun isOnLine(point: Point): Boolean {
        return point.x > -1 && point.x < 1 && point.y < 1
    }
}