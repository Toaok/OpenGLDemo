package indi.toaok.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView

fun GLSurfaceView.Renderer.loadShader(type: Int, shaderCode: String): Int {
    val shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)
    return shader
}
