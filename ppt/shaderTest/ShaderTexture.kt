package com.shaderTest

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ShaderTexture : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_shader, container,  false)
        val glSurfaceView = rootView.findViewById<GLSurfaceView>(R.id.glsurfaceview)
        // 设置RGBA颜色缓冲、深度缓冲及stencil缓冲大小
        // Set the size of RGBA、depth and stencil buffer
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0)
        // 设置GL版本，这里设置为2.0
        // Set GL version, here I set it to 2.0
        glSurfaceView.setEGLContextClientVersion(2)
        // 设置对应sample的渲染器
        // Set the corresponding sample renderer
        glSurfaceView.setRenderer(ShaderTextureRenderer())
        return rootView
    }
}