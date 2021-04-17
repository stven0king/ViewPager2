package androidx.viewpager2.integration.testapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.integration.testapp.widget.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout

/**
 * Created by Tanzhenxing
 * Date: 2021/4/17 11:43 上午
 * Description:测试吸顶+Fragment左右滑动+（页面内部横滑和竖滑列表）
 */
class MyFragment : BaseFragment() {
    private lateinit var tabLayout: TabLayout
    private var tabViewList = mutableListOf<View>()
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: FragmentStateAdapter
    private val data = mutableListOf(11, 12, 13)

    init {
        TAG = "tanzhenxing:MyFragment"
    }
    var num = 3
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val itemView = inflater.inflate(R.layout.activity_transfer_header, container, false)
        itemView.findViewById<View>(R.id.iv).setOnClickListener { view ->
            data.shuffle()
            data.removeAt(0)
            data.add(10 + num)
            num++
            adapter.notifyDataSetChanged()
        }
        adapter = MyFragmentStateAdapter(data, this)
        viewPager = itemView.findViewById<ViewPager2>(R.id.viewpager).apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = this@MyFragment.adapter
        }
        tabLayout = itemView.findViewById(R.id.tabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.customView = LinearLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                orientation = LinearLayout.VERTICAL
            }
            (tab.customView as LinearLayout).setPadding(0, 20, 0, 20)
            val textView = TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                text = "top==${data[position]}"
            }
            (tab.customView as LinearLayout).addView(textView)
            val textView2 = TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                text = "Bottom==${data[position]}"
            }
            tab.customView?.setPadding(10, 0, 10, 0)
            (tab.customView as LinearLayout).addView(textView2)
            tabViewList.add(tab.customView as LinearLayout)
            Log.d("tanzhenxing", "TabLayoutMediator:$position")
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                Log.d("tanzhenxing", "onTabSelected${p0!!.position}")
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                Log.d("tanzhenxing", "onTabUnselected${p0!!.position}")
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
                Log.d("tanzhenxing", "onTabReselected${p0!!.position}")
            }

        })
        val appBarLayout = itemView.findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var lastOffset = 0
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
                //Log.d("tanzhenxing", "onOffsetChanged $p1")
                //if (p1 == lastOffset) {
                //    return
                //}
                //lastOffset = p1
                //if (p1 >= -495) {
                //    val topMargin = if (p1 > -455) 0 else 495 + p1 - 30
                //    for (i in 0..tabLayout.tabCount) {
                //        tabLayout.getTabAt(i)?.view?.apply {
                //            setBackgroundColor(Color.BLUE)
                //            val linearLayout = tab!!.customView as LinearLayout
                //            //(linearLayout.getChildAt(1).layoutParams as LinearLayout.LayoutParams).topMargin = topMargin
                //            tab!!.customView?.requestLayout()
                //        }
                //    }
                //    (tabLayout.layoutParams as LinearLayout.LayoutParams).topMargin = topMargin
                //}
                //if (p1 == 0) {
                //    for (i in 0..tabLayout.tabCount) {
                //        tabLayout.getTabAt(i)?.view?.apply {
                //            setBackgroundColor(Color.YELLOW)
                //            val linearLayout = tab!!.customView as LinearLayout
                //            tab!!.customView = tab!!.customView
                //            tab!!.customView?.requestLayout()
                //        }
                //    }
                //    (tabLayout.layoutParams as LinearLayout.LayoutParams).topMargin = 0
                //}
            }

        })
        return itemView
    }
}