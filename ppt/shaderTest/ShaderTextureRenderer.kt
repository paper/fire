package com.shaderTest

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.shaderTest.Util
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class ShaderTextureRenderer : GLSurfaceView.Renderer {


// 原图展示顶点着色器
//    precision mediump float;
//    attribute vec4 a_position;
//    attribute vec2 a_textureCoordinate;
//    varying vec2 v_textureCoordinate;
//    void main() {
//        v_textureCoordinate = a_textureCoordinate;
//        gl_Position = a_position;
//    }
    private val vertexShaderCode =
                "precision mediump float;\n" +
                "attribute vec4 a_position;\n" +
                "attribute vec2 a_textureCoordinate;\n" +
                "varying vec2 v_textureCoordinate;\n" +
                "void main() {\n" +
                "    v_textureCoordinate = a_textureCoordinate;\n" +
                "    gl_Position = a_position;\n" +
                "}"

// 原图展示片段着色器
//    precision mediump float;
//    varying vec2 v_textureCoordinate;
//    uniform sampler2D u_texture;
//    void main() {
//        gl_FragColor = texture2D(u_texture, v_textureCoordinate);
//    }
    private val fragmentShaderCode =
            "precision mediump float;\n" +
                    "varying vec2 v_textureCoordinate;\n" +
                    "uniform sampler2D noiseTexture;\n" +
                    "uniform float iTime;\n" +
                    "void main() {\n" +

                    "float fireImpetus = 0.1;\n" +
                    "float fireImpetusInner = 0.1;\n" +
                    "vec2 uv = vec2( v_textureCoordinate.x *1080./2300. ,v_textureCoordinate.y);\n" +
                    "vec4 burnColorOut = vec4( 0.92,0.09,0.09,1.);\n" +
                    "vec4 burnColorInner = vec4( 0.86,0.443,0.086,1.);\n" +
                    // 让uv的y动起来
                    "    uv.y += iTime;\n" +
                    // uv.y 是一个0～1循环的噪点变量，作用是让火苗循环运动
                    "    uv.y = mod(uv.y , 1.0);\n" +
                    // 噪点图取样r,取样坐标随着y周期性改变
                    "    float noiseValue = texture2D(noiseTexture, uv).r;\n" +
                    // 渲染范围  smooth整个图片 v_textureCoordinate.y
                    "    float p1 = smoothstep(0.0,1.0,v_textureCoordinate.y - fireImpetus);\n" +
                    // 颜色1 程度
                    "    float f1 = step(noiseValue,p1);\n" +
                    // 内色 程度，比颜色1少fireImpetusInner的边，但是要做smoothstep，保证颜色衔接平滑
                    "    float f2 = smoothstep(noiseValue,p1 ,p1 - fireImpetusInner);\n" +
                    // 颜色1 = 内色 + r1
                    "    float r1 = f1 - f2;\n" +
                    "    float r2 = f2;\n" +
                    "    gl_FragColor = burnColorOut * r1 +  burnColorInner * r2;"+
                    "}"
    // GLSurfaceView的宽高
    // The width and height of GLSurfaceView
    private var glSurfaceViewWidth = 0
    private var glSurfaceViewHeight = 0
    private var __programId = 0

    private var time = 0f

    // 纹理顶点数据
    private val vertexData = floatArrayOf(-1f, -1f, -1f, 1f, 1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)
    private val VERTEX_COMPONENT_COUNT = 2
    private lateinit var vertexDataBuffer : FloatBuffer

    // 纹理坐标
    // The texture coordinate
    private val textureCoordinateData = floatArrayOf(0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 1f, 0f, 1f, 1f)
    private val TEXTURE_COORDINATE_COMPONENT_COUNT = 2
    private lateinit var textureCoordinateDataBuffer : FloatBuffer


    override fun onDrawFrame(gl: GL10?) {

        // 设置清屏颜色
        GLES20.glClearColor(0f, 0f, 0f, 0f)

        // 清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val timeLoaction = GLES20.glGetUniformLocation(__programId, "iTime")
        GLES20.glUniform1f(timeLoaction, time)

        // 设置视口，这里设置为整个GLSurfaceView区域
        GLES20.glViewport(0, 0, glSurfaceViewWidth, glSurfaceViewHeight)

        // 调用draw方法用TRIANGLES的方式执行渲染，顶点数量为3个
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexData.size / VERTEX_COMPONENT_COUNT)
        time = (time + 0.02f)%1.0f
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 记录GLSurfaceView的宽高
        // Record the width and height of the GLSurfaceView
        glSurfaceViewWidth = width
        glSurfaceViewHeight = height

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        // 创建GL程序
        val programId = GLES20.glCreateProgram()

        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        val fragmentShader= GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        // 加载顶点着色器代码
        GLES20.glShaderSource(vertexShader, vertexShaderCode)
        // 加载片段着色器代码
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode)
        // 编译顶点着色器代码
        GLES20.glCompileShader(vertexShader)
        // 编译片段着色器代码
        GLES20.glCompileShader(fragmentShader)

        // 将shader程序附着到GL程序上
        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)

        // 链接GL程序
        GLES20.glLinkProgram(programId)

        // 应用GL程序
        GLES20.glUseProgram(programId)

        // 将三角形顶点数据放入buffer中
        vertexDataBuffer = ByteBuffer.allocateDirect(vertexData.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexDataBuffer.put(vertexData)
        vertexDataBuffer.position(0)

        // 获取字段a_position在shader中的位置
        val aPositionLocation = GLES20.glGetAttribLocation(programId, "a_position")

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(aPositionLocation)

        // 指定a_position所使用的顶点数据
        GLES20.glVertexAttribPointer(aPositionLocation, VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexDataBuffer)

        // 将纹理坐标数据放入buffer中
        textureCoordinateDataBuffer = ByteBuffer.allocateDirect(textureCoordinateData.size * java.lang.Float.SIZE / 8)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureCoordinateDataBuffer.put(textureCoordinateData)
        textureCoordinateDataBuffer.position(0)

        // 获取字段a_textureCoordinate在shader中的位置
        val aTextureCoordinateLocation = GLES20.glGetAttribLocation(programId, "a_textureCoordinate")

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(aTextureCoordinateLocation)

        // 指定a_textureCoordinate所使用的顶点数据
        GLES20.glVertexAttribPointer(aTextureCoordinateLocation, TEXTURE_COORDINATE_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, textureCoordinateDataBuffer)

        // 创建图片纹理
        val textures = IntArray(1)
        GLES20.glGenTextures(textures.size, textures, 0)
        val imageTexture = textures[0]

        // 设置纹理参数
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)


        GLES20.glUseProgram(programId)


        // 解码图片并加载到纹理中
        val bitmap = Util.decodeBitmapFromAssets("noise.png")
        val b = ByteBuffer.allocate(bitmap.width * bitmap.height * 4)
        bitmap.copyPixelsToBuffer(b)
        b.position(0)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.width, bitmap.height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, b)
        val uTextureLocation = GLES20.glGetAttribLocation(programId, "u_texture")
        GLES20.glUniform1i(uTextureLocation, 0)



        val timeLoaction = GLES20.glGetUniformLocation(programId, "iTime")
        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(timeLoaction)

        __programId = programId
    }

}