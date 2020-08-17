package indi.toaok.opengl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 *
 * @author user
 * @version 1.0  2020/8/17.
 */
class SimpleHelloWorld:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun initView() {
        initWidget()

    }

    private fun initWidget() {

    }



    private fun initData() {

    }

    private fun initEvent() {

    }
}