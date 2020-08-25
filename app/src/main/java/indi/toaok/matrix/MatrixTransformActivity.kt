package indi.toaok.matrix

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.*


/**
 *
 * @author user
 * @version 1.0  2020/8/19.
 */
class MatrixTransformActivity : AppCompatActivity() {

    private val TAG = "MatrixTransform"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun initView() {
        initWidget()
    }

    private fun initWidget() {
//        val gridView = GridView(this)
//        setContentView(
//            gridView,
//            ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//        )

        val bezierCurveView = BezierCurveView(this)
        setContentView(
            bezierCurveView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        window.decorView.post {
            Log.i(TAG, "WidthxHeight:${window.decorView.width}x${window.decorView.height}")
        }

    }


    private fun initData() {

    }

    private fun initEvent() {

    }


    class GridView : View {

        private val TAG = "GridView"

        private lateinit var linePaint: Paint
        private lateinit var fillPaint: Paint
        private lateinit var textPaint: Paint
        private lateinit var rectPaint: Paint

        private lateinit var matrix: Matrix

        private val strokeStyle = 0xff0000cc
        private val fillStyle = 0xffccccff
        private val fillSelectStyle = 0xffffeecc
        private val textStyle = 0xff000000

        private var strokeWidth = 2f
        private val gridNumX = 10
        private val gridNumY = 10

        private var unitSize = dp2px(40f)

        //选中的矩形
        private var xIndex = -1
        private var yIndex = -1

        //变换矩阵
        private val baseX = Vector2D(0.87f, 0.5f)//ex基向量
        private val baseY = Vector2D(-0.32f, 0.94f)//ey基向量

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
            initPaint()
        }

        private fun initPaint() {
            linePaint = Paint()
            linePaint.isAntiAlias = true
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = dp2px(strokeWidth)
            linePaint.color = strokeStyle.toInt()

            fillPaint = Paint()
            fillPaint.isAntiAlias = true
            fillPaint.color = fillStyle.toInt()

            textPaint = TextPaint()
            textPaint.isAntiAlias = true
            //textPaint.strokeWidth = dp2px(strokeWidth)
            textPaint.color = textStyle.toInt()

            rectPaint = Paint()
            rectPaint.isAntiAlias = true
            rectPaint.style = Paint.Style.STROKE
            rectPaint.strokeWidth = dp2px(strokeWidth / 2)
            rectPaint.color = Color.RED
        }


        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            super.onLayout(changed, left, top, right, bottom)
            val unitWidth = abs(baseX.x) + abs(baseY.x)
            val unitHeight = abs(baseX.y) + abs(baseY.y)
            unitSize =
                min(width / (unitWidth * (gridNumX + 1)), height / (unitHeight * (gridNumX + 1)))
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            onDrawTile(canvas)
        }

        private fun onDrawTile(canvas: Canvas) {
            //绘制区域的宽高(外切矩形)
            val pathWidth =
                abs(baseX.x) * (gridNumX + 1) * unitSize + abs(baseY.x) * (gridNumY + 1) * unitSize
            val pathHeight =
                abs(baseX.y) * (gridNumX + 1) * unitSize + abs(baseY.y) * (gridNumY + 1) * unitSize
            //计算移动到屏幕中央需要的距离
            var transX = width / 2 - pathWidth / 2
            if (baseX.x < 0) {
                transX += abs(baseX.x * unitSize * (gridNumX + 1))
            }
            if (baseY.x < 0) {
                transX += abs(baseY.x * unitSize * (gridNumY + 1))
            }
            //矩阵变换
            matrix = Matrix(
                a = baseX.x, b = baseX.y, tx = transX,
                c = baseY.x, d = baseY.y, ty = 0f
            )
            for (i in 0..gridNumX) {
                for (y in 0..gridNumY) {
                    val px = i * unitSize
                    val py = y * unitSize
                    val leftTop = Point(px, py)
                    val rightTop = Point(px + unitSize, py)
                    val rightBottom = Point(px + unitSize, py + unitSize)
                    val leftBottom = Point(px, py + unitSize)
                    val center = Point(px + unitSize * 0.5f, py + unitSize * 0.5f)

                    var tLeftTop = matrix.transfromPoint(leftTop)
                    var tRightTop = matrix.transfromPoint(rightTop)
                    var tRightBottom = matrix.transfromPoint(rightBottom)
                    var tLeftBottom = matrix.transfromPoint(leftBottom)
                    var tCenter = matrix.transfromPoint(center)

                    val path = Path()
                    path.moveTo(tLeftTop.x, tLeftTop.y)
                    path.lineTo(tRightTop.x, tRightTop.y)
                    path.lineTo(tRightBottom.x, tRightBottom.y)
                    path.lineTo(tLeftBottom.x, tLeftBottom.y)
                    path.close()


                    canvas.drawPath(path, linePaint)
                    fillPaint.color = if (i == xIndex && y == yIndex) {
                        fillSelectStyle.toInt()
                    } else {
                        fillStyle.toInt()
                    }
                    canvas.drawPath(path, fillPaint)

                    val text = "$i,$y"
                    val textRect = Rect()
                    textPaint.getTextBounds(text, 0, text.length, textRect)
                    canvas.drawText(
                        text,
                        0,
                        text.length,
                        tCenter.x - textRect.width() / 2,
                        tCenter.y + textRect.height() / 2,
                        textPaint
                    )
                }
            }
        }


