package indi.toaok.matrix


data class Point(var x: Float, var y: Float) {
    constructor():this(0f,0f)
    constructor(x: Int = 0, y: Int = 0) : this(x.toFloat(), y.toFloat())
}