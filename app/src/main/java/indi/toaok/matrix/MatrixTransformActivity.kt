package indi.toaok.matrix

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt


/**
 *
 * @author user
 * @version 1.0  2020/8/19.
 */
class MatrixTransformActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var testView: TextView

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
        gridView = GridView(this)
//        testView= TextView(this)
//        testView.text = "Hello World!"
        setContentView(
            gridView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }


    private fun initData() {

    }

    private fun initEvent() {

    }


    class GridView : View {


        private lateinit var linePaint: Paint
        private lateinit var fillPaint: Paint
        private lateinit var textPaint: Paint

        private lateinit var matrix: Matrix
        private lateinit var matrixInvert: Matrix

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

        constructor(context: Context?) : this(context, null)

        constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

        constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attributeSet,
            defStyleAttr
        ) {
//            init()
        }

        private fun init() {
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
            textPaint.strokeWidth = dp2px(strokeWidth)
            textPaint.color = textStyle.toInt()

            matrix = Matrix()
            matrix.scale(sqrt(2f) / 2, sqrt(2f) / 2)
            matrix.rotate((PI / 4).toFloat())
            matrix.scale(2f, 1f)
            matrix.translate(unitSize * (gridNumX + 1), 0f)

            matrixInvert = Matrix()
            matrixInvert.translate(-unitSize * (gridNumX + 1), 0f)
            matrixInvert.scale(0.5f, 1f)
            matrixInvert.rotate((-PI / 4).toFloat())
            matrixInvert.scale(sqrt(2f), sqrt(2f))

        }

        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            super.onLayout(changed, left, top, right, bottom)
            unitSize = min(width, height) / 2f / (min(gridNumX,gridNumY)+1)
            init()
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            onDrawTile(canvas)
        }

        private fun onDrawTile(canvas: Canvas?) {
            for (i in 0..gridNumX) {
                for (y in 0..gridNumY) {
                    val px = i * unitSize
                    val py = y * unitSize
                    val leftTop = Point(px, py)
                    val rightTop = Point(px + unitSize, py)
                    val rightBottom = Point(px + unitSize, py + unitSize)
                    val leftBottom = Point(px, py + unitSize)
                    val center = Point(px + unitSize * 0.5f, py + unitSize * 0.5f)

                    val tLeftTop = matrix.transfromPoint(leftTop)
                    val tRightTop = matrix.transfromPoint(rightTop)
                    val tRightBottom = matrix.transfromPoint(rightBottom)
                    val tLeftBottom = matrix.transfromPoint(leftBottom)
                    val tCenter = matrix.transfromPoint(center)
                    val path = Path()
                    path.moveTo(tLeftTop.x, tLeftTop.y)
                    path.lineTo(tRightTop.x, tRightTop.y)
                    path.lineTo(tRightBottom.x, tRightBottom.y)
                    path.lineTo(tLeftBottom.x, tLeftBottom.y)
                    path.close()

                    canvas?.drawPath(path, linePaint)
                    fillPaint.color = if (i == xIndex && y == yIndex) {
                        fillSelectStyle.toInt()
                    } else {
                        fillStyle.toInt()
                    }
                    canvas?.drawPath(path, fillPaint)


                    val text = "$i,$y"
                    val textRect = Rect()
                    textPaint.getTextBounds(text, 0, text.length, textRect)
                    canvas?.drawText(
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

        private fun dp2px(dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            val x = event?.x ?: -1f
            val y = event?.y ?: -1f
            Log.i("Event Point", "$x,$y")
            val pointInvert = matrixInvert.transfromPoint(Point(x, y))
            xIndex = floor(pointInvert.x / unitSize).toInt()
            yIndex = floor(pointInvert.y / unitSize).toInt()
            Log.i("Invert Point", "$xIndex,$yIndex")
            if (xIndex in 0..gridNumX && yIndex in 0..gridNumY) {

                invalidate()
            }
            return super.onTouchEvent(event)
        }
    }

}