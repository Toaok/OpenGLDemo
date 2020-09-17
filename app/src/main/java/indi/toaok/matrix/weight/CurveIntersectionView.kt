package indi.toaok.matrix.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
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

    //二阶贝塞尔曲线
    private val p00 = Point(-1, 1)//起点
    private val p01 = Point(2, 2)//控制点
    private val p02 = Point(1.0, -0.5) //终点
    private val bezierCurve0 = BezierCurve2D(p00, p01, p02)

    private val p10 = Point(3.0, -0.5)//起点
    private val p11 = Point(-3, 2)//控制点
    private val p12 = Point(2, -1) //终点
    private val bezierCurve1 = BezierCurve2D(p10, p11, p12)
    //交点
    private val intersections = ArrayList<Point>()

    init {
        intersections.addAll(line.whitBezierIntersection(bezierCurve0))
        intersections.addAll(line.whitBezierIntersection(bezierCurve1))
        intersections.addAll(bezierCurve0.withBezierCurveIntersection(bezierCurve1))
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
        unitSize = min(
            min(
                width / max(
                    abs(p00.x - p02.x),
                    abs(p00.x - p01.x),
                    abs(p01.x - p02.x)
                ),
                height / max(
                    abs(p00.y - p02.y),
                    abs(p00.y - p01.y),
                    abs(p01.y - p02.y)
                )
            ), min(
                width / max(
                    abs(p10.x - p12.x),
                    abs(p10.x - p11.x),
                    abs(p11.x - p12.x)
                ),
                height / max(
                    abs(p10.y - p12.y),
                    abs(p10.y - p11.y),
                    abs(p11.y - p12.y)
                )

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
                abs(p00.y - p01.y),
                abs(p00.y - p02.y),
                abs(p01.y - p02.y)
            ) * unitSize*/) * 0.5
        )

        //绘制曲线
        bezierCurve0.unitSize = this.unitSize
        bezierCurve0.origin = screenCenter
        bezierCurve0.draw(canvas, linePaint)

        bezierCurve1.unitSize = this.unitSize
        bezierCurve1.origin = screenCenter
        bezierCurve1.draw(canvas, linePaint)

        //绘制直线
        line.unitSize = this.unitSize
        line.origin = screenCenter
        line.draw(canvas, linePaint)


        //绘制交点
        val radius = dp2px(2f)
        for (intersection in intersections) {
            Log.i(TAG, "(x,y):(${intersection.x},${intersection.y})")
            val screenPoint =
                toScreen(intersection, unitSize, screenCenter)
            Log.i(TAG, "screen(x,y):(${screenPoint.x},${screenPoint.y})")
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
}