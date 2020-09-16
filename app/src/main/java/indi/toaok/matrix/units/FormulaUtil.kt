package indi.toaok.matrix.units

import android.util.Log
import indi.toaok.matrix.mode.ComplexNum
import indi.toaok.matrix.mode.ComplexNum.Companion.OMEGA
import indi.toaok.matrix.mode.ComplexNum.Companion.OMEGA_CUBIC
import indi.toaok.matrix.mode.ComplexNum.Companion.OMEGA_SQUARE
import indi.toaok.matrix.mode.ComplexNum.Companion.OMEGA_ZERO
import kotlin.math.*

/**
 * 方程求解工具
 */

/**
 * 计算方程ax+b=0的解
 * @param a
 * @param b
 */
fun calcLineFormulaZero(a: Double, b: Double): ArrayList<ComplexNum> {
    val results = ArrayList<ComplexNum>()
    //最高次项系数为0，方程无解或有无穷解，此处忽略
    if (a != 0.0) {
        results.add(ComplexNum(-b / a))
    }
    return results
}

fun calcLineFormulaZero(
    a: Float,
    b: Float
): ArrayList<ComplexNum> {
    return calcLineFormulaZero(a.toDouble(), b.toDouble())
}

/**
 * 计算方程ax^2+bx+c=0
 * @param a
 * @param b
 * @param c
 */
fun calcSquareFormulaZero(a: Double, b: Double, c: Double): ArrayList<ComplexNum> {
    //最高次项系数为0，用低一次的方程求解
    if (near0(a)) {
        return calcLineFormulaZero(b, c)
    }
    val results = ArrayList<ComplexNum>()
    //二次判别式部分
    val deltaRoot = ComplexNum(b * b - 4 * a * c).squareRoot()
    for (i in 0 until 2) {
        results.add(ComplexNum(-b).add(deltaRoot[i]).divideNumber(2 * a))
    }
    return results
}

fun calcSquareFormulaZero(
    a: Float,
    b: Float,
    c: Float
): ArrayList<ComplexNum> {
    return calcSquareFormulaZero(a.toDouble(), b.toDouble(), c.toDouble())
}

fun calcCubicFormulaZero(a: Double, b: Double, c: Double, d: Double): ArrayList<ComplexNum> {

    //最高次项系数为0，用低一次的方程求解
    if (near0(a)) {
        return calcSquareFormulaZero(b, c, d)
    }
    //计入特殊情况处理，目的在于减少不必要的精度损失
    //形如a^3+d=0的情况，直接开方即可
    if (near0(b) && near0(c)) {
        return ComplexNum(-d / a).cubicRoot()
    }
    //ax^3+bx^2=0,常数项和一次项都为0，两个跟为0，然后降为一次即可
    if (near0(d) && near0(c)) {
        val linrRoots = calcLineFormulaZero(a, b)
        linrRoots.add(ComplexNum())
        linrRoots.add(ComplexNum())
        return linrRoots
    }
    //ax^3+bx^2+cx=0,常数项为0，一个跟为0，然后降为二次即可
    if (near0(d)) {
        val squareRoots = calcSquareFormulaZero(a, b, c)
        squareRoots.add(ComplexNum())
        return squareRoots
    }
    //
    val A = b * b - 3 * a * c
    val B = b * c - 9 * a * d
    val C = c * c - 3 * b * d

    val delta = B * B - 4 * A * C

    val results = ArrayList<ComplexNum>()

    if (A == 0.0 && B == 0.0) {
        for (i in 0 until 3) {
            results.add(ComplexNum(-b / 3 / a))
        }
    } else if (delta > 0) {
        val roots = ArrayList<ComplexNum>()
        //二次判别式
        val deltaRoot = ComplexNum(delta).squareRoot()
        for (i in 0 until 2) {
            roots.add(ComplexNum(-B).add(deltaRoot[i]).divideNumber(2.0))
        }
        val ys = ArrayList<ComplexNum>()
        //y的立方根
        val ycrs = ArrayList<ComplexNum>()
        for (root in roots) {
            val y = root.multiplyNumber(a * 3).addNumber(A * b)
            ys.add(y)
            ycrs.add(ComplexNum((if (y.real > 0) 1 else -1) * abs(y.real).pow(1.0 / 3.0)))
        }
        val x1 = ComplexNum(-b).subtract(ycrs[0]).subtract(ycrs[1]).divideNumber(3 * a)
        results.add(x1)
        for (i in 0 until 2) {
            val x = ComplexNum(-2 * b).add(ycrs[0]).add(ycrs[1])
                .add(
                    ycrs[0].subtract(ycrs[1]).multiply(ComplexNum.I).multiplyNumber(
                        if (i == 0) sqrt(
                            3.0
                        ) else -sqrt(3.0)
                    )
                ).divideNumber(6 * a)
            results.add(x)
        }
    } else if (delta == 0.0) {
        val K = B / A
        results.add(ComplexNum(-b / a + K, 0.5 * K))
    } else if (delta < 0) {
        //盛金公式上说这种情况下A必须大于0，所以就没用复数来搞了
        val T = (2 * A * b - 3 * a * B) / 2 * sqrt(A * A * A)
        val theta = acos(T)
        val x1 = ComplexNum((-b - 2 * sqrt(A) * cos(theta / 3)) / 3 / a)
        results.add(x1)
        for (i in 0 until 2) {
            val x = ComplexNum(
                -b + sqrt(A) * (cos(theta / 3) + (if (i == 0) 1 else -1) * sin(theta / 3) * sqrt(
                    3.0
                )) / 3 / a
            )
            results.add(x)
        }

    }
    return results
}