        override fun onTouchEvent(event: MotionEvent?): Boolean {
            val x = event?.x ?: -1f
            val y = event?.y ?: -1f
            Log.i("Event Point", "$x,$y")

            val pointInvert = matrix.invert().transfromPoint(Point(x, y))
            xIndex = floor(pointInvert.x / unitSize).toInt()
            yIndex = floor(pointInvert.y / unitSize).toInt()
            Log.i("Invert Point", "$xIndex,$yIndex")
            if (xIndex in 0..gridNumX && yIndex in 0..gridNumY) {
                invalidate()
            }
            return super.onTouchEvent(event)
        }


    }


    class BezierCurveView : View {

        private val TAG = "BezierCurveView"


        private lateinit var bezierCurvePaint: Paint
        private lateinit var pointPaint: Paint
        private lateinit var textPaint: TextPaint

        private val bezierCurveStyle = 0xff0000cc
        private val pointStyle = 0xffcc0000
        private val textStyle = 0xff00cc00

        private var strokeWidth = 2f

        private var pointNum = 10

        private var unitSize = dp2px(40f)

        //起点
        private val p0 = Point(-1, 0)
        //控制点
        private val p1 = Point(1, -1)
        //终点
        private val p2 = Point(2, 2)
        //端点连线的中点
        private val anchorCenter: Point
        //抛物线的顶点
        private val anchor: Point

        //x方向的基向量
        private val baseX: Point
        //y方向的基向量
        private val baseY: Point

        //变换矩阵
        private val matrix: Matrix

        init {
            //端点连线的中点
            anchorCenter = Point((p0.x + p2.x) * 0.5, (p0.y + p2.y) * 0.5)//(0,1)
            //抛物线的顶点
            anchor = Point((anchorCenter.x + p1.x) * 0.5, (anchorCenter.y + p1.y) * 0.5)//(0,0)

            //x方向的基向量
            baseX = Point(p2.x - anchorCenter.x, p2.y - anchorCenter.y)//(1,0)
            //y方向的基向量
            baseY = Point(anchorCenter.x - anchor.x, anchorCenter.y - anchor.y)//(1,1)
//            baseY=Point(0,1)
            //变换矩阵
//            matrix = Matrix(
//                baseX.x, baseX.y, anchor.x,
//                baseY.x, baseY.y, anchor.y
//            )
            matrix = Matrix(
                1.5f, 1f, 0.75f,
                -0.25f, 1f, 0f
            )
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
            bezierCurvePaint = Paint()
            bezierCurvePaint.isAntiAlias = true
            bezierCurvePaint.style = Paint.Style.STROKE
            bezierCurvePaint.strokeWidth = dp2px(strokeWidth)
            bezierCurvePaint.color = bezierCurveStyle.toInt()

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
                    width / max(abs(p0.x - p2.x), abs(p0.x - p1.x), abs(p1.x - p2.x)),
                    height / max(abs(p0.y - p2.y), abs(p0.y - p1.y), abs(p1.y - p2.y))
                )
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            onDrawBezierCurve(canvas)
        }

        private fun onDrawBezierCurve(canvas: Canvas) {
            //把点转换到屏幕上
//            val screenCenter = Point(width * 0.5, height * 0.5)
            val screenCenter = Point(width * 0.5, height * 0.5)
            val screenP0 = toScreen(p0, unitSize, screenCenter)
            val screenP1 = toScreen(p1, unitSize, screenCenter)
            val screenP2 = toScreen(p2, unitSize, screenCenter)
            //绘制曲线
            val bezierPath = Path()
            bezierPath.moveTo(screenP0.x, screenP0.y)
            bezierPath.quadTo(screenP1.x, screenP1.y, screenP2.x, screenP2.y)
            canvas.drawPath(bezierPath, bezierCurvePaint)
            //绘制点
            val radius = dp2px(2f)
            for (i in -pointNum ..pointNum ) {
                val xValue = i / 10f
                val yValue = xValue * xValue//y=x^2
                Log.i(TAG,"(x,y):($xValue,$yValue)")
                val screenPoint = toScreen(matrix.transfromPoint(Point(xValue, yValue)), unitSize, screenCenter)
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
            unitSize: Float,
            origin: Point = Point()
        ): Point {
            return Point(
                origin.x + transformPoint.x * unitSize,
                origin.y + transformPoint.y * unitSize
            )
        }
    }
}