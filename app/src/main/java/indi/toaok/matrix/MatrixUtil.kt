package indi.toaok.matrix

import kotlin.math.cos
import kotlin.math.sin

/**
 * 获取一个平移矩阵t
 *  [1  0  tx]
 *  [0  1  ty]
 *  [0  0   1]
 * @param tx x方向平移距离
 * @param ty y方向平移距离
 */
fun getTranslateMatrix(tx: Float, ty: Float): Matrix {
    return Matrix(
        1f, 0f, tx,
        0f, 1f, ty
    )
}

/**
 * 将当前矩阵进行平移
 * @param tx x方向平移距离
 * @param ty y方向平移距离
 */
fun Matrix.translate(tx: Float, ty: Float) {
    append(getTranslateMatrix(tx, ty))
}

/**
 * 获取一个缩放矩阵
 * [sx 0  0]
 * [0  sy 0]
 * [0  0  1]
 * @param scaleX x方向缩放大小
 * @param scaleY y方向缩放大小
 */
fun getScaleMatrix(scaleX: Float, scaleY: Float): Matrix {
    return Matrix(
        scaleX, 0f, 0f,
        0f, scaleY, 0f
    )
}

/**
 * 将当前矩阵进行缩放
 * @param scaleX x方向缩放大小
 * @param scaleY y方向缩放大小
 */
fun Matrix.scale(scaleX: Float, scaleY: Float) {
    append(getScaleMatrix(scaleX, scaleY))
}

/**
 * 获取一个旋转矩阵
 * [ cosθ -sinθ  0 ]
 * [ sinθ  cosθ  0 ]
 * [  0     0    1 ]
 * @param rotation 旋转角度
 */
fun getRotateMatrix(rotation: Float): Matrix {
    val cos = cos(rotation)
    val sin = sin(rotation)
    return Matrix(
        cos, -sin, 0f,
        sin, cos, 0f
    )
}

/**
 * 将当前矩阵旋转
 * @param rotation 旋转角度
 */
fun Matrix.rotate(rotation: Float){
    append(getRotateMatrix(rotation))
}