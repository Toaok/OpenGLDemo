package indi.toaok.opengl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var layoutManager: LinearLayoutManager? = null
    private val recyclerData = ArrayList<String>()
    private var adapter: Adapter? = null
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
        initRecyclerView()
    }

    private fun initWidget() {
        recyclerView = findViewById(R.id.recycler_view)
    }

    private fun initRecyclerView() {
        if (recyclerData.isNotEmpty()) {
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
            if (adapter == null) {
                adapter = Adapter(R.layout.item_main_list, recyclerData)
            }
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    private fun initData() {
        recyclerData.add("SimpleHelloWord")
    }

    private fun initEvent() {

    }

    class Adapter(layoutResId: Int, data: MutableList<String>?) :
        BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {
        override fun convert(holder: BaseViewHolder, text: String) {
            holder.setText(R.id.button, text)
        }
    }

}
