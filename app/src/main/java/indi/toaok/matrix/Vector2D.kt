package indi.toaok.matrix

import kotlin.math.sqrt

class Vector2D(var x: Float, var y: Float) {
    constructor() : this(0f, 0f)
    constructor(x: Int = 0, y: Int = 0) : this(x.toFloat(), y.toFloat())

    fun clone(): Vector2D {
        return Vector2D(this.x, this.y)
    }

    /**
     * 向量长度(模)
     */
    var length: Float = sqrt(x * x + y * y)
        set(value) {
            if (field > 0) {
                this.x *= value / field
                this.y *= value / field
            }
        }

    /**
     * 向量单位化
     *
     */
    fun normalize() {
        length = 1f
    }

    /**
     * 向量加法
     */
    fun add(vector2D: Vector2D): Vector2D {
        return Vector2D(this.x + vector2D.x, this.y + vector2D.y)
    }

    /**
     * 向量减法
     */
    fun subtract(vector2D: Vector2D): Vector2D {
        return Vector2D(this.x - vector2D.x, this.y - vector2D.y)
    }

    /**
     * 向量乘法
     */
    fun subtract(num: Float): Vector2D {
        return Vector2D(this.x * num, this.y * num)
    }

    /**
     * 向量点积
     */
    fun dot(vector2D: Vector2D): Float {
        return this.x * vector2D.x + this.y * vector2D.y
    }

    /**
     * 向量叉积
     */
    fun cross(vector2D: Vector2D): Float {
        return this.x * vector2D.y - this.y * vector2D.y
    }
}