fun calcCubicFormulaZero(
    a: Float,
    b: Float,
    c: Float,
    d: Float
): ArrayList<ComplexNum> {
    return calcCubicFormulaZero(a.toDouble(), b.toDouble(), c.toDouble(), d.toDouble())
}

/**
 * 计算方程ax^4+bx^3+cx^2+dx+e=0的四个解
 * 以下代码按照费拉里公式进行实现
 * @param a
 * @param b
 * @param c
 * @param d
 * @param e
 */
fun calcFourFormulaZero(
    a: Double,
    b: Double,
    c: Double,
    d: Double,
    e: Double
): ArrayList<ComplexNum> {

    //最高次项系数为0,用低一次的方程求解
    if (near0(a)) {
        return calcCubicFormulaZero(b, c, d, e)
    }
    //测试发现费拉里法无法处理一些特殊情况，m=0时出错率较高，但未能找出必须规律，为此，对于一些能用简单方法直接将次的情况，此处加入特殊处理
    //此外，特殊情况处理的好处是运算量小，可以减少中途计算中的精度损失

    //形如ax^4+e=0的情况，直接开方即可
    if (near0(b) && near0(c) && near0(d)) {
        return ComplexNum(-e / a).getRoot(4)
    }
    //形如ax^4+bx^3=0，常数项、一次项、二次项均为0，则有三个根为0，然后直接降到1次即可
    if (near0(c) && near0(d) && near0(e)) {
        var lineRoots = calcLineFormulaZero(a, b)
        lineRoots.add(ComplexNum())
        lineRoots.add(ComplexNum())
        lineRoots.add(ComplexNum())
        return lineRoots
    }
    //形如ax^4+bx^3+cx^2=0，常数项和一次项为0，则有两个根为0，然后直接降到2次即可
    if (near0(d) && near0(e)) {
        val squareRoots = calcSquareFormulaZero(a, b, c)
        squareRoots.add(ComplexNum())
        squareRoots.add(ComplexNum())
        return squareRoots
    }
    //形如ax^4+bx^3+cx^2+dx=0，常数项为0，则有一个根为0，然后直接降到3次即可
    if (near0(e)) {
        val cubicRoots = calcCubicFormulaZero(a, b, c, d)
        cubicRoots.add(ComplexNum())
        return cubicRoots
    }
    //形如ax^4+cx^2+e=0，一次项和三次项为0，为双二次方程，用换元法让y=x^2，即可降为二次
    if (near0(b) && near0(d)) {
        val twoSquareRoots = calcSquareFormulaZero(a, c, e)
        val fourRoots = twoSquareRoots[0].squareRoot()
        fourRoots.addAll(twoSquareRoots[1].squareRoot())
        return fourRoots
    }

    //弗拉里法求四次方程解
    val P = (c * c + 12 * a * e - 3 * b * d) / 9
    val Q = (27 * a * d * d + 2 * c * c * c + 27 * b * b * e - 72 * a * c * e - 9 * b * c * d) / 54
    val D = ComplexNum(Q * Q - P * P * P).squareRoot()[0]

    val u1 = ComplexNum(Q).add(D).cubicRoot()[0]
    val u2 = ComplexNum(Q).subtract(D).cubicRoot()[0]

    val u = if (u1.length > u2.length) u1 else u2

    val v = if (u.real == 0.0 && u.image == 0.0) ComplexNum() else ComplexNum(P).divide(u)

    var maxM = ComplexNum()
    var currentOmegaObj = ComplexNum()
    for (k in 1..3) {
        var m = ComplexNum(b * b - 8.0/ 3.0 * a * c)
        var omegaObj = u.colne().multiply(OMEGA_SQUARE.pow(k - 1))
        omegaObj = omegaObj.add(v.multiply(OMEGA_SQUARE.pow(4 - k)))
        omegaObj = omegaObj.multiplyNumber(4 * a)
        m = m.add(omegaObj)
        m = m.squareRoot()[0]
        if (m.length >= maxM.length) {
            currentOmegaObj = omegaObj
            maxM = m
        }
    }
    var S: ComplexNum
    var T = ComplexNum()
    if (!near0(maxM.length)) {
        S = ComplexNum(2 * b * b - 16.0 / 3.0 * a * c)
        S = S.subtract(currentOmegaObj)
        T = ComplexNum(8 * a * b * c - 16 * a * a * d - 2 * b * b * b)
        T = T.divide(maxM)
    } else {
        S = ComplexNum(b * b - 8.0 / 3.0 * a * c)
    }

    val results = ArrayList<ComplexNum>()
    for (n in 1..4) {
        var value = ComplexNum()
        //-1的[n/2]次方表示小于n/2的最大整数
        var minus1PowNdivide2 = (-1.0).pow(n / 2)
        var minus1PowNadd1 = (-1.0).pow(n + 1)
        value = value.subtractNumber(b)
        value = value.add(ComplexNum(minus1PowNdivide2).multiply(maxM))
        value = value.add(
            S.add(ComplexNum(minus1PowNdivide2).multiply(T)).squareRoot()[0].multiplyNumber(minus1PowNadd1)
        )
        value = value.divideNumber(4 * a)
//        Log.i("BezierCurve2D",value.length.toString())
        results.add(value)
    }
    return results
}

fun calcFourFormulaZero(
    a: Float,
    b: Float,
    c: Float,
    d: Float,
    e: Float
): ArrayList<ComplexNum> {
    return calcFourFormulaZero(a.toDouble(), b.toDouble(), c.toDouble(), d.toDouble(), e.toDouble())
}

/**
 * 计算多项式方程的解
 */
fun calcPolyFormulaZero() {
    val currentArgs = arrayOf<Int>()
    while (currentArgs.isNotEmpty() && currentArgs[0] == 0) {
        // currentArgs.
    }
}


/**
 * 为了使用上的方便，这里提供一个通过复数解得到实数的方法，可能有相等的实根
 */

fun getRealSolutions(
    complexNums: ArrayList<ComplexNum>,
    tolerance: Double = 0.00001
): ArrayList<Double> {
    val number = ArrayList<Double>()
    for (item in complexNums) {
        if (near0(item.image, tolerance)) {
            number.add(item.real)
        }
    }
    return number
}

fun near0(num: Double, tolerance: Double = 0.0000001): Boolean {
    return abs(num) <= tolerance
}


