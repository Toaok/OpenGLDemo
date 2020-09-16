package indi.toaok.matrix.mode


class Point(var x: Float, var y: Float) {
    constructor() : this(0f, 0f)
    constructor(x: Int = 0, y: Int = 0) : this(x.toFloat(), y.toFloat())
    constructor(x: Double = 0.0, y: Double = 0.0) : this(x.toFloat(), y.toFloat())
}
