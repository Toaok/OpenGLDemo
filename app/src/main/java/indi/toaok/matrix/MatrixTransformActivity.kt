package indi.toaok.matrix

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import indi.toaok.matrix.weight.CurveIntersectionView


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

        val bezierCurveView = CurveIntersectionView(this)
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

}