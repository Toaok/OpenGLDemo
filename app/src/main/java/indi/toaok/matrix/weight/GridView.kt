package indi.toaok.matrix.weight

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import indi.toaok.matrix.mode.Matrix
import indi.toaok.matrix.mode.Point
import indi.toaok.matrix.mode.Vector2D
import indi.toaok.matrix.units.dp2px
import indi.toaok.matrix.units.min
import kotlin.math.abs
import kotlin.math.floor

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