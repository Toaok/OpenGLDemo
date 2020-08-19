package indi.toaok.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SimpleHelloWorldActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello_world)
        init()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun initView() {
        initWidget()
        initGLSurfaceView()
    }

    private fun initWidget() {
        glSurfaceView = findViewById(R.id.gl_surface_view)
    }

    private fun initGLSurfaceView() {
        //设置ARGB颜色缓冲区、深度缓冲及stencil(模板)缓冲大小
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        //设置GL版本，这里设置为2.0
        glSurfaceView.setEGLContextClientVersion(2)
        //设置Render
        glSurfaceView.setRenderer(SimpleHelloWorldRender())

        glSurfaceView.renderMode=GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }


    private fun initData() {

    }

    private fun initEvent() {

    }

    class SimpleHelloWorldRender : GLSurfaceView.Renderer {
        var glSurfaceViewWidth = 0
        var glSurfaceViewHeight = 0

        private val vertexShaderCode =
            "precision mediump float;\n" +
                    "attribute vec4 a_Position;\n" +
                    "void main(){\n" +
                    "   gl_Position=a_Position;\n" +
                    "}"

        private val fragmentShaderCode =
            "precision mediump float;\n" +//精确度
                    "void main(){\n" +
                    "   gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);\n" +
                    "}"
        override fun onDrawFrame(gl: GL10?) {
            //设置清屏颜色
            GLES20.glClearColor(0.9f,0.9f,0.9f,1f)
            //清屏
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            //设置视口
            GLES20.glViewport(0,0,glSurfaceViewWidth,glSurfaceViewHeight)
            //调用draw方法用TRIANGLES的方式执行渲染，顶点数量为3个
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            glSurfaceViewWidth = width
            glSurfaceViewHeight = height
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

            //创建GL程序
            val programId = GLES20.glCreateProgram()
            //将shader程序附着到GL程序上
            GLES20.glAttachShader(programId, vertexShader)
            GLES20.glAttachShader(programId, fragmentShader)
            //链接GL程序
            GLES20.glLinkProgram(programId)

            //使用GL程序
            GLES20.glUseProgram(programId)
            //三角形顶点数据
            val vertexData= floatArrayOf(-1f,0.5f,-0.5f,1f,0f,0f,0f,0f,0.5f,-1f,1f,-0.5f)
            //将顶点数据方如buffer中
            val buffer=ByteBuffer.allocateDirect(vertexData.size*java.lang.Float.SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            buffer.put(vertexData)
            buffer.position(0)
            //获取字段a_Position在shader中的位置
            val location=GLES20.glGetAttribLocation(programId,"a_Position")
            //启动对应位置的参数
            GLES20.glEnableVertexAttribArray(location)
            //指定a_Position所使用的顶点数据
            GLES20.glVertexAttribPointer(location,2,GLES20.GL_FLOAT,false,0,buffer)
        }

    }

}