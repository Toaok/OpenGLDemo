package indi.toaok.matrix.units

import indi.toaok.matrix.mode.ComplexNum
import indi.toaok.matrix.mode.I
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

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

/**
 * 计算方程ax^2+bx+c=0
 * @param a
 * @param b
 * @param c
 */
fun calcSquareFormulaZero(a: Double, b: Double, c: Double): ArrayList<ComplexNum> {
    //最高次项系数为0，用低一次的方程求解
    if (near0(a)) {
        return calcSquareFormulaZero(b, c)
    }
    val results = ArrayList<ComplexNum>()
    //二次判别式部分
    val deltaRoot = ComplexNum(b * b - 4 * a * c).squareRoot()
    for (i in 0..2) {
        results.add(ComplexNum(-b).add(deltaRoot[i]).divideNumber(2 * a))
    }
    return results
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

    val i = 0
    val results = ArrayList<ComplexNum>()

    if (A == 0.0 && B == 0.0) {
        for (i in 0..3) {
            results.add(ComplexNum(-b / 3 / a))
        }
    } else if (delta > 0) {
        val roots = ArrayList<ComplexNum>()
        val deltaRoot = ComplexNum(delta).squareRoot()
        for (i in 0..2) {
            roots.add(ComplexNum(-B).add(deltaRoot[i].divideNumber(2.0)))
        }
        val ys = ArrayList<ComplexNum>()
        val ycrs = ArrayList<ComplexNum>()
        for (i in 0..roots.size) {
            val root = roots[i]
            val y = root.multiplyNumber(a * 3).addNumber(A * b)
            ys.add(y)
            ycrs.add(ComplexNum((if (y.real > 0) 1 else -1) * abs(y.real).pow(1.0 / 3.0)))
        }
        val x1 = ComplexNum(-b).subtract(ycrs[0]).subtract(ycrs[1]).divideNumber(3 * a)
        results.add(x1)
        for (i in 0..2) {
            val x = ComplexNum(-2 * b).add(ycrs[0]).add(ycrs[1])
                .add(
                    ycrs[0].subtract(ycrs[1]).mutiply(ComplexNum.I).multiplyNumber(
                        if (i == 0) sqrt(
                            3.0
                        ) else -sqrt(3.0)
                    )
                ).divideNumber(6 * a)
            results.add(x)
        }
    } else if (delta == 0.0) {
        val K=B/A
        results.add(ComplexNum(-b/a+K,-1.0/2.0*K))
    }
}

fun near0(num: Double, tolerance: Double = 0.0000001): Boolean {
    return abs(num) <= tolerance
}
