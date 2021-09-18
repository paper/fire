package com.shaderTest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SimpleActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.context = applicationContext
        setContentView(R.layout.activity_shader)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content, ShaderTexture())
        transaction.commit()
    }

}
