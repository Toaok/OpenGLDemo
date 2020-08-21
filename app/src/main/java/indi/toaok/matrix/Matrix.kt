package indi.toaok.matrix

class Matrix(
    var a: Float = 1f, var c: Float = 0f, var tx: Float = 0f,
    var b: Float = 0f, var d: Float = 1f, var ty: Float = 0f
) {

    //矩阵乘法的规矩，默认用的是左乘
    fun multiply(matrix: Matrix): Matrix {
        val newMatrix = Matrix()
        newMatrix.a = matrix.a * this.a + matrix.c * this.b
        newMatrix.b = matrix.b * this.a + matrix.d * this.b
        newMatrix.c = matrix.a * this.c + matrix.c * this.d
        newMatrix.d = matrix.b * this.c + matrix.d * this.d
        newMatrix.tx = matrix.a * this.tx + matrix.c * this.ty + matrix.tx
        newMatrix.ty = matrix.b * this.tx + matrix.d * this.ty + matrix.ty
        return newMatrix
    }

    /**
     * 矩阵左乘，修改当前矩阵数据
     * @param matrix 需要跟当前矩阵相乘的矩阵对象
     *
     */
    fun append(matrix: Matrix) {
        copyFrom(multiply(matrix))
    }

    /**
     * 矩阵右乘，修改当前矩阵
     * @param matrix 需要跟当前矩阵相乘的矩阵对象
     */
    fun prepend(matrix: Matrix) {
        copyFrom(matrix.multiply(this))
    }


    /**
     * 当前矩阵对一个点进行转换，并返回新的点对象
     * @param p 点对象
     */
    fun transfromPoint(p: Point): Point {
        val newPoint = Point()
        newPoint.x = this.a * p.x + this.c * p.y + this.tx
        newPoint.y = this.b * p.x + this.d * p.y + this.ty
        return newPoint
    }

    /**
     * 进行求逆运算，会修改当前矩阵的数据
     *
     */
    fun invert(): Matrix {
        val a = this.a
        val b = this.b
        val c = this.c
        val d = this.d
        val tx = this.tx
        val ty = this.ty
        val D = a * d - b * c
        this.a = d / D
        this.b = -b / D
        this.c = -c / D
        this.d = a / D
        this.tx = (c * ty - d * tx) / D
        this.ty = (b * tx - a * ty) / D
        return this
    }

    /**
     * 从指定矩阵中复制数据
     * @param matrix 被复制矩阵对象
     */
    private fun copyFrom(matrix: Matrix) {
        this.a = matrix.a
        this.b = matrix.b
        this.c = matrix.c
        this.d = matrix.d
        this.tx = matrix.tx
        this.ty = matrix.ty
    }

    /**
     * 返回当前矩阵的一个副本
     */
    fun clone(): Matrix {
        return Matrix(
            this.a, this.c, this.tx,
            this.b, this.d, this.ty
        )
    }
}