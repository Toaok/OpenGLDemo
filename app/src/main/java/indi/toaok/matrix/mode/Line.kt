package indi.toaok.matrix.mode

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


}