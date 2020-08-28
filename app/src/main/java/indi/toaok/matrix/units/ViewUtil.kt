package indi.toaok.matrix.units

import android.view.View

fun View.dp2px(dpValue: Float): Float {
    val scale = context.resources.displayMetrics.density
    return dpValue * scale + 0.5f
}