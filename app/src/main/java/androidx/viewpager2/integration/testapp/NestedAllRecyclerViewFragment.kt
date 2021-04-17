package androidx.viewpager2.integration.testapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.integration.testapp.view.RecyclerViewAtViewPager2

/**
 * Created by Tanzhenxing
 * Date: 2021/4/17 11:44 上午
 * Description:
 * 用于[androidx.viewpager2.widget.ViewPager2]搭配[androidx.viewpager2.adapter.FragmentStateAdapter]
 * 实现对[Fragment]的复用，以及页面数据的懒加载
 */
class NestedAllRecyclerViewFragment(val position: Int) : BaseFragment() {
    init {
        TAG = "tanzhenxing:$position"
    }

    /**
     * 标识是否第一次创建
     * 通过 [NestedAllRecyclerViewFragment.onResume] 和该标识结合在一起触发。
     */
    private var isFirstShow = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("tanzhenxing", "onCreateView:$position ${hashCode()}")
        val itemView =
            inflater.inflate(R.layout.item_nested_all_recyclerviews, container, false)
        val text: TextView = itemView.findViewById(R.id.text)
        text.text = "position=$position"
        val rv1: RecyclerView = itemView.findViewById(R.id.recycle_view)
        rv1.setUpRecyclerView(RecyclerView.VERTICAL, this)
        itemView.setBackgroundResource(PAGE_COLORS[position % PAGE_COLORS.size])
        return itemView
    }


    override fun onResume() {
        super.onResume()
        if (isFirstShow) {
            isFirstShow = false
            initData()
        }
        onUserVisible()
    }

    fun initData() {
        //加载第一页数据
    }


    override fun onPause() {
        super.onPause()
        onUserGone()
    }

    /**
     * 对于用户显示
     */
    protected fun onUserVisible() {
    }

    /**
     * 对于用户隐藏
     */
    protected fun onUserGone() {
    }

    companion object {
        fun create(position: Int): NestedAllRecyclerViewFragment {
            return NestedAllRecyclerViewFragment(position)
        }
    }

    class RvAdapter(private val orientation: Int, private val fragment: Fragment) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int {
            return 40
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == 1) {
                val recyclerView = RecyclerViewAtViewPager2(parent.context)
                    .apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                recyclerView.layoutManager =
                    LinearLayoutManager(parent.context, RecyclerView.HORIZONTAL, false)
                recyclerView.adapter =
                    ParallelNestedScrollingActivity.RvAdapter(RecyclerView.HORIZONTAL)
                return RecycleViewHolder(recyclerView)
            }
            val tv = TextView(parent.context)
            tv.layoutParams = matchParent().apply {
                if (orientation == RecyclerView.HORIZONTAL) {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
            tv.textSize = 20f
            tv.gravity = Gravity.CENTER
            tv.setPadding(20, 55, 20, 55)
            tv.setOnClickListener {
                fragment.startActivityForResult(
                    Intent(
                        fragment.context,
                        ParallelNestedScrollingActivity::class.java
                    ), 789
                )
            }
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ViewHolder) {
                with(holder) {
                    tv.text = tv.context.getString(R.string.item_position, adapterPosition)
                    tv.setBackgroundResource(CELL_COLORS[position % CELL_COLORS.size])
                }
            }
            if (holder is RecycleViewHolder) {
                with(holder) {

                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            if (position == 0) {
                return 1
            }
            return super.getItemViewType(position)
        }

        class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)
        class RecycleViewHolder(val recyclerView: RecyclerView) :
            RecyclerView.ViewHolder(recyclerView)
    }
}
private fun RecyclerView.setUpRecyclerView(orientation: Int, fragment: Fragment) {
    layoutManager = LinearLayoutManager(context, orientation, false)
    adapter = NestedAllRecyclerViewFragment.RvAdapter(orientation, fragment)
}