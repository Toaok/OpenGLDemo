package indi.toaok.opengl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import indi.toaok.opengl.vo.ItemMainBean

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var layoutManager: LinearLayoutManager? = null
    private val recyclerData = ArrayList<ItemMainBean<*>>()
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
        recyclerData.add(ItemMainBean("SimpleHelloWord",SimpleHelloWorldActivity::class.java))
    }

    private fun initEvent() {
        adapter?.let { adapter ->
            adapter.setOnItemClickListener { adapter, view, position ->
                startActivity(Intent(this,recyclerData[position].targetActivity))
            }
        }
    }

    class Adapter(layoutResId: Int, data: MutableList<ItemMainBean<*>>?) :
        BaseQuickAdapter<ItemMainBean<*>, BaseViewHolder>(layoutResId, data) {
        override fun convert(holder: BaseViewHolder, item: ItemMainBean<*>) {
            holder.setText(R.id.button, item.text)
        }
    }

}
