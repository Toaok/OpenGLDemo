package indi.toaok.matrix

/**
 *
 * @author user
 * @version 1.0  2020/8/25.
 */
/**
 * 求最小值
 * @param valus 传入的参数，多个
 * @return
 */
fun min(vararg values: Float): Float {
    if(values.size<=2)return kotlin.math.min(values[0], values[1])
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
    if(values.size<=2)return kotlin.math.max(values[0], values[1])
    var max: Float = Float.MIN_VALUE
    for (d in values) {
        if (d > max) max = d
    }
    return max
}