package indi.toaok.matrix.mode

import java.lang.Math.pow
import kotlin.math.*


/**
 * 复数类 a+bi
 */
class ComplexNum(
    val real: Double = 0.0,//复数的实部
    val image: Double = 0.0//复数的虚部
) {
    companion object {

        /**
         * OMEGA是根为1，辅角主值等于120度的复数，它是1开三次方的结果在解3次4次方程中非常常用
         * OMEGA就是希腊字母里最像w的那个
         */
        val OMEGA = ComplexNum(1.0 / 2.0, sqrt(3.0) / 2.0)

        /**
         * 理论上OMEGA的平方可以用OMEGA相乘得到，但由于容易产生浮点误差，加上数值固定，因此也直接做成常量
         */
        val OMEGA_SQUARE = ComplexNum(-1.0 / 2.0, sqrt(3.0) / 2.0)

        /**
         * OMEGA的三次方恰好等于实数1
         */
        val OMEGA_CUBIC = ComplexNum(-1.0, 0.0)
        /**
         * OMEGA的0次方
         */
        val OMEGA_ZERO = ComplexNum(1.0, 0.0)

        /**
         * 虚数单位i
         */
        val I = ComplexNum(0.0, 1.0)
    }

    /**
     * 复数的辅角主值（复数所在点与坐标连线跟水瓶线的夹角），以弧度为单位
     * 必要时可用degree属性可以更有效避免边缘为的浮点误差引发的错误
     */
    val angle: Double
        get() {
            return if (abs(this.image) < 0.0000001) {
                if (this.real > 0) 0.0 else PI
            } else if (abs(this.real) < 0.0000001) {
                if (this.image > 0) PI * 0.5 else if (this.image == 0.0) 0.0 else -PI
            } else {
                atan2(this.image, this.real)
            }
        }

    /**
     * 复数的辅角主值，以角度值表示（跟弧度相比，该方法能更有效地避免边缘位置的浮点误差引发的错误）
     */
    val degree: Double
        get() {
            return if (abs(this.image) < 0.0000001) {
                if (this.real > 0) 0.0 else 180.0
            } else if (abs(this.real) < 0.0000001) {
                if (this.image > 0) 90.0 else if (this.image == 0.0) 0.0 else 270.0
            } else {
                atan2(this.image, this.real) / PI * 180
            }
        }

    /**
     * 复数的模
     */
    val length: Double
        get() {
            return sqrt(this.real * this.real + this.image * this.image)
        }

    /**
     * 复数相加
     * @param complexNum 要与其相加的复数
     */
    fun add(complexNum: ComplexNum): ComplexNum {
        return ComplexNum(this.real + complexNum.real, this.image + complexNum.image)
    }

    /**
     * 与实数相加
     * @param num 要与其相加的数字
     */
    fun addNumber(num: Double): ComplexNum {
        return ComplexNum(this.real + num, this.image)
    }

    /**
     * 复数相减
     * @param complexNum 要与其相减的复数
     */
    fun subtract(complexNum: ComplexNum): ComplexNum {
        return ComplexNum(this.real - complexNum.real, this.image - complexNum.image)
    }

    /**
     * 与实数相减
     * @param num 要与其相加的数字
     */
    fun subtractNumber(num: Double): ComplexNum {
        return ComplexNum(this.real - num, image)
    }

    /**
     * 复数相乘(运算法则是利用多项式乘法进行展开得到)
     * @param complexNum 要与其相乘的复数
     */
    fun multiply(complexNum: ComplexNum): ComplexNum {
        return ComplexNum(
            this.real * complexNum.real - this.image * complexNum.image,
            this.image * complexNum.real + this.real * complexNum.image
        )
    }

    /**
     * 与实数相乘
     * @param num 要与其相乘的数字
     */
    fun multiplyNumber(num: Double): ComplexNum {
        return ComplexNum(this.real * num, this.image * num)
    }

    /**
     * 复数除法（运算法则是通过平方差公式，分子分母同时乘以分母的共轭复数以去除分母中的虚部，然后就利用乘法法则进行计算）
     * @param complexNum 要与其相除的复数
     *
     */

    fun divide(complexNum: ComplexNum): ComplexNum {
        //分母画为实数
        val denominator = complexNum.real * complexNum.real + complexNum.image * complexNum.image
        //分子也乘以同样的复数，并除以分母得到最终结果
        return ComplexNum(
            (this.real * complexNum.real + this.image * complexNum.image) / denominator,
            (this.image * complexNum.real - this.real * complexNum.image) / denominator
        )
    }

    /**
     * 与实数相除
     * @param num 要与其相除的数字
     */
    fun divideNumber(num: Double): ComplexNum {
        return ComplexNum(this.real / num, this.image / num)
    }

    /**
     * 开平方运算
     */
    fun squareRoot(): ArrayList<ComplexNum> {
        return getRoot(2)
    }

    /**
     * 开立方运算
     */
    fun cubicRoot(): ArrayList<ComplexNum> {
        return getRoot(3)
    }

    /**
     * 开任意整数次方的运算
     * @param times
     */
    fun getRoot(times: Int): ArrayList<ComplexNum> {
        val vec = ArrayList<ComplexNum>()
        //复数开发运算的原理是把辅角根据次数进行平分
        var degree = this.degree
        degree /= times
        //然后多个方根平分360度，所以需要算出每个方根之间的辅角间隔
        val degreeUnit = 360 / times
        //复数长度（模）直接开方即可
        val lengthRoot = length.pow(1.0 / times)
        for (i in 0 until times) {
            val currentDegree = degree + i * degreeUnit
            val currentAngle = currentDegree * PI / 180
            val cos = cos(currentAngle)
            val sin = sin(currentAngle)
            vec.add(ComplexNum(lengthRoot * cos, lengthRoot * sin))
        }
        return vec
    }

    /**
     * 返回当前复数的一个副本
     */
    fun colne(): ComplexNum {
        return ComplexNum(this.real, this.image)
    }

    override fun toString(): String {
        val realStr = if (this.real != 0.0 || this.image == 0.0) "$real" else ""
        val imageStr = when {
            this.image == 0.0 -> ""
            this.image < 0 -> "${this.image}"
            else -> if (realStr != "") {
                "+"
            } else {
                ""
            } + "${this.image}i"
        }
        return realStr + imageStr
    }

    /**
     * 任意整数次方的运算
     */
    fun pow(k: Int): ComplexNum {
        var c = colne()
        when {
            k > 0 -> {
                for (i in 1..k) {
                    c = c.multiply(this)
                }
            }
            k == 0 -> {
                c = ComplexNum(1.0)
            }
            k < 0 -> {
                for (i in 1..k) {
                    c = c.multiply(this)
                }
                c = ComplexNum(1.0).divide(c)
            }
        }
        return c
    }
}