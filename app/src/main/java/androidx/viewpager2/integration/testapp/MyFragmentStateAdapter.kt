package androidx.viewpager2.integration.testapp

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Tanzhenxing
 * Date: 2021/4/17 11:52 上午
 * Description:可复用fragment的adapter
 */
class MyFragmentStateAdapter(val data: List<Int>, fragment: Fragment) : FragmentStateAdapter(fragment){
    private val fragments = mutableMapOf<Int, Fragment>()
    override fun createFragment(position: Int): Fragment {
        val value = data[position]
        val fragment = fragments[value]
        if (fragment != null) {
            Log.d("tanzhenxing", "createFragment:cache:$value")
            return fragment
        }
        Log.d("tanzhenxing", "createFragment:nocache:$value")
        val cardFragment =
            NestedAllRecyclerViewFragment.create(value)
        fragments[value] = cardFragment
        return cardFragment
    }

    /**
     * 根据数据生成唯一id
     *
     * 如果不重写，那么在调用[notifyDataSetChanged]更新的时候
     *
     * 会抛出```new IllegalStateException("Fragment already added")```异常
     */
    override fun getItemId(position: Int): Long {
        return data[position].toLong()
    }

    /**
     * 用来判断当前id对应的fragment是否添加过
     */
    override fun containsItem(itemId: Long): Boolean {
        data.forEach {
            if (it.toLong() == itemId) {
                return true
            }
        }
        return false
    }

    override fun getItemCount(): Int {
        return data.size
    }
}