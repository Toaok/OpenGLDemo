package indi.toaok.matrix.units

import indi.toaok.matrix.mode.ComplexNum
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
        val T = (2 * A * b - 3 * a * B) / (2 * sqrt(A * A * A))
        val theta = acos(T)
        val x1 = ComplexNum((-b - 2 * sqrt(A) * cos(theta / 3)) / 3 / a)
        results.add(x1)
        for (i in 0 until 2) {
            val x = ComplexNum(
                (-b + sqrt(A) * (cos(theta / 3) + (-1.0).pow(i) * sin(theta / 3) * sqrt(3.0))) / 3 / a
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

    val results = ArrayList<ComplexNum>()
    //最高次项系数化为1
    val A = 1.0
    val B = b / a
    val C = c / a
    val D = d / a
    val E = e / a
    /**
     * 引入变量y,M=sqrt(8*y+B*B-4*C),N=B*y-D
     * 此方程是一下两个一元二次方程的解
     * 2X^2+(B+M)+2*(y+N/M)=0
     * 2X^2+(B-M)+2*(y-N/M)=0
     */
    //其中y是一个一元三次方程的任意实根
    val yA = 8.0
    val yB = -4 * C
    val yC = -(8 * E - 2 * B * D)
    val yD = -E * (B * B - 4 * C) - D * D
    //根据盛金公式求得y的根
    val yRoot = calcCubicFormulaZero(yA, yB, yC, yD)
    //获取y的实根
    val yRealRoot = getRealSolutions(yRoot)
    val y = if (yRealRoot.isNotEmpty()) yRealRoot[0] else return results//如果没有实根无解
    //求得M,N
    val M = sqrt(8 * y + B * B - 4 * C)
    val N = B * y - D
    //将M,N带入一元二次方程
    results.addAll(calcSquareFormulaZero(2.0, B + M, 2 * (y + N / M)))
    results.addAll(calcSquareFormulaZero(2.0, B - M, 2 * (y - N / M)))
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
 * TODO
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


