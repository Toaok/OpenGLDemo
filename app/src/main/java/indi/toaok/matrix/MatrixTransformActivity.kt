package indi.toaok.matrix

import android.content.Context
import android.content.res.Configuration
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
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlin.math.*


/**
 *
 * @author user
 * @version 1.0  2020/8/19.
 */
class MatrixTransformActivity : AppCompatActivity() {

    private val TAG = "MatrixTransform"

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
            textPaint.strokeWidth = dp2px(strokeWidth)
            textPaint.color = textStyle.toInt()

            rectPaint = Paint()
            rectPaint.isAntiAlias = true
            rectPaint.style = Paint.Style.STROKE
            rectPaint.strokeWidth = dp2px(strokeWidth / 2)
            rectPaint.color = Color.RED
        }


        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            super.onLayout(changed, left, top, right, bottom)
            unitSize = if (width / 2f < height) {
                (width / 2f) / (min(
                    gridNumX,
                    gridNumY
                ) + 1)
            } else {
                height.toFloat() / (min(gridNumX, gridNumY) + 1)
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            onDrawTile(canvas)
        }

        private fun onDrawTile(canvas: Canvas) {
            //变换矩阵
            val baseX = Vector2D(0.87f, 0.5f)//ex基向量
            val baseY = Vector2D(-0.32f, 0.94f)//ey基向量
            //缩放，使其宽高小于可视区域的宽高
            val scale = min(1 / baseX.add(baseY).x, 1 / baseX.add(baseY).y)

            val cosθ = baseX.dot(baseY) / baseX.length * baseY.length
            val sinθ = sqrt(1 - cosθ * cosθ)
            val pathWidth = (baseX.length*cosθ+baseY.length*sinθ) * unitSize * (gridNumX + 1)
            val transX = (width-pathWidth)/2f+baseX.length*cosθ*unitSize* (gridNumX + 1)
            matrix = Matrix(
                a = baseX.x, b = baseX.y, tx = transX,
                c = baseY.x, d = baseY.y, ty = 0f
            )
            Log.i(TAG, "width:$width")
            Log.i(TAG, "pathWidth:$pathWidth")
            Log.i(TAG, "transX:$transX")
            if (width > height) {
                matrix.scale(scale, scale)
            }
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

        private fun dp2px(dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
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

        /**
         * 求最小值
         * @param valus 传入的参数，多个
         * @return
         */
        fun min(vararg values: Float): Float {
            var min: Float = Float.MAX_VALUE
            for (d in values) {
                if (d < min) min = d
            }
            return min
        }

        /**
         * 求最大值
         * @param valus 传入的参数，多个
         * @return
         */
        fun max(vararg values: Float): Float {
            var min: Float = Float.MIN_VALUE
            for (d in values) {
                if (d > min) min = d
            }
            return min
        }
    }


}